package com.example.nafis.nf2024.smallsteps.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nafis.nf2024.smallsteps.Model.Notes

@Dao
interface NoteDAO {

//    DAO- Data Access Object
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note:Notes)

    @Delete
    suspend fun deleteNote(note: Notes)

    @Update
    suspend fun updateNote(note: Notes)

    @Query("SELECT * FROM NOTES ORDER BY ID DESC")
    fun getAllNote():LiveData<List<Notes>>

    @Query("SELECT * FROM notes WHERE noteTitle LIKE :query OR noteBody LIKE :query ORDER BY noteTitle COLLATE NOCASE ASC")
    fun searchNote(query: String): LiveData<List<Notes>>

}