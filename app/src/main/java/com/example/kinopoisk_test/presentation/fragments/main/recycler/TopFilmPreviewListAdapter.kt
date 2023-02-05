package com.example.kinopoisk_test.presentation.fragments.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kinopoisk_test.R
import com.example.kinopoisk_test.databinding.ItemPreviewElementBinding
import com.example.kinopoisk_test.presentation.fragments.main.TopFilmPreviewListViewModel
import com.example.kinopoisk_test.presentation.model.TopFilmPreviewVo
import com.example.kinopoisk_test.presentation.screens.Screens
import com.github.terrakok.cicerone.Router

class TopFilmPreviewListAdapter(
    val previews: ArrayList<TopFilmPreviewVo>,
    private val router: Router,
    private val viewModel: TopFilmPreviewListViewModel
) : RecyclerView.Adapter<TopFilmPreviewListAdapter.TopFilmPreviewListViewHolder>() {

    var filterString = ""

    class TopFilmPreviewListViewHolder(
        private val binding: ItemPreviewElementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            topFilmPreviewVo: TopFilmPreviewVo,
            router: Router,
            viewModel: TopFilmPreviewListViewModel
        ) = with(binding) {
            txPreviewGenreAndYear.text = topFilmPreviewVo.descWithYear
            txPreviewTitle.text = topFilmPreviewVo.title

            Glide.with(binding.root.context)
                .load(topFilmPreviewVo.posterUrl)
                .error(R.drawable.ic_error_image_load)
                .into(imPreviewPoster)

            imFavourite.isVisible = topFilmPreviewVo.isFavourite

            root.setOnLongClickListener {
                viewModel.addToFavouriteList(topFilmPreviewVo.filmId)
                imFavourite.isVisible = true
                true
            }

            root.setOnClickListener {
                router.navigateTo(Screens.topFilmInfoFragment(topFilmPreviewVo.filmId))
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopFilmPreviewListViewHolder =
        TopFilmPreviewListViewHolder(
            ItemPreviewElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TopFilmPreviewListViewHolder, position: Int) {
        holder.bind(previews[position], router, viewModel)
    }

    override fun getItemCount() = previews.size

    fun setData(previews: List<TopFilmPreviewVo>) {
        val filteredPeviews = if (filterString != "") {
            previews.filter { topFilmPreviewVo: TopFilmPreviewVo ->
                topFilmPreviewVo.title.toLowerCase().startsWith(filterString.toLowerCase())
            }
        } else {
            previews
        }

        val diffUltil = TopFilmPrevDiffUtil(this.previews, filteredPeviews)
        val result = DiffUtil.calculateDiff(diffUltil)

        this.previews.clear()
        this.previews.addAll(filteredPeviews)
        result.dispatchUpdatesTo(this)
    }

}