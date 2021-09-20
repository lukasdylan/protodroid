package id.lukasdylan.grpc.protodroid.internal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ProtodroidDataEntity::class], version = 2)
internal abstract class ProtodroidDatabase : RoomDatabase() {

    abstract fun protodroidDao(): ProtodroidDao

    companion object {
        fun initDatabase(context: Context): ProtodroidDatabase {
            return Room.databaseBuilder(context, ProtodroidDatabase::class.java, "Protodroid.db")
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE protodroiddataentity ADD COLUMN status_level INTEGER")
    }
}