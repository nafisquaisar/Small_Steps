package com.example.nafis.nf2024.smallsteps.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nafis.nf2024.smallsteps.Activity.AboutActivity
import com.example.nafis.nf2024.smallsteps.Activity.HelpActivity
import com.example.nafis.nf2024.smallsteps.Activity.LockScreenActivity
import com.example.nafis.nf2024.smallsteps.Activity.SetLockActivity
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxNoteAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.NoteAdapter
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxNoteItemClick
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteItemClick
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.Notes

import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxViewModel
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentCheckListBinding
import com.example.nafis.nf2024.smallsteps.databinding.FragmentNewCheckListBinding


class CheckListFragment : Fragment() ,SearchView.OnQueryTextListener{
     private lateinit var binding: FragmentCheckListBinding
    private lateinit var checkboxNotesViewModel : CheckBoxViewModel
    private lateinit var noteAdapter: CheckBoxNoteAdapter
    private lateinit var list:ArrayList<CheckBoxNote>
    private lateinit var sharedPreferences: SharedPreferences


    private val callback by lazy {
        object : CheckBoxNoteItemClick {
            override fun onCheckNoteClick(note: CheckBoxNote) {
                val direction = NoteTypeFragmentDirections.actionNoteTypeFragmentToUpdateCheckBoxFragment(note)
                   parentFragment?.findNavController()?.navigate(direction)
            }

            override fun onLongClick(note: CheckBoxNote) {
                deleteNote(note)
            }


        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCheckListBinding.inflate(inflater,container,false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Small Steps"

        binding.fabAddCheckBoxNote.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_noteTypeFragment_to_newCheckListFragment)
        }
        checkboxNotesViewModel=( activity as MainActivity).checkNoteViewModel
        setUpRecyclerView()


        return binding.root
    }

    private fun setUpRecyclerView() {
        list=ArrayList()
        noteAdapter= CheckBoxNoteAdapter(list,callback)
        binding.checkListNoterecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter

        }

        activity?.let {
            checkboxNotesViewModel.getAllCheckBoxNotes().observe(
                viewLifecycleOwner
            ) {
                list = it as ArrayList<CheckBoxNote>
                noteAdapter.submitList(list)
                updateUI(it)
            }
        }

    }

    private fun updateUI(note: List<CheckBoxNote>?) {
        if (note != null) {
            if (note.isNotEmpty()) {
                binding.cardView.visibility = View.GONE
                binding.checkListNoterecyclerView.visibility = View.VISIBLE
            } else {
                binding.cardView.visibility = View.VISIBLE
                binding.checkListNoterecyclerView.visibility = View.GONE

            }
        }
    }

    private fun deleteNote(currentNote: CheckBoxNote) {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                // Delete the note from the database (via ViewModel)
                checkboxNotesViewModel.deleteCheckBoxNote(currentNote)

                // Get the updated list of notes after deletion
                checkboxNotesViewModel.getAllCheckBoxNotes().observe(viewLifecycleOwner) { updatedNotes ->
                    list.clear() // Clear the old list
                    list.addAll(updatedNotes) // Add the updated list from the database

                    // Submit the updated list to the adapter
                    noteAdapter.submitList(ArrayList(list)) // Update the adapter with the new list
                    updateUI(updatedNotes) // Handle empty or non-empty list UI visibility
                }
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)
        sharedPreferences = requireActivity().getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE)

        val savedPIN = sharedPreferences.getString("LOCK_PIN", null)

        // Hide or show the menu item based on the condition
        if (savedPIN.isNullOrEmpty()) {
            menu?.findItem(R.id.disablepin)?.isVisible = false // Hide the settings item
            menu?.findItem(R.id.enablepin)?.title="Enable PIN" // Show the settings item
        } else {
            menu?.findItem(R.id.disablepin)?.isVisible = true  // Show the settings item
            menu?.findItem(R.id.enablepin)?.title="Change PIN" // Show the settings item

        }

        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = query ?: ""
        checkboxNotesViewModel.searchCheckBoxNote(searchQuery)?.observe(viewLifecycleOwner) { filteredList ->
            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
            }
            list = filteredList as ArrayList<CheckBoxNote>
            noteAdapter.submitList(list)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                // Handle your search item click
                return true
            }
            R.id.help->{
                startActivity(Intent(requireContext(), HelpActivity::class.java))
            }
            R.id.about->{
                startActivity(Intent(requireContext(), AboutActivity::class.java))
            }
            R.id.enablepin->{
                startActivity(Intent(requireContext(), SetLockActivity::class.java))
            }
            R.id.disablepin->{
                var intent= Intent(requireContext(), LockScreenActivity::class.java)
                intent.putExtra("disable",true)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }



}