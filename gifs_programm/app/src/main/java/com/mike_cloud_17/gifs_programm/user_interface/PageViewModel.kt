package com.mike_cloud_17.gifs_programm.user_interface

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mike_cloud_17.gifs_programm.request_work.NetworkService
import com.mike_cloud_17.gifs_programm.req_classes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PageViewModel : ViewModel() {
    var pageSection: PageSection = PageSection.RANDOM

    val gifState: LiveData<State<Gif?>>
        get() = _gifState

    val panel: LiveData<Panel>
        get() = _panel

    val infoPanel: LiveData<Boolean>
        get() = _infoPanel

    private val _gifState = MutableLiveData<State<Gif?>>()

    private var _panel: MutableLiveData<Panel> = MutableLiveData<Panel>().apply {
        this.postValue(Panel(backButton = false, nextButton = false, refreshButton = false))
    }

    private var currentIndex = -1

    private val _infoPanel = MutableLiveData<Boolean>()

    private var totalCount = -1

    private var currentPage = 1

    private val loadedGIFs = mutableListOf<Gif>()

    init {
        Timber.plant(Timber.DebugTree())
    }

    fun initLoad() {
        if (isFirstLoad()) {
            Timber.d("First loading...")
            nextGif()
        }
    }

    fun refresh() {
        if (_panel.value?.refreshButton == true) {
            loadGifByPageSection()
        }
    }

    fun doInfoPanel(){
        if(_infoPanel.value != true){
            _infoPanel.postValue(true)
        }
        else{
            _infoPanel.postValue(false)
        }
    }

    fun nextGif() {
        if (hasNext() || isFirstLoad()) {
            Timber.d("Load a next gif...")
            if (needLoadGif()) {
                loadGifByPageSection()
            } else {
                currentIndex += 1
                postPanel(backButton = !isFirstCurrentGIF(), nextButton = hasNext())
                postState(Event.SUCCESS, gif = loadedGIFs.elementAt(currentIndex))
            }
        } else {
            Timber.w("Unable to upload next gif.")
        }
    }

    fun prevGif() {
        if (!isFirstCurrentGIF()) {
            Timber.d("Load a previous gif...")
            if (_panel.value?.refreshButton == false) {
                currentIndex -= 1
            }
            postPanel(!isFirstCurrentGIF(), true)
            postState(Event.SUCCESS, throwable = null, gif = loadedGIFs.elementAt(currentIndex))
        } else {
            Timber.w("Unable to upload previous gif.")
        }
    }

    private fun onFailed(throwable: Throwable) {
        Timber.e(throwable)

        postPanel(backButton = !isFirstCurrentGIF(), refreshButton = true)
        postState(Event.ERROR, throwable)
    }

    private fun onSuccess() {
        currentIndex += 1
        currentPage += 1

        Timber.i("Successful load. New element by index: $currentIndex")

        postPanel(backButton = !isFirstCurrentGIF(), nextButton = hasNext())
        postState(Event.SUCCESS, gif = loadedGIFs.elementAt(currentIndex))
    }

    private fun loadRandomGif() {
        if (pageSection != PageSection.RANDOM) {
            Timber.e(
                "Random gif uploads; section = %s",
                pageSection
            )
        }

        return NetworkService.retrofitService().getRandomGif().enqueue(object : Callback<Gif> {
            override fun onResponse(call: Call<Gif>, response: Response<Gif>) {
                val randomGif = response.body()

                if (response.code() == 200 && randomGif != null) {
                    loadedGIFs.add(randomGif)
                    onSuccess()
                } else {
                    onFailed(Throwable("Server respond: ${response.code()}; randomGif = $randomGif"))
                }
            }

            override fun onFailure(call: Call<Gif>, t: Throwable) {
                onFailed(t)
            }
        })
    }

    private fun loadGIFs() {
        if (pageSection == PageSection.RANDOM) {
            Timber.e(
                "Random gifs upload"
            )
        }

        return NetworkService.retrofitService()
            .getSectionGIFs(section = pageSection.toString(), page = currentPage)
            .enqueue(object : Callback<ResponseWrapper> {
                override fun onResponse(
                    call: Call<ResponseWrapper>,
                    response: Response<ResponseWrapper>
                ) {
                    val responseWrapper = response.body()
                    if (response.code() == 200 && responseWrapper != null &&
                        !responseWrapper.result.isNullOrEmpty()
                    ) {
                        loadedGIFs.addAll(responseWrapper.result)
                        totalCount = responseWrapper.totalCount
                        onSuccess()
                    } else {
                        onFailed(
                            Throwable(
                                "Server respond: ${response.code()}; " + "res = ${responseWrapper?.result}"
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
                    onFailed(t)
                }
            })
    }

    private fun loadGifByPageSection() {
        _panel.postValue(Panel(backButton = false, nextButton = false, refreshButton = false))
        postState(Event.LOADING, throwable = null, gif = null)

        return when (pageSection) {
            PageSection.RANDOM -> loadRandomGif()
            else -> loadGIFs()
        }
    }

    private fun needLoadGif(): Boolean {
        return loadedGIFs.isEmpty() || loadedGIFs.size - currentIndex <= 1
    }

    private fun isFirstCurrentGIF(): Boolean {
        return currentIndex <= 0
    }

    private fun isFirstLoad(): Boolean {
        return currentIndex == -1
    }

    private fun hasNext(): Boolean {
        return when (pageSection) {
            PageSection.RANDOM -> loadedGIFs.isNotEmpty()
            else -> loadedGIFs.isNotEmpty() && totalCount - currentIndex > 1
        }
    }

    private fun postState(event: Event, throwable: Throwable? = null, gif: Gif? = null) {
        Timber.d(
            "Post was been updated. Event = %s, Throwable = %s, Data = %s",
            event, throwable.toString(), gif
        )

        _gifState.postValue(
            State(
                event = event,
                throwable = throwable,
                data = gif
            )
        )
    }

    private fun postPanel(
        backButton: Boolean = false,
        nextButton: Boolean = false,
        refreshButton: Boolean = false
    ) {
        Timber.d(
            "Panel was been updated. Back Button = %s, Next Button = %s, Refresh Button = %s",
            backButton, nextButton, refreshButton
        )

        _panel.postValue(
            Panel(
                backButton = backButton,
                nextButton = nextButton,
                refreshButton = refreshButton
            )
        )
    }

}