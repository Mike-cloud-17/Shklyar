package com.mike_cloud_17.gifs_programm.user_interface

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mike_cloud_17.gifs_programm.request_work.PageInfoFactory

class PagerAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = PageInfoFactory.PAGES.map {
        PageFragment.newInstance(it)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getPageTitle(position: Int): CharSequence {
        return activity.resources.getString(PageInfoFactory.PAGES.elementAt(position).resourceId)
    }
}