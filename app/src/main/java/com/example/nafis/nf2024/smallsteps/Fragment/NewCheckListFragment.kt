package com.example.nafis.nf2024.smallsteps.Fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxViewHolder
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxItemClick
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentNewCheckListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates


class NewCheckListFragment : Fragment() {
    private lateinit var binding:FragmentNewCheckListBinding
    private lateinit var checkboxContainer: LinearLayout
    private lateinit var list:ArrayList<checkbox>
    private lateinit var checkBoxAdapter: CheckBoxAdapter
    private var color by Delegates.notNull<Int>()
    private lateinit var checkBoxViewModel:CheckBoxViewModel

    private val callback by lazy {
        object :CheckBoxItemClick{
            override fun onCheckClick(note: checkbox) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDeleteClick(pos: Int, note: checkbox) {
                list.removeAt(pos) // Update the list
                checkBoxAdapter.submitList(ArrayList(list))
                saveCheckNote(true)
            }

        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNewCheckListBinding.inflate(inflater,container,false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Add New CheckList"
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)?.apply {
                setTint(requireContext().getColor(R.color.white))
            }
        )
        color = 0xFFFFFF
        checkBoxViewModel= (activity as MainActivity).checkNoteViewModel
        checkboxContainer = binding.checkboxContainer
        val addCheckboxButton = binding.addcheckbox
        list= ArrayList()
        binding.checkboxRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        checkBoxAdapter= CheckBoxAdapter(callback)
        binding.checkboxRecyclerView.adapter=checkBoxAdapter
        val currentDate=getCurrentDateTime()
        binding.datetime.setText(currentDate)
        addCheckboxButton.setOnClickListener {
            addNewCheckbox()
        }
        binding.backgroundcolorchange.setOnClickListener {
            showBottomDialog()
        }

        binding.fabDoneCheckbox.setOnClickListener {
            updateAllCheckboxText()
            saveCheckNote()
        }


        return binding.root
    }


    private fun updateAllCheckboxText() {
        for (i in 0 until binding.checkboxRecyclerView.childCount) {
            val holder = binding.checkboxRecyclerView.findViewHolderForAdapterPosition(i)
            if (holder is CheckBoxViewHolder) {
                list[i].text = holder.binding.checkboxedittext.text.toString()
            }

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveCheckNote(isdelete:Boolean=false) {
        val currentDate=getCurrentDateTime()
        val title=binding.etNoteTitle.text.toString()

        if(title.isNotEmpty()){
                val note= CheckBoxNote(title = title, checkBoxes = list, backgroundColor = String.format(
                    "#%06X",
                    0xFFFFFF and color
                ), dateCreated = currentDate)
                 checkBoxViewModel.addCheckBoxNote(note)
           if(!isdelete){
               Toast.makeText(context,
                   "Note Saved Successfully",
                   Toast.LENGTH_LONG).show()
               val bundle=Bundle().apply {
                   putInt("selectedTabIndex", 1)
               }
               requireParentFragment().findNavController().navigate(R.id.action_newCheckListFragment_to_noteTypeFragment,bundle)
           }
        }else{
            Toast.makeText(
                context,
                "Please enter note Title",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun addNewCheckbox() {
        val newCheckbox = checkbox(id = list.size + 1, isChecked = false, text = "")
        list.add(newCheckbox) // Add new checkbox to the list
        checkBoxAdapter.submitList(ArrayList(list)) // Notify adapter that data has changed
    }

    fun showBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.demo_layout, null)

        val cardViews = listOf<CardView>(
            view.findViewById(R.id.cardView1),
            view.findViewById(R.id.cardView2),
            view.findViewById(R.id.cardView3),
            view.findViewById(R.id.cardView4),
            view.findViewById(R.id.cardView5),
            view.findViewById(R.id.cardView6),
            view.findViewById(R.id.cardView7),
            view.findViewById(R.id.cardView8),
            view.findViewById(R.id.cardView9),
            view.findViewById(R.id.cardView10),
            view.findViewById(R.id.cardView11),
            view.findViewById(R.id.cardView12)
        )
        val backbtn=view.findViewById<View>(R.id.closedialog)
        for (card in cardViews) {
            card.setOnClickListener {
                val colorInt = card.cardBackgroundColor.defaultColor // Get the background color
                color = colorInt
                // Set background immediately
                binding.mainbg.setBackgroundColor(colorInt)

                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        backbtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return currentDateTime.format(formatter)
    }



}