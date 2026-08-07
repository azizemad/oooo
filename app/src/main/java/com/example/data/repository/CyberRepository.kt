package com.example.data.repository

import com.example.data.db.CyberDatabase
import com.example.data.db.entities.*
import kotlinx.coroutines.flow.Flow

class CyberRepository(private val db: CyberDatabase) {

    // Contacts
    val allContacts: Flow<List<ContactEntity>> = db.contactDao().getAllContacts()
    val favoriteContacts: Flow<List<ContactEntity>> = db.contactDao().getFavoriteContacts()

    fun searchContacts(query: String): Flow<List<ContactEntity>> = db.contactDao().searchContacts(query)

    suspend fun insertContact(contact: ContactEntity): Long = db.contactDao().insertContact(contact)
    suspend fun updateContact(contact: ContactEntity) = db.contactDao().updateContact(contact)
    suspend fun deleteContact(contact: ContactEntity) = db.contactDao().deleteContact(contact)
    suspend fun deleteContactById(id: Long) = db.contactDao().deleteContactById(id)
    suspend fun getContactByPhone(phone: String): ContactEntity? = db.contactDao().getContactByPhone(phone)

    // Call Logs
    val allCallLogs: Flow<List<CallLogEntity>> = db.callLogDao().getAllCallLogs()

    suspend fun insertCallLog(callLog: CallLogEntity): Long = db.callLogDao().insertCallLog(callLog)
    suspend fun deleteCallLog(callLog: CallLogEntity) = db.callLogDao().deleteCallLog(callLog)
    suspend fun clearCallLogs() = db.callLogDao().clearCallLogs()

    // Blocked Numbers
    val allBlockedNumbers: Flow<List<BlockedNumberEntity>> = db.blockedNumberDao().getAllBlockedNumbers()

    suspend fun isBlocked(phoneNumber: String): Boolean = db.blockedNumberDao().isBlocked(phoneNumber)
    suspend fun insertBlockedNumber(blockedNumber: BlockedNumberEntity): Long = db.blockedNumberDao().insertBlockedNumber(blockedNumber)
    suspend fun deleteBlockedNumber(blockedNumber: BlockedNumberEntity) = db.blockedNumberDao().deleteBlockedNumber(blockedNumber)

    // Fake Calls
    val allFakeCalls: Flow<List<FakeCallEntity>> = db.fakeCallDao().getAllFakeCalls()

    suspend fun insertFakeCall(fakeCall: FakeCallEntity): Long = db.fakeCallDao().insertFakeCall(fakeCall)
    suspend fun deleteFakeCall(fakeCall: FakeCallEntity) = db.fakeCallDao().deleteFakeCall(fakeCall)

    // Fake Chats
    val allFakeChats: Flow<List<FakeChatEntity>> = db.fakeChatDao().getAllFakeChats()

    fun getMessagesForChat(chatId: Long): Flow<List<FakeChatMessageEntity>> = db.fakeChatDao().getMessagesForChat(chatId)

    suspend fun insertFakeChat(chat: FakeChatEntity): Long = db.fakeChatDao().insertFakeChat(chat)
    suspend fun insertFakeChatMessage(message: FakeChatMessageEntity) {
        db.fakeChatDao().insertMessage(message)
        db.fakeChatDao().updateLastMessage(message.chatId, message.text, message.timestamp)
    }
}
