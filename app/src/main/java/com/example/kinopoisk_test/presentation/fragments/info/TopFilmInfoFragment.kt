package com.example.kinopoisk_test.presentation.fragments.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.kinopoisk_test.R
import com.example.kinopoisk_test.databinding.FragmentTopFilmInfoBinding
import com.example.kinopoisk_test.presentation.fragments.BaseFragment
import com.example.kinopoisk_test.presentation.model.TopFilmInfoVo
import kotlinx.coroutines.flow.collectLatest

class TopFilmInfoFragment(
    private val kinoId: Int
) : BaseFragment<FragmentTopFilmInfoBinding, TopFilmInfoViewModel>() {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTopFilmInfoBinding
        get() = FragmentTopFilmInfoBinding::inflate
    override val viewModelClass: Class<TopFilmInfoViewModel>
        get() = TopFilmInfoViewModel::class.java

    override fun initUI() {
        showLoadingScreen()
        activityBinding?.apply {
            toolBar.root.isVisible = false
        }
    }

    override fun initStartValues() {
        viewModel?.getTopFilmInfo(kinoId)
    }

    override fun initButtons() {
        binding?.apply {
            error.btErrorButton.setOnClickListener {
                viewModel?.getTopFilmInfo(kinoId)
            }
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
                    }
                }
            }
            lifecycleScope.launchWhenCreated {
                viewmodel.isLoading.collectLatest { isLoading ->
                    if (isLoading) {
                        showLoadingScreen()
                        closeErrorScreen()
                    } else {
                        closeLoadingScreen()
                    }
                }
            }
            lifecycleScope.launchWhenCreated {
                viewmodel.filmList.collectLatest { filmInfo ->
                    closeLoadingScreen()
                    closeErrorScreen()
                    bindInfoUi(filmInfo)
                }
            }
        }
    }

    private fun bindInfoUi(filmInfo: TopFilmInfoVo) {
        binding?.apply {
            closeLoadingScreen()
            closeErrorScreen()

            txCountry.apply {
                isVisible = true
                text = filmInfo.country
            }
            txGenres.apply {
                isVisible = true
                text = filmInfo.genre
            }
            txDescription.apply {
                isVisible = true
                text = filmInfo.description
            }
            txTitle.apply {
                isVisible = true
                text = filmInfo.title
            }

            imPoster.isVisible = true
            Glide.with(requireContext())
                .load(filmInfo.posterUrl)
                .error(R.drawable.ic_error_image_load)
                .into(imPoster)
        }
    }

    override fun showLoadingScreen() {
        binding?.apply {
            loadingProgBar.isVisible = true
        }
    }

    override fun showErrorScreen() {
        binding?.apply {
            error.root.isVisible = true
        }
    }

    override fun closeLoadingScreen() {
        binding?.apply {
            loadingProgBar.isVisible = false
        }
    }

    override fun closeErrorScreen() {
        binding?.apply {
            error.root.isVisible = false
        }
    }

    override fun closeOtherViews() {
        binding?.apply {
            txCountry.isVisible = false
            txGenres.isVisible = false
            txDescription.isVisible = false
            txTitle.isVisible = false
            imPoster.isVisible = false
        }
    }

}