package id.lukasdylan.grpc.protodroid.internal.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Lukas Dylan on 05/08/20.
 */
@Dao
internal interface ProtodroidDao {

    @Query("SELECT * FROM protodroiddataentity ORDER BY ID DESC")
    fun fetchAllData(): LiveData<List<ProtodroidDataEntity>>

    @Query("SELECT * FROM protodroiddataentity WHERE id = :dataId LIMIT 1")
    fun fetchSingleDataById(dataId: Long): LiveData<ProtodroidDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(entity: ProtodroidDataEntity): Long

    @Query("DELETE FROM protodroiddataentity")
    fun deleteAllData()
}