package com.example.nafis.nf2024.smallsteps.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nafis.nf2024.smallsteps.Adapter.PageViewAdapter

import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.databinding.FragmentNoteTypeBinding
import com.google.android.material.tabs.TabLayoutMediator


class NoteTypeFragment : Fragment() {

    private lateinit var binding:FragmentNoteTypeBinding
    private lateinit var adapter:PageViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

       binding= FragmentNoteTypeBinding.inflate(inflater,container,false)
        adapter= PageViewAdapter(childFragmentManager,lifecycle)
        binding.notecontainer.adapter=adapter

        val selectedTab=arguments?.getInt("selectedTabIndex",0) ?:0

        TabLayoutMediator(binding.tablayout,binding.notecontainer){tab,position->
            tab.text=if(position==0) "Text Note" else "CheckList "
        }.attach()

        binding.notecontainer.setCurrentItem(selectedTab,false)

        return binding.root
    }


}