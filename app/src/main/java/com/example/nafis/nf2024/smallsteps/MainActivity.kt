package com.example.nafis.nf2024.smallsteps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.nafis.nf2024.smallsteps.Database.NoteDatabase
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Repository.CheckBoxRepository
import com.example.nafis.nf2024.smallsteps.Repository.NoteRepository
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxNoteViewModelFactory
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxViewModel
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModelFactory
import com.example.nafis.nf2024.smallsteps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding

    // NavController for Navigation
    private lateinit var navController: NavController

    // ViewModel for Notes
    lateinit var noteViewModel: NoteViewModel
    lateinit var checkNoteViewModel: CheckBoxViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar as the ActionBar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Small Step"


        // Initialize NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Set up ActionBar with NavController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up the ViewModel
        setUpViewModel()
        setUpCheckBoxViewModel()
    }

    private fun setUpCheckBoxViewModel() {
        val checkBoxRepository=CheckBoxRepository(NoteDatabase(this))
        val viewModelProviderFactory=CheckBoxNoteViewModelFactory(application,checkBoxRepository)

        checkNoteViewModel=ViewModelProvider(this,viewModelProviderFactory).get(CheckBoxViewModel::class.java)
    }

    private fun setUpViewModel() {
        // Initialize Repository and ViewModelFactory
        val noteRepository = NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)

        // Get the ViewModel using the factory
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NoteViewModel::class.java)
    }

    // Handle back navigation with the NavController
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
