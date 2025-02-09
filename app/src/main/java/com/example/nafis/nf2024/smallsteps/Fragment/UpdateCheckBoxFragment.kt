package com.example.nafis.nf2024.smallsteps.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxViewHolder
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxItemClick
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentUpdateCheckBoxBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates


class UpdateCheckBoxFragment : Fragment() {
    private lateinit var binding: FragmentUpdateCheckBoxBinding
    private lateinit var currentNote: CheckBoxNote
    private val arg: UpdateCheckBoxFragmentArgs by navArgs()
    private lateinit var checkBoxViewModel: CheckBoxViewModel
    private lateinit var list: ArrayList<checkbox>
    private var color by Delegates.notNull<Int>()
    private lateinit var checkBoxAdapter: CheckBoxAdapter
    private lateinit var checkboxContainer: LinearLayout
    private val CREATE_FILE_REQUEST_CODE = 1


    private val callback by lazy{
        object :CheckBoxItemClick{
            override fun onCheckClick(note: checkbox) {
                TODO("Not yet implemented")
            }

            override fun onDeleteClick(pos: Int, note: checkbox) {
                list.removeAt(pos) // Update the list
                checkBoxAdapter.submitList(ArrayList(list))
                updateCheckBox(true)
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
        binding = FragmentUpdateCheckBoxBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Update CheckNote"
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)
                ?.apply {
                    setTint(requireContext().getColor(R.color.white))
                }
        )

        currentNote = arg.checknote
        checkBoxViewModel = (activity as MainActivity).checkNoteViewModel

        setAllLayout()


        binding.addcheckbox.setOnClickListener {
            addNewCheckbox()
        }
        binding.backgroundcolorchange.setOnClickListener {
            showBottomDialog()
        }

        binding.fabDoneCheckbox.setOnClickListener {
            updateAllCheckboxText()
            updateCheckBox()
        }
        binding.root.setOnTouchListener { _, _ ->
            clearFocusFromEditText()
            true
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

    private fun clearFocusFromEditText() {
        val currentFocusView = activity?.currentFocus
        if (currentFocusView != null) {
            currentFocusView.clearFocus()
            hideKeyboard(currentFocusView)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setAllLayout() {
        binding.etNoteTitle.setText(currentNote.title)
        binding.date.setText(currentNote.dateCreated)

        try {
            color = android.graphics.Color.parseColor(currentNote.backgroundColor)
            binding.mainbg.setBackgroundColor(color)  // Use setBackgroundColor instead of setBackgroundResource
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            color = android.graphics.Color.WHITE  // Fallback to white if parsing fails
            binding.mainbg.setBackgroundColor(color)
        }

        checkboxContainer = binding.checkboxContainer

        list = ArrayList()
        list.clear()
        list = currentNote.checkBoxes
        binding.checkboxRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.checkboxRecyclerView.isNestedScrollingEnabled = false
        checkBoxAdapter = CheckBoxAdapter(callback)
        binding.checkboxRecyclerView.adapter = checkBoxAdapter
        checkBoxAdapter.submitList(currentNote.checkBoxes)
    }


    //================   update the checkbox note    =======================
    private fun updateCheckBox( isdelete:Boolean=false) {
        val currentDate = currentNote.dateCreated
        val title = binding.etNoteTitle.text.toString().trim()

        // Convert HEX color safely
        color = try {
            android.graphics.Color.parseColor(currentNote.backgroundColor)
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.WHITE // Default to white if parsing fails
        }

        if (title.isNotEmpty()) {
            val updatedList = ArrayList(list.filter { it.text.isNotBlank() })
            val note = CheckBoxNote(
                id = currentNote.id, // Preserve existing ID
                title = title,
                checkBoxes = updatedList,
                backgroundColor = String.format(
                    "#%06X",
                    0xFFFFFF and color
                ), // Convert color back to HEX string
                dateCreated = currentDate
            )
            checkBoxViewModel.updateCheckBoxNote(note)



          if(!isdelete) {
              Toast.makeText(
                  context,
                  "Note Updated Successfully",
                  Toast.LENGTH_LONG
              ).show()
              val bundle = Bundle().apply {
                  putInt("selectedTabIndex", 1) // 1 for "CheckList" tab
              }
              requireParentFragment().findNavController()
                  .navigate(R.id.action_updateCheckBoxFragment_to_noteTypeFragment, bundle)
          }
        } else {
            Toast.makeText(
                context,
                "Please enter note Title",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    //================update the checkbox note=======================


    //===============add check box =============================
    private fun addNewCheckbox() {
        val newCheckbox = checkbox(id = list.size + 1, isChecked = false, text = "")
        list.add(newCheckbox) // Add new checkbox to the list
        checkBoxAdapter.submitList(ArrayList(list)) // Notify adapter that data has changed
    }
    //===============add check box =============================


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
                color = colorInt
                // Set background immediately
                binding.mainbg.setBackgroundColor(colorInt)
                currentNote.backgroundColor = String.format("#%06X", 0xFFFFFF and colorInt)

                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
    // ===================Change the BackGround of the Note===============================


    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {

            setTitle("Delete Note")
            setMessage("You want to delete this Note?")
            setPositiveButton("Delete") { _, _ ->
                checkBoxViewModel.deleteCheckBoxNote(currentNote)
                val bundle = Bundle().apply {
                    putInt("selectedTabIndex", 1) // 1 for "CheckList" tab
                }
                view?.findNavController()?.navigate(
                    R.id.action_updateCheckBoxFragment_to_noteTypeFragment,bundle
                )

            }
            setNegativeButton("Cancel", null)
        }.create().show()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                return true
            }

            R.id.menu_delete -> {
                deleteNote()
            }

            R.id.share -> { // Handle "More" button
                currentNote = arg.checknote
                createAndSharePdf(
                    currentNote.title,
                    currentNote.checkBoxes,
                    currentNote.dateCreated
                )
                return true
            }

            R.id.exportUpdatefile -> {
                createPdfFile()
                return true
            }

//            R.id.backgroundcolor -> {
////                showCardSelectionDialog()
//                return true
//            }

        }
        return super.onOptionsItemSelected(item)
    }


    // ====================================   Share the pdf   ======================================

    fun createAndSharePdf(
        noteTitle: String,
        noteContent1: ArrayList<checkbox>, // List of checkbox items
        createdDate: String
    ) {
        try {
            // 📂 Define PDF save path
            val pdfPath = context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
            val directory = File(pdfPath ?: throw Exception("Unable to access directory"))
            if (!directory.exists()) directory.mkdirs()

            val file = File(directory, "$noteTitle.pdf")

            // 🖊 Create PDF Writer & Document
            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            // 📆 Generate Timestamp
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

            // 📝 Title on Left, Date on Right
            val titleDateTable = Table(floatArrayOf(1f, 1f)) // Two-column table
            titleDateTable.setWidth(UnitValue.createPercentValue(100f))

            titleDateTable.addCell(Cell().add(Paragraph(noteTitle).setBold().setFontSize(20f)).setBorder(
                Border.NO_BORDER))
            titleDateTable.addCell(Cell().add(Paragraph(createdDate).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER))

            document.add(titleDateTable)

            // 🔹 Add Bold Line Below Title & Date
            document.add(LineSeparator(SolidLine())) // ✅ Correct

            // ✅❌ Add Checkboxes List (with space after each checkbox)
            for (item in noteContent1) {
                val checkboxSymbol = if (item.isChecked) "[ _/ ]" else "[  ]" // ☑ or ☐
                document.add(
                    Paragraph("$checkboxSymbol  ${item.text}")
                        .setFontSize(12f)
                )

            }


            // 🔹 Add Bold Line at Bottom
            document.add(LineSeparator(SolidLine())) // ✅ Correct

            // 📆 Centered Generated Date at Bottom
            document.add(
                Paragraph("Generated on: $timestamp")
                    .setBold()
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.CENTER)
            )

            document.close()

            // 🚀 Share PDF
            sharePdf(file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
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
        currentNote=arg.checknote
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "${currentNote.title}.pdf")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    // Step 2: Handle the file location chosen by the user
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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
        try {
            val pfd: ParcelFileDescriptor? = requireActivity().contentResolver.openFileDescriptor(uri, "w")
            pfd?.let {
                val outputStream = FileOutputStream(pfd.fileDescriptor)
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(pdfWriter)
                val document = Document(pdfDocument)

                // 📆 Generate Timestamp
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                // 📝 Title on Left, Date on Right
                val titleDateTable = Table(floatArrayOf(1f, 1f)) // Two-column table
                titleDateTable.setWidth(UnitValue.createPercentValue(100f))

                titleDateTable.addCell(Cell().add(Paragraph(currentNote.title).setBold().setFontSize(20f)).setBorder(
                    Border.NO_BORDER))
                titleDateTable.addCell(Cell().add(Paragraph(currentNote.dateCreated).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER))

                document.add(titleDateTable)

                // 🔹 Add Bold Line Below Title & Date
                document.add(LineSeparator(SolidLine())) // ✅ Correct

                // ✅❌ Add Checkboxes List (with space after each checkbox)
                for (item in currentNote.checkBoxes) {
                    val checkboxSymbol = if (item.isChecked) "[ _/ ]" else "[  ]" // ☑ or ☐
                    document.add(
                        Paragraph("$checkboxSymbol  ${item.text}")
                            .setFontSize(12f)
                    )

                }


                // 🔹 Add Bold Line at Bottom
                document.add(LineSeparator(SolidLine())) // ✅ Correct

                // 📆 Centered Generated Date at Bottom
                document.add(
                    Paragraph("Generated on: $timestamp")
                        .setBold()
                        .setFontSize(10f)
                        .setTextAlignment(TextAlignment.CENTER)
                )

                document.close()
                pfd.close()

                Toast.makeText(requireContext(), "PDF saved successfully!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // ====================================   Export the pdf   ======================================




}