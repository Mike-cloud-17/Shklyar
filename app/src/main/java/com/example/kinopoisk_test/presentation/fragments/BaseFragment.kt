package com.example.kinopoisk_test.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.kinopoisk_test.app.App
import com.example.kinopoisk_test.databinding.ActivityMainBinding
import com.example.kinopoisk_test.presentation.activity.MainActivity
import com.github.terrakok.cicerone.Router

abstract class BaseFragment <T : ViewBinding, VM: BaseViewModel> : Fragment() {

    abstract val inflater: (LayoutInflater, ViewGroup?, Boolean) -> T
    protected var binding: T? = null

    protected var activityBinding: ActivityMainBinding? = null

    protected var viewModel: VM? = null
    abstract val viewModelClass: Class<VM>

    protected var router: Router? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = inflater(inflater, container, false)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel == null) {
            viewModel = ViewModelProvider(requireActivity())[viewModelClass]
            if (activityBinding == null) {
                activityBinding = (requireActivity() as? MainActivity)?.binding
            }
            if (router == null) {
                router = (requireContext().applicationContext as? App)?.router
            }
            initStartValues()
            initObservers()
            initUI()
            initButtons()
        }
    }

    protected open fun initUI() {
        // no-op
    }

    protected open fun initObservers() {
        // no-op
    }

    protected open fun initStartValues() {
        // no-op
    }

    protected open fun initButtons() {
        // no-op
    }


    protected abstract fun showLoadingScreen()
    protected abstract fun closeLoadingScreen()

    protected abstract fun showErrorScreen()
    protected abstract fun closeErrorScreen()

    protected abstract fun closeOtherViews()

    override fun onDetach() {
        binding = null
        viewModel = null
        router = null
        activityBinding = null
        super.onDetach()
    }

}