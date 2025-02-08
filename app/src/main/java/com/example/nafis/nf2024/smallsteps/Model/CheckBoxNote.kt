            package com.example.nafis.nf2024.smallsteps.Model

            import android.os.Parcelable
            import androidx.room.Entity
            import androidx.room.PrimaryKey
            import androidx.room.TypeConverters
            import kotlinx.android.parcel.Parcelize

            @Entity(tableName = "checkbox_note")
            @Parcelize
            @TypeConverters(CheckBoxConverter::class)
            data class CheckBoxNote(
                @PrimaryKey(autoGenerate = true)
                val id: Int = 0,
                val title: String,
                val checkBoxes: ArrayList<checkbox>,  // List of checkboxes
                var backgroundColor: String,   // Store color as HEX (e.g., "#FFFFFF")
                val dateCreated: String
            ) : Parcelable


