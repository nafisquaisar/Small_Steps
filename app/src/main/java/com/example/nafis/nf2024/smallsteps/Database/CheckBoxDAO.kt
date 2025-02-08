package com.example.nafis.nf2024.smallsteps.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote

@Dao
interface CheckBoxDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckBoxNote(note: CheckBoxNote)

    @Delete
    suspend fun deleteCheckBoxNote(note: CheckBoxNote)

    @Update
    suspend fun updateCheckBoxNote(note: CheckBoxNote)

    @Query("SELECT * FROM checkbox_note ORDER BY id DESC")
    fun getAllCheckBoxNotes(): LiveData<List<CheckBoxNote>>

    @Query("SELECT * FROM checkbox_note WHERE title LIKE '%' || :query || '%' OR checkBoxes LIKE '%' || :query || '%' ORDER BY title COLLATE NOCASE ASC")
    fun searchCheckBoxNote(query: String): LiveData<List<CheckBoxNote>>

}
