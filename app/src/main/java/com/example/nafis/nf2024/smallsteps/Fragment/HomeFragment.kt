package com.example.nafis.nf2024.smallsteps.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nafis.nf2024.smallsteps.Activity.AboutActivity
import com.example.nafis.nf2024.smallsteps.Activity.HelpActivity
import com.example.nafis.nf2024.smallsteps.Activity.LockScreenActivity
import com.example.nafis.nf2024.smallsteps.Activity.SetLockActivity
import com.example.nafis.nf2024.smallsteps.Adapter.NoteAdapter
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteItemClick
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var list:ArrayList<Notes>
    private lateinit var sharedPreferences: SharedPreferences


    private lateinit var notesViewModel : NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    private val callback by lazy {
             object :NoteItemClick{
                 override fun onNoteClick(note: Notes) {
                     val direction = NoteTypeFragmentDirections.actionNoteTypeFragmentToUpdateNoteFragment(note)
                     requireParentFragment().findNavController().navigate(direction)
                 }

                 override fun onLongClick(note: Notes) {
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Small Steps"
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel

        setUpRecyclerView()

        binding.fabAddNote.setOnClickListener{
            requireParentFragment().findNavController().navigate(
                R.id.action_noteTypeFragment_to_newNoteFragment
            )
        }
    }

    private fun setUpRecyclerView() {
        list=ArrayList()
        noteAdapter= NoteAdapter(list,callback)
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter

        }

        activity?.let {
            notesViewModel.getAllNotes().observe(
                viewLifecycleOwner
            ) {
                list = it as ArrayList<Notes>
                noteAdapter.submitList(list)
                updateUI(it)
            }
        }

    }


    private fun deleteNote(currentNote: Notes) {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                // Delete the note from the database (via ViewModel)
                notesViewModel.deleteNote(currentNote)

                // Get the updated list of notes after deletion
                notesViewModel.getAllNotes().observe(viewLifecycleOwner) { updatedNotes ->
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



    private fun updateUI(note: List<Notes>?) {
        if (note != null) {
            if (note.isNotEmpty()) {
                binding.cardView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.cardView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE

            }
        }
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
        notesViewModel.searchNote(searchQuery)?.observe(viewLifecycleOwner) { filteredList ->
            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
            }
            list = filteredList as ArrayList<Notes>
            noteAdapter.submitList(list)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                // Handle your search item click
                return true
            }
            R.id.help->{
                startActivity(Intent(requireContext(),HelpActivity::class.java))
            }
            R.id.about->{
                startActivity(Intent(requireContext(),AboutActivity::class.java))
            }
            R.id.enablepin->{
                startActivity(Intent(requireContext(),SetLockActivity::class.java))
            }
            R.id.disablepin->{
                var intent=Intent(requireContext(),LockScreenActivity::class.java)
                intent.putExtra("disable",true)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}