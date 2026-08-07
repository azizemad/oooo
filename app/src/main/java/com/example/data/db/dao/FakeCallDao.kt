package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.entities.FakeCallEntity
import com.example.data.db.entities.FakeChatEntity
import com.example.data.db.entities.FakeChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FakeCallDao {
    @Query("SELECT * FROM fake_calls ORDER BY timestamp DESC")
    fun getAllFakeCalls(): Flow<List<FakeCallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFakeCall(fakeCall: FakeCallEntity): Long

    @Delete
    suspend fun deleteFakeCall(fakeCall: FakeCallEntity)
}

@Dao
interface FakeChatDao {
    @Query("SELECT * FROM fake_chats ORDER BY lastTimestamp DESC")
    fun getAllFakeChats(): Flow<List<FakeChatEntity>>

    @Query("SELECT * FROM fake_chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: Long): Flow<List<FakeChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFakeChat(chat: FakeChatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: FakeChatMessageEntity): Long

    @Query("UPDATE fake_chats SET lastMessage = :lastMessage, lastTimestamp = :timestamp WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: Long, lastMessage: String, timestamp: Long)
}
