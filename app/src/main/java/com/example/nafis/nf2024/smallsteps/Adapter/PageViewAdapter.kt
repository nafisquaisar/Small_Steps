package com.example.nafis.nf2024.smallsteps.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nafis.nf2024.smallsteps.Fragment.CheckListFragment
import com.example.nafis.nf2024.smallsteps.Fragment.HomeFragment

class PageViewAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
            return 2
    }

    override fun createFragment(position: Int): Fragment {
              return if (position==0){
                         HomeFragment()
              }else{
                        CheckListFragment()
              }
    }
}