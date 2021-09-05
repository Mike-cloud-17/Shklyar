package com.mike_cloud_17.gifs_programm.user_interface

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mike_cloud_17.gifs_programm.R
import com.mike_cloud_17.gifs_programm.request_work.EventObserver
import com.mike_cloud_17.gifs_programm.req_classes.Event
import com.mike_cloud_17.gifs_programm.req_classes.Gif
import com.mike_cloud_17.gifs_programm.req_classes.PageInfo
import com.mike_cloud_17.gifs_programm.databinding.FragmentPageBinding
import com.mike_cloud_17.gifs_programm.request_work.RequestDrawable
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class PageFragment : Fragment(), EventObserver<Gif> {
    private val messageErrorSnackBarText by lazy {
        binding.root.resources.getString(R.string.error_snackbar_text)
    }

    private val messageErrorGifText by lazy {
        binding.root.resources.getString(R.string.error_gif_text)
    }

    private var gifAnimateDuration: Int = DEFAULT_ANIMATE_DURATION

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pageInfo = arguments?.getParcelable<PageInfo>(ARG_PAGE_INFO)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            pageInfo?.let {
                this.pageSection = it.pageSection
            }
        }
        Timber.plant(Timber.DebugTree())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(layoutInflater)

        pageViewModel.gifState.observe(viewLifecycleOwner) {
            when (it.event) {
                Event.ERROR -> onError(it.throwable)
                Event.SUCCESS -> onSuccess(it.data!!)
                Event.LOADING -> onLoading()
            }
        }

        pageViewModel.panel.observe(viewLifecycleOwner) {
            binding.fabBack.isEnabled = it.backButton
            binding.fabNext.isEnabled = it.nextButton
            binding.fabRefresh.isEnabled = it.refreshButton
        }

        binding.cardView.animate().alpha(0.0f)
        pageViewModel.infoPanel.observe(viewLifecycleOwner){
            if(it){
                binding.cardView.animate().alpha(0.6f)
            }
            else{
                binding.cardView.animate().alpha(0.0f)
            }
        }

        binding.fabBack.setOnClickListener { pageViewModel.prevGif() }
        binding.fabNext.setOnClickListener { pageViewModel.nextGif() }
        binding.fabRefresh.setOnClickListener { pageViewModel.refresh() }
        binding.fabInfo?.setOnClickListener{pageViewModel.doInfoPanel()}
        pageViewModel.initLoad()

        Timber.d("onCreateView was been successful")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        gifAnimateDuration = PreferenceManager.getDefaultSharedPreferences(binding.root.context)
            .getInt("duration", DEFAULT_ANIMATE_DURATION)

    }

    override fun onSuccess(data: Gif) {
        binding.textViewDescription.text = data.description
        "Data: ${data.date}".also { binding.textViewDate.text = it }
        "Author: ${data.author}".also { binding.textViewAuthor.text = it }
        binding.progressBar.visibility = View.VISIBLE

        Glide.with(binding.root)
            .load(data.gifUrl)
            .transition(GenericTransitionOptions.with { view ->
                view.alpha = 0f
                val fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
                fadeAnim.duration = gifAnimateDuration.toLong()
                fadeAnim.start()
            })
            .diskCacheStrategy(CACHE_STRATEGY)
            .thumbnail(Glide.with(binding.root).load(data.previewUrl))
            .listener(RequestListenerImpl(object : RequestDrawable {
                override fun onLoadFailed() {
                    binding.progressBar.visibility = View.GONE
                    showErrorSnackBar()
                }

                override fun onResourceReady() {
                    binding.progressBar.visibility = View.GONE
                }
            }))
            .error(R.drawable.error_cattt)
            .centerCrop()
            .into(binding.imageViewGif)
    }

    override fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onError(throwable: Throwable?) {
        if(throwable == null){
            Timber.w("Throwable is null.")
        }
        else{
            Timber.d(throwable)
        }

        Glide.with(binding.root).load(R.drawable.error_cattt).centerCrop()
            .into(binding.imageViewGif)

        binding.textViewDescription.text = messageErrorGifText
        binding.textViewDate.text = ""
        binding.textViewAuthor.text = ""
        binding.progressBar.visibility = View.GONE

        showErrorSnackBar()
    }

    private fun showErrorSnackBar() {
        Timber.d("Was called showErrorSnackBar.")

        if (isVisible) {
            Snackbar.make(
                binding.root,
                messageErrorSnackBarText,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val ARG_PAGE_INFO = "page_info"

        @JvmStatic
        fun newInstance(
            pageInfo: Parcelable
        ): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAGE_INFO, pageInfo)
                }
            }
        }

        const val DEFAULT_ANIMATE_DURATION: Int = 3500

        val CACHE_STRATEGY: DiskCacheStrategy = DiskCacheStrategy.ALL
    }
}