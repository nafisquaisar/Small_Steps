package com.example.nafis.nf2024.smallsteps.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.smallsteps.Adapter.NoteContentAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.NoteContentViewHolder
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteContentClick
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.NoteContent
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentNewNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding : FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel : NoteViewModel
    private lateinit var mView: View
    private lateinit var btnChecklist: ImageView
    private lateinit var btnBulletPoint: ImageView
    private lateinit var btnImage: ImageView
    private lateinit var textBtn: ImageView
    private lateinit var etNoteBody: EditText
    private lateinit var imageViewNote: ImageView
    private lateinit var list:ArrayList<NoteContent>
    private val REQUEST_GALLERY = 1
    private val REQUEST_CAMERA = 2
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private var imageUri: Uri? = null
    private var color by Delegates.notNull<Int>()
    private lateinit var noteAdapter:NoteContentAdapter

    private val callback by lazy {
        object:NoteContentClick{
            override fun onNoteContentClick(note: NoteContent) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDelete(pos: Int, note: NoteContent) {
                if (pos in list.indices) { // Check if index is valid
                    list.removeAt(pos) // No need to create a new list
                    noteAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Invalid operation: List is empty or position is invalid", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onTextAdd(pos: Int, note: NoteContent) {

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
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAll(view)
        clickListner()
        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        binding.noteRecycler.layoutManager=LinearLayoutManager(requireContext())
        noteAdapter= NoteContentAdapter(callback)
        binding.noteRecycler.adapter=noteAdapter
        list.clear()
        noteAdapter.submitList(list)
        if (list.isEmpty()) {
            addTextSpace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickListner() {

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
            updateAllCheckboxText()
            savenote(false)
        }
        binding.backgroundcolorchange.setOnClickListener {
            showBottomDialog()
        }

        textBtn.setOnClickListener {
            addTextSpace()
        }
    }

    private fun addTextSpace() {
        val newContent = NoteContent(id = list.size + 1, Text = "", imgUri = "", isText = true)
            val updatedList = ArrayList(list)
            updatedList.add(newContent)

            list = updatedList
            noteAdapter.submitList(updatedList)

    }
    private fun updateAllCheckboxText() {
        for (i in 0 until noteAdapter.itemCount) {  // Loop through all items
            val holder = binding.noteRecycler.findViewHolderForAdapterPosition(i)
            if (holder is NoteContentViewHolder) {
                list[i].Text = holder.binding.noteEditText.text.toString()  // Update list from views
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeAll(view: View) {
        notesViewModel = (activity as MainActivity).noteViewModel
        mView = view
        btnChecklist=binding.btnChecklist
        btnBulletPoint=binding.btnBulletPoint
        btnImage=binding.imageBtn
        textBtn=binding.textbtn
        color = 0xFFFFFF
        list=ArrayList()
        binding.datetime.setText(getDateTime())
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Add New Note"
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)?.apply {
                setTint(requireContext().getColor(R.color.white))
            }
        )

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun savenote(isDelete: Boolean) {

        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val createdDate=getDateTime()
        val updatedList = ArrayList(list)
        list = updatedList

        var  backgroundColor = String.format("#%06X", 0xFFFFFF and color)

        if (noteTitle.isNotEmpty()){
            val note = Notes(0,noteTitle, "",backgroundColor,createdDate, contentList = list)

            notesViewModel.addNote(note)

          if(!isDelete){
              Toast.makeText(mView.context,
                  "Note Saved Successfully",
                  Toast.LENGTH_LONG).show()

              requireParentFragment().findNavController().navigate(R.id.action_newNoteFragment_to_noteTypeFragment)
          }
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



    private fun insertChecklist() {
        // Find the currently focused EditText in the RecyclerView
        val focusedView = binding.noteRecycler.findFocus()
        if (focusedView is EditText) {
            val currentText = focusedView.text.toString()
            val cursorPos = focusedView.selectionStart

            // Insert the checklist symbol at the current cursor position
            val updatedText = currentText.substring(0, cursorPos) + "☑ " + currentText.substring(cursorPos)
            focusedView.setText(updatedText)

            // Restore the cursor position after the inserted text
            focusedView.setSelection(cursorPos + 2)
        } else {
            Toast.makeText(context, "Select a note to add a Check Box", Toast.LENGTH_SHORT).show()
        }
    }


    private fun insertBulletPoint() {

        val focusedView = binding.noteRecycler.findFocus()
        if (focusedView is EditText) {
            val currentText = focusedView.text.toString()
            val cursorPos = focusedView.selectionStart

            // Insert the checklist symbol at the current cursor position
            val updatedText = currentText.substring(0, cursorPos) + "• " + currentText.substring(cursorPos)
            focusedView.setText(updatedText)

            // Restore the cursor position after the inserted text
            focusedView.setSelection(cursorPos + 2)
        } else {
            Toast.makeText(context, "Select a note to add a Bullet Point", Toast.LENGTH_SHORT).show()
        }

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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            // Request CAMERA permission
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.nafis.nf2024.smallsteps.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch camera
                launchCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required!", Toast.LENGTH_SHORT).show()
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
            when (requestCode) {
                REQUEST_GALLERY -> {
                    imageUri = data?.data
                    imageUri?.let { uri ->
                        val newContent = NoteContent(
                            id = list.size + 1,
                            Text = "",
                            imgUri = uri.toString(),
                            isText = false
                        )
                        list.add(newContent)
                        noteAdapter.submitList(ArrayList(list))
                        noteAdapter.notifyDataSetChanged()
                    }
                }

                REQUEST_CAMERA -> {
                    // Use the imageUri assigned when opening the camera
                    imageUri?.let { uri ->
                        val newContent = NoteContent(
                            id = list.size + 1,
                            Text = "",
                            imgUri = uri.toString(),
                            isText = false
                        )
                        list.add(newContent)
                        noteAdapter.submitList(ArrayList(list))
                        noteAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateTime():String{
        val currdate=LocalDateTime.now()
        val formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return currdate.format(formatter)
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

                binding.cardView2.setBackgroundColor(colorInt)

                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        backbtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

    }

}