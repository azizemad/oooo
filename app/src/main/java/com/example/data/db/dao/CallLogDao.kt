package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.entities.CallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs WHERE callType = :type ORDER BY timestamp DESC")
    fun getCallLogsByType(type: String): Flow<List<CallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLogEntity): Long

    @Delete
    suspend fun deleteCallLog(callLog: CallLogEntity)

    @Query("DELETE FROM call_logs")
    suspend fun clearCallLogs()
}
