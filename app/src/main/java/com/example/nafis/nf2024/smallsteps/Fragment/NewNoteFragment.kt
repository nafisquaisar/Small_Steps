package com.example.nafis.nf2024.smallsteps.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentNewNoteBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding : FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel : NoteViewModel

    private lateinit var mView: View
    private lateinit var btnChecklist: ImageView
    private lateinit var btnBulletPoint: ImageView
    private lateinit var btnImage: ImageView
    private lateinit var etNoteBody: EditText
    private lateinit var imageViewNote: ImageView

    private val REQUEST_GALLERY = 1
    private val REQUEST_CAMERA = 2
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        mView = view
        etNoteBody=binding.etNoteBody
        btnChecklist=binding.btnChecklist
        btnBulletPoint=binding.btnBulletPoint
        btnImage=binding.btnImage

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Add New Note"
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)?.apply {
                setTint(requireContext().getColor(R.color.white))
            }
        )

        btnChecklist.setOnClickListener {
            insertChecklist()
        }
        btnBulletPoint.setOnClickListener {
            insertBulletPoint()
        }
        btnImage.setOnClickListener {
            showImagePickerDialog()
        }

        binding.addNote.setOnClickListener {
            savenote()
        }

    }


    private fun savenote(){
        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val noteBody = binding.etNoteBody.text.toString().trim()

        if (noteTitle.isNotEmpty()){
            val note = Notes(0,noteTitle, noteBody)

            notesViewModel.addNote(note)

            Toast.makeText(mView.context,
                "Note Saved Successfully",
                Toast.LENGTH_LONG).show()

            requireParentFragment().findNavController().navigate(R.id.action_newNoteFragment_to_noteTypeFragment)
        }
        else{
            Toast.makeText(
                mView.context,
                "Please enter note Title",
                Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        menu.clear() // Clear existing menu
//        inflater.inflate(R.menu.menu_new_note, menu) // Add the "More" button menu
//        super.onCreateOptionsMenu(menu, inflater)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> { // Handle back button
//                requireActivity().onBackPressed()
//                return true
//            }
//            R.id.share -> { // Handle "More" button
//                Toast.makeText(requireContext(), "Share clicked", Toast.LENGTH_SHORT).show()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


    private fun insertChecklist() {
        etNoteBody.append("\n☑ ")
    }

    private fun insertBulletPoint() {
        etNoteBody.append("\n• ")
    }

    private fun showImagePickerDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Image")
            .setItems(arrayOf("Camera", "Gallery")) { dialog, which ->
                if (which === 0) {
                    openCamera()
                } else {
                    openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

        private fun openCamera() {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                val photoFile = createImageFile()
                if (photoFile != null) {
                    imageUri =
                        FileProvider.getUriForFile(requireContext(), "com.example.nafis.nf2024.smallsteps.fileprovider", photoFile)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("IMG_$timeStamp", ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                imageUri = data.data
                imageViewNote.setImageURI(imageUri)
                imageViewNote.setVisibility(View.VISIBLE)
            } else if (requestCode == REQUEST_CAMERA) {
                imageViewNote.setImageURI(imageUri)
                imageViewNote.setVisibility(View.VISIBLE)
            }
        }
    }


}