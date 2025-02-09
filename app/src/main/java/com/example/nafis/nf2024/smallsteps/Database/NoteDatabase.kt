package com.example.nafis.nf2024.smallsteps.Database


import com.example.nafis.nf2024.smallsteps.NoteContentConverters
import android.content.Context
import androidx.room.*
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxConverter
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.Notes

@Database(entities = [Notes::class, CheckBoxNote::class], version = 2)
@TypeConverters(CheckBoxConverter::class, NoteContentConverters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDAO
    abstract fun getCheckBoxDao(): CheckBoxDAO

    companion object {
        @Volatile
        private var instance: NoteDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "note_db"
            ).build()


    }
}
