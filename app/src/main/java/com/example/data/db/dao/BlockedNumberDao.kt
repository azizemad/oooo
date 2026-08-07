package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.entities.BlockedNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedNumberDao {
    @Query("SELECT * FROM blocked_numbers ORDER BY timestamp DESC")
    fun getAllBlockedNumbers(): Flow<List<BlockedNumberEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_numbers WHERE phoneNumber = :phoneNumber)")
    suspend fun isBlocked(phoneNumber: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedNumber(blockedNumber: BlockedNumberEntity): Long

    @Delete
    suspend fun deleteBlockedNumber(blockedNumber: BlockedNumberEntity)
}
