package com.example.kinopoisk_test.presentation.fragments.main

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kinopoisk_test.R
import com.example.kinopoisk_test.databinding.FragmentTopFilmPreviewListBinding
import com.example.kinopoisk_test.presentation.fragments.BaseFragment
import com.example.kinopoisk_test.presentation.fragments.main.recycler.PaginationListener
import com.example.kinopoisk_test.presentation.fragments.main.recycler.TopFilmPreviewListAdapter
import com.example.kinopoisk_test.presentation.model.TopFilmPreviewVo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


class TopFilmPreviewListFragment :
    BaseFragment<FragmentTopFilmPreviewListBinding, TopFilmPreviewListViewModel>() {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTopFilmPreviewListBinding
        get() = FragmentTopFilmPreviewListBinding::inflate
    override val viewModelClass: Class<TopFilmPreviewListViewModel>
        get() = TopFilmPreviewListViewModel::class.java

    private var isPopular = false

    var isLoading: Boolean = false

    override fun initUI() {
        lifecycleScope.launchWhenStarted {
            if (binding?.startLoading?.root?.isVisible == true) {
                delay(LOADING_DURATION)
            }

            setPopular()
        }

        initRecyclerView()

        activityBinding?.toolBar?.root?.isVisible = false

        activityBinding?.toolBar?.inputSearch?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                (binding?.rcPreviews?.adapter as? TopFilmPreviewListAdapter)?.filterString = str.toString()
                if(isPopular) {
                    setPopular()
                } else {
                    setFavourite()
                }
            }

            override fun afterTextChanged(str: Editable?) {
                //no-op
            }
        })
    }

    private fun initRecyclerView() {
        binding?.rcPreviews?.apply {
            router?.let { r->
                viewModel?.let { vm ->
                    adapter = TopFilmPreviewListAdapter(arrayListOf(), r, vm)
                }
            }
            val lManager = LinearLayoutManager(requireContext()).also {
                layoutManager = it
            }
            addOnScrollListener(object : PaginationListener(lManager) {
                override fun isLastPage(): Boolean = viewModel?.isLastPage ?: true

                override fun isLoading(): Boolean = isLoading

                override fun loadMoreItems() {
                    isLoading = true
                    viewModel?.getTopFilmList()
                }

            })
        }
    }

    override fun initObservers() {
        viewModel?.let { viewmodel ->
            lifecycleScope.launchWhenCreated {
                viewmodel.isError.collectLatest { isError ->
                    if (isError) {
                        showErrorScreen()
                        closeLoadingScreen()
                        closeOtherViews()
                    } else {
                        closeErrorScreen()
                    }
                }
            }
            lifecycleScope.launchWhenCreated {
                viewmodel.isLoading.collectLatest { isLoading ->
                    if (isLoading) {
                        showLoadingScreen()
                        closeErrorScreen()
                        binding?.startLoading?.root?.isVisible = false
                    } else {
                        closeErrorScreen()
                    }
                }
            }
            lifecycleScope.launchWhenCreated {
                viewmodel.filmList.collectLatest { filmList ->
                    if (binding?.startLoading?.root?.isVisible == false || filmList.isNotEmpty()) {
                        closeLoadingScreen()
                        closeErrorScreen()
                        binding?.startLoading?.root?.isVisible = false
                        bindPreviewUi(filmList)
                    }
                }
            }
        }
    }

    private fun bindPreviewUi(filmList: List<TopFilmPreviewVo>) {
        binding?.apply {
            (rcPreviews.adapter as? TopFilmPreviewListAdapter)?.setData(filmList)

            rcPreviews.isVisible = true
            btChooseFavourite.isVisible = true
            btChoosePopular.isVisible = true
        }

        activityBinding?.toolBar?.txToolbarTitle?.text = if (isPopular) {
            POPULAR
        } else {
            FAVOURITE
        }
        activityBinding?.toolBar?.root?.isVisible = true

        isLoading = false

        binding?.imNotFound?.isVisible = (binding?.rcPreviews?.adapter as? TopFilmPreviewListAdapter)?.previews?.isEmpty() ?: false
    }

    override fun initButtons() {
        binding?.apply {
            btChoosePopular.setOnClickListener {
                if (!isPopular) {
                    setPopular()
                }
            }
            btChooseFavourite.setOnClickListener {
                if (isPopular) {
                    setFavourite()
                }
            }
            error.btErrorButton.setOnClickListener {
                setPopular()
            }
        }
        activityBinding?.apply {
            toolBar.btSearch.setOnClickListener {
                toolBar.apply {
                    txToolbarTitle.isVisible = false
                    btBack.isVisible = true
                    btSearch.isVisible = false
                    inputSearch.isVisible = true
                }
            }
            toolBar.btBack.setOnClickListener {
                toolBar.apply {
                    txToolbarTitle.isVisible = true
                    btBack.isVisible = false
                    btSearch.isVisible = true
                    inputSearch.isVisible = false

                    if(isPopular) {
                        setPopular()
                    } else {
                        setFavourite()
                    }

                    inputSearch.setText("")
                    (binding?.rcPreviews?.adapter as? TopFilmPreviewListAdapter)?.filterString = ""
                }
            }
        }
    }

    private fun setPopular() {
        viewModel?.getTopFilmList()
        isPopular = true
        binding?.apply {
            btChooseFavourite.apply {
                background = resources.getDrawable(R.drawable.not_choosed_background)
                setTextColor(resources.getColor(R.color.not_choosed))
            }
            btChoosePopular.apply {
                background = resources.getDrawable(R.drawable.choosed_background)
                setTextColor(resources.getColor(R.color.choosed))
            }
            (rcPreviews.adapter as? TopFilmPreviewListAdapter)?.setData(emptyList())

            swipeView.setOnRefreshListener {
                setPopular()
            }
        }

    }

    private fun setFavourite() {
        viewModel?.getFavouriteList()
        isPopular = false
        binding?.apply {
            btChooseFavourite.apply {
                background = resources.getDrawable(R.drawable.choosed_background)
                setTextColor(resources.getColor(R.color.choosed))
            }
            btChoosePopular.apply {
                background = resources.getDrawable(R.drawable.not_choosed_background)
                setTextColor(resources.getColor(R.color.not_choosed))
            }
            (rcPreviews.adapter as? TopFilmPreviewListAdapter)?.setData(emptyList())

            swipeView.setOnRefreshListener {
                swipeView.isRefreshing = false
            }
        }
    }

    override fun showLoadingScreen() {
        binding?.apply {
            loadingProgBar.isVisible = true
            imNotFound.isVisible = false
        }
    }

    override fun showErrorScreen() {
        binding?.apply {
            error.root.isVisible = true
            imNotFound.isVisible = false
        }
    }

    override fun closeLoadingScreen() {
        binding?.apply {
            loadingProgBar.isVisible = false
            imNotFound.isVisible = false
            swipeView.isRefreshing = false
        }
    }

    override fun closeErrorScreen() {
        binding?.apply {
            error.root.isVisible = false
            imNotFound.isVisible = false
        }
    }

    override fun closeOtherViews() {
        binding?.apply {
            startLoading.root.isVisible = false
            rcPreviews.isVisible = false
            btChooseFavourite.isVisible = false
            btChoosePopular.isVisible = false
            imNotFound.isVisible = false
        }
    }


    companion object {
        private const val LOADING_DURATION = 3000L

        private const val POPULAR = "Популярные"
        private const val FAVOURITE = "Избранное"
    }

}