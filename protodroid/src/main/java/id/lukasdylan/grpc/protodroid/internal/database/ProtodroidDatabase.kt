package id.lukasdylan.grpc.protodroid.internal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProtodroidDataEntity::class], version = 1, exportSchema = false)
internal abstract class ProtodroidDatabase : RoomDatabase() {

    abstract fun protodroidDao(): ProtodroidDao

    companion object {
        fun initDatabase(context: Context): ProtodroidDatabase {
            return Room.databaseBuilder(context, ProtodroidDatabase::class.java, "Protodroid.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}