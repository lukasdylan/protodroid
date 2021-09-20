package id.lukasdylan.grpc.protodroid.internal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Created by Lukas Dylan on 05/08/20.
 */
@Dao
internal interface ProtodroidDao {

    @Query("SELECT * FROM protodroiddataentity ORDER BY ID DESC")
    fun fetchAllData(): Flow<List<ProtodroidDataEntity>>

    @Query("SELECT * FROM protodroiddataentity WHERE id = :dataId LIMIT 1")
    fun fetchSingleDataById(dataId: Long): Flow<ProtodroidDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(entity: ProtodroidDataEntity): Long

    @Query("DELETE FROM protodroiddataentity")
    suspend fun deleteAllData()
}