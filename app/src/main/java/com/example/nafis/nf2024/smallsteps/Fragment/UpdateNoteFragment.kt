package com.example.nafis.nf2024.smallsteps.Fragment


import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.smallsteps.Adapter.NoteContentAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.NoteContentViewHolder
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteContentClick
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.NoteContent
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentUpdateNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates


class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private lateinit var notesViewModel: NoteViewModel
    private var color by Delegates.notNull<Int>()
    private lateinit var list: ArrayList<NoteContent>

    private lateinit var currentNote: Notes
    private val CREATE_FILE_REQUEST_CODE = 1
    private val REQUEST_GALLERY = 1
    private val REQUEST_CAMERA = 2
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    // Since the Update Note Fragment contains arguments in nav_graph
    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var noteAdapter: NoteContentAdapter

    private val callback by lazy {
        object : NoteContentClick {
            override fun onNoteContentClick(note: NoteContent) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDelete(pos: Int, note: NoteContent) {
                if (pos in list.indices) {
                    val updatedList = ArrayList(list)
                    updatedList.removeAt(pos)
                    list = updatedList
                    noteAdapter.submitList(updatedList)
                } else {
                    Toast.makeText(
                        context,
                        "Invalid operation: List is empty or position is invalid",
                        Toast.LENGTH_SHORT
                    ).show()
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
        _binding = FragmentUpdateNoteBinding.inflate(
            inflater,
            container,
            false
        )
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Update Note"
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)
                ?.apply {
                    setTint(requireContext().getColor(R.color.white))
                }
        )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!
        setLayout()


        // if the user update the note
        binding.fabDone.setOnClickListener {
            updateAllCheckboxText()
            updateNote()
        }

        binding.backgroundcolorchange.setOnClickListener {
            showBottomDialog()
        }

        binding.btnBulletPoint.setOnClickListener {
            insertBulletPoint()
        }
        binding.btnChecklist.setOnClickListener {
            insertChecklist()
        }
        binding.textImage.setOnClickListener {
            addTextSpace()
        }
        binding.btnImage.setOnClickListener {
            showImagePickerDialog()
        }
    }


    private fun setLayout() {
        list = ArrayList(currentNote.contentList)  // Ensure a new list instance

        binding.etNoteTitle.setText(currentNote.noteTitle)
        binding.datetime.setText(currentNote.createdDate)
        try {
            color = android.graphics.Color.parseColor(currentNote.bgColor)
            binding.cardView2.setBackgroundColor(color)  // Use setBackgroundColor instead of setBackgroundResource
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            color = android.graphics.Color.WHITE  // Fallback to white if parsing fails
            binding.cardView2.setBackgroundColor(color)
        }
        binding.noteRecycler.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteContentAdapter(callback)
        binding.noteRecycler.adapter = noteAdapter
        noteAdapter.submitList(ArrayList(list))  // Ensure proper update
        noteAdapter.notifyDataSetChanged() // Force UI update
    }

    private fun updateAllCheckboxText() {
        for (i in 0 until noteAdapter.itemCount) {  // Loop through all items
            val holder = binding.noteRecycler.findViewHolderForAdapterPosition(i)
            if (holder is NoteContentViewHolder) {
                list[i].Text =
                    holder.binding.noteEditText.text.toString()  // Update list from views
            }
        }
    }

    //=================== Update the Text Note==========================
    fun updateNote() {
        val title = binding.etNoteTitle.text.toString().trim()
        val updatedList = ArrayList(list)
        list = updatedList
        color = try {
            android.graphics.Color.parseColor(currentNote.bgColor)
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.WHITE
        }

        if (title.isNotEmpty()) {
            val note = Notes(
                currentNote.id, title, "", String.format(
                    "#%06X",
                    0xFFFFFF and color
                ), currentNote.createdDate, list
            )
            notesViewModel.updateNote(note)
            view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_noteTypeFragment)
        } else {
            Toast.makeText(
                context,
                "Please enter note Title",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    //=================== Update the Text Note==========================


    //=================== Delete  the Text Note ==========================
    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {

            setTitle("Delete Note")
            setMessage("You want to delete this Note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteNote(currentNote)

                view?.findNavController()?.navigate(
                    R.id.action_updateNoteFragment_to_noteTypeFragment
                )

            }
            setNegativeButton("Cancel", null)
        }.create().show()


    }


    //================== Add Text Space for Writing==========================
    private fun addTextSpace() {
        val newContent = NoteContent(id = list.size + 1, Text = "", imgUri = "", isText = true)
        val updatedList = ArrayList(list)
        updatedList.add(newContent)

        list = updatedList
        noteAdapter.submitList(updatedList)

    }
    //================== Add Text Space for Writing==========================

    //================== Add checklist for Writing==========================
    private fun insertChecklist() {
        // Find the currently focused EditText in the RecyclerView
        val focusedView = binding.noteRecycler.findFocus()
        if (focusedView is EditText) {
            val currentText = focusedView.text.toString()
            val cursorPos = focusedView.selectionStart

            // Insert the checklist symbol at the current cursor position
            val updatedText =
                currentText.substring(0, cursorPos) + "☑ " + currentText.substring(cursorPos)
            focusedView.setText(updatedText)

            // Restore the cursor position after the inserted text
            focusedView.setSelection(cursorPos + 2)
        } else {
            Toast.makeText(context, "Select a note to add a Check Box", Toast.LENGTH_SHORT).show()
        }
    }
    //================== Add checklist for Writing==========================

    //================== Add Bullet Point for Writing==========================
    private fun insertBulletPoint() {

        val focusedView = binding.noteRecycler.findFocus()
        if (focusedView is EditText) {
            val currentText = focusedView.text.toString()
            val cursorPos = focusedView.selectionStart

            // Insert the checklist symbol at the current cursor position
            val updatedText =
                currentText.substring(0, cursorPos) + "• " + currentText.substring(cursorPos)
            focusedView.setText(updatedText)

            // Restore the cursor position after the inserted text
            focusedView.setSelection(cursorPos + 2)
        } else {
            Toast.makeText(context, "Select a note to add a Bullet Point", Toast.LENGTH_SHORT)
                .show()
        }

    }
    //================== Add Bullet Point for Writing==========================

    //================== Add Bullet Point for Writing==========================
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
            != PackageManager.PERMISSION_GRANTED
        ) {

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch camera
                launchCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required!",
                    Toast.LENGTH_SHORT
                ).show()
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
                CREATE_FILE_REQUEST_CODE -> {
                    if (data?.data != null) {
                        writePdf(data.data!!)
                    } else {
                        Log.e("PDF Export", "URI is null, file not created.")
                        Toast.makeText(requireContext(), "Failed to create file", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // Handle back button
                requireActivity().onBackPressed()
                return true
            }

            R.id.menu_delete -> {
                deleteNote()
            }

            R.id.share -> { // Share the existing PDF file
                sharePdfFile()
                return true
            }

            R.id.exportUpdatefile -> { // Export PDF to another location
                exportPdfFile()
                return true
            }
//            R.id.backgroundcolor->{
////                showCardSelectionDialog()
//                return true
//            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
//========================================  main funtion of all done ==========================================================================================================

    // ====================================   Share the pdf   ==================================================================================================
    private fun createPdfFile(): File? {
        return try {
            val pdfPath = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
            val directory = File(pdfPath ?: throw Exception("Unable to access directory"))
            if (!directory.exists()) directory.mkdirs()

            // Define file path
            val file = File(directory, "${currentNote.noteTitle}.pdf")

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            // Create a table with two columns: Title (Left) and Date (Right)
            val table = Table(floatArrayOf(1f, 1f)) // Two equal columns
            table.setWidth(UnitValue.createPercentValue(100f)) // Ensure table spans full width

// Left Side: Title
            table.addCell(
                Cell().add(Paragraph(currentNote.noteTitle).setBold().setFontSize(20f))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT) // Explicitly align left
            )

// Right Side: Created Date (Properly Aligned to the Right)
            table.addCell(
                Cell().add(
                    Paragraph(currentNote.createdDate)
                        .setFontSize(12f)
                        .setTextAlignment(TextAlignment.RIGHT) // Align text inside cell
                )
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT) // Ensure the entire cell is aligned right
            )

// Add the table to the document
            document.add(table)


            // Add a horizontal line
            document.add(LineSeparator(SolidLine()))

            // Add text and images
            for (item in currentNote.contentList) {
                if (item.Text != null) {
                    document.add(Paragraph(item.Text).setFontSize(16f))
                }
                if (item.imgUri != null) {
                    try {
                        val uri = Uri.parse(item.imgUri)
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val byteArray = inputStream?.readBytes() ?: throw Exception("Failed to read image data")
                        inputStream.close()

                        val imageData = ImageDataFactory.create(byteArray)
                        val iTextImage = Image(imageData).scaleToFit(400f, 400f)
                        document.add(iTextImage)
                    } catch (e: Exception) {
                        Log.e("PDF Creation", "Error adding image: ${e.message}")
                        document.add(Paragraph(""))
                    }
                }
            }

            // Add another horizontal line
            document.add(LineSeparator(SolidLine()))

            // Add created date at the bottom
            document.add(
                Paragraph(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
            )

            document.close()
            Toast.makeText(requireContext(), "PDF saved successfully!", Toast.LENGTH_SHORT).show()

            return file // Return the saved PDF file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }


    //... (getBitmapFromUri, bitmapToByteArray - same as before)
    private fun sharePdfFile() {
        val file = createPdfFile() ?: return
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share PDF via"))
    }


    // ====================================   Share the pdf   ======================================

    // ====================================   Export the pdf   ======================================
    // Step 1: Launch file picker for PDF creation
    private fun exportPdfFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "${currentNote.noteTitle}.pdf")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    private fun writePdf(uri: Uri) {
        try {
            val pfd: ParcelFileDescriptor? = requireActivity().contentResolver.openFileDescriptor(uri, "w")
            pfd?.let {
                val outputStream = FileOutputStream(pfd.fileDescriptor)
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(pdfWriter)
                val document = Document(pdfDocument)
                // Create a table with two columns: Title (Left) and Date (Right)
                val table = Table(floatArrayOf(1f, 1f)) // Two equal columns
                table.setWidth(UnitValue.createPercentValue(100f)) // Ensure table spans full width

// Left Side: Title
                table.addCell(
                    Cell().add(Paragraph(currentNote.noteTitle).setBold().setFontSize(20f))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT) // Explicitly align left
                )

// Right Side: Created Date (Properly Aligned to the Right)
                table.addCell(
                    Cell().add(
                        Paragraph(currentNote.createdDate)
                            .setFontSize(12f)
                            .setTextAlignment(TextAlignment.RIGHT) // Align text inside cell
                    )
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT) // Ensure the entire cell is aligned right
                )

// Add the table to the document
                document.add(table)


                // Add a horizontal line
                document.add(LineSeparator(SolidLine()))

                // Add text and images
                for (item in currentNote.contentList) {
                    if (item.Text != null) {
                        document.add(Paragraph(item.Text).setFontSize(16f))
                    }
                    if (item.imgUri != null) {
                        try {
                            val uri = Uri.parse(item.imgUri)
                            val inputStream = requireContext().contentResolver.openInputStream(uri)
                            val byteArray = inputStream?.readBytes() ?: throw Exception("Failed to read image data")
                            inputStream.close()

                            val imageData = ImageDataFactory.create(byteArray)
                            val iTextImage = Image(imageData).scaleToFit(400f, 400f)
                            document.add(iTextImage)
                        } catch (e: Exception) {
                            Log.e("PDF Creation", "Error adding image: ${e.message}")
                            document.add(Paragraph(""))
                        }
                    }
                }

                // Add another horizontal line
                document.add(LineSeparator(SolidLine()))

                // Add created date at the bottom
                document.add(
                    Paragraph(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
                        .setFontSize(12f)
                        .setTextAlignment(TextAlignment.CENTER)
                )

                document.close()
                pfd.close()
            }
            Toast.makeText(requireContext(), "PDF exported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error exporting PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ====================================   Export the pdf   ======================================



    // ===================Change the BackGround of the Note===============================

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

        for (card in cardViews) {
            card.setOnClickListener {
                val colorInt = card.cardBackgroundColor.defaultColor // Get the background color
                color=colorInt
                // Set background immediately
                binding.cardView2.setBackgroundColor(colorInt)
                currentNote.bgColor = String.format("#%06X", 0xFFFFFF and colorInt)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }



}