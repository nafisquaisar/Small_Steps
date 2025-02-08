package com.example.nafis.nf2024.smallsteps.Fragment


import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.NoteViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentUpdateNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream


class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private var _binding : FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel : NoteViewModel


    private lateinit var currentNote : Notes
    private val CREATE_FILE_REQUEST_CODE = 1
    // Since the Update Note Fragment contains arguments in nav_graph
    private val args: UpdateNoteFragmentArgs by navArgs()


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
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)?.apply {
                setTint(requireContext().getColor(R.color.white))
            }
        )

        // Retrieve saved color from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("NoteBg", Context.MODE_PRIVATE)
        val savedColor = sharedPref.getInt("selected_color", -1)

        if (savedColor != -1) {
            binding.mainbg.setBackgroundColor(savedColor) // Apply saved color
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)
        binding.etNoteBodyUpdate.setText(currentNote.noteBody)

        // if the user update the note
        binding.fabDone.setOnClickListener{
            val title = binding.etNoteTitleUpdate.text.toString().trim()
            val body = binding.etNoteBodyUpdate.text.toString().trim()

            if (title.isNotEmpty()){
                val note = Notes(currentNote.id,title, body)
                notesViewModel.updateNote(note)
                view.findNavController().navigate(R.id.action_updateNoteFragment_to_noteTypeFragment)
            }else{
                Toast.makeText(
                    context,
                    "Please enter note Title",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.backgroundcolorchange.setOnClickListener{
            showBottomDialog()
        }
    }

    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {

            setTitle("Delete Note")
            setMessage("You want to delete this Note?")
            setPositiveButton("Delete"){_,_ ->
                notesViewModel.deleteNote(currentNote)

                view?.findNavController()?.navigate(
                    R.id.action_updateNoteFragment_to_noteTypeFragment
                )

            }
            setNegativeButton("Cancel", null)
        }.create().show()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update_note,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> { // Handle back button
                requireActivity().onBackPressed()
                return true
            }
            R.id.menu_delete -> {
                deleteNote()
            }
            R.id.share -> { // Handle "More" button
                currentNote = args.note!!
                createAndSharePdf(currentNote.noteTitle,currentNote.noteBody)
                return true
            }
            R.id.exportUpdatefile->{
                createPdfFile()
                return true
            }
            R.id.backgroundcolor->{
//                showCardSelectionDialog()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // ====================================   Share the pdf   ======================================
    fun createAndSharePdf(noteTitle: String, noteContent: String) {
        try {
            // Path to save the PDF (Scoped Storage)
            val pdfPath = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
            val directory = File(pdfPath ?: throw Exception("Unable to access directory"))
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Create the file
            val file = File(directory, "$noteTitle.pdf")

            // Create PDF Writer
            val writer = PdfWriter(file)
            val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
            val document = Document(pdfDocument)

            // Add content to PDF
            document.add(Paragraph(noteTitle).setBold().setFontSize(20f))
            document.add(Paragraph(noteContent).setFontSize(14f))
            document.close()

            // Share the PDF
            sharePdf(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file) // Use FileProvider
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
    private fun createPdfFile() {
        currentNote=args.note!!
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "${currentNote.noteTitle}.pdf")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    // Step 2: Handle the file location chosen by the user
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                try {
                    writePdf(uri)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Step 3: Generate and write the PDF content
    private fun writePdf(uri: Uri) {
        currentNote=args.note!!
        val pfd: ParcelFileDescriptor? = requireActivity().contentResolver.openFileDescriptor(uri, "w")
        pfd?.let {
            val outputStream = FileOutputStream(pfd.fileDescriptor)

            // Create PDFWriter and PDFDocument
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Add title and content to the PDF
            document.add(Paragraph("Title: ${currentNote.noteTitle}").setBold().setFontSize(18f))
            document.add(Paragraph(currentNote.noteBody).setFontSize(14f))

            // Close the document
            document.close()
            pfd.close()

            Toast.makeText(requireContext(), "PDF saved successfully!", Toast.LENGTH_SHORT).show()
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

                // Save color to SharedPreferences
                val sharedPref = requireActivity().getSharedPreferences("NoteBg", Context.MODE_PRIVATE)
                sharedPref.edit().putInt("selected_color", colorInt).apply()

                // Set background immediately
                binding.mainbg.setBackgroundColor(colorInt)

                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }



}