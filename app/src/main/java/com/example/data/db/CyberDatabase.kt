package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.db.dao.*
import com.example.data.db.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ContactEntity::class,
        CallLogEntity::class,
        BlockedNumberEntity::class,
        FakeCallEntity::class,
        FakeChatEntity::class,
        FakeChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CyberDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun callLogDao(): CallLogDao
    abstract fun blockedNumberDao(): BlockedNumberDao
    abstract fun fakeCallDao(): FakeCallDao
    abstract fun fakeChatDao(): FakeChatDao

    companion object {
        @Volatile
        private var INSTANCE: CyberDatabase? = null

        fun getDatabase(context: Context): CyberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberDatabase::class.java,
                    "cyber_dialer_db"
                )
                .addCallback(CyberDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class CyberDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: CyberDatabase) {
                // Populate sample contacts
                val sampleContacts = listOf(
                    ContactEntity(name = "Aziz Emad", phoneNumber = "+1 800-555-0199", email = "aziz@cyberdialer.io", isFavorite = true, category = "Developer", colorHex = "#00F0FF", note = "Creator of CyberDialer Pro"),
                    ContactEntity(name = "Cyber Command", phoneNumber = "+1 888-999-0000", email = "command@matrix.net", isFavorite = true, category = "Security", colorHex = "#00FF66", note = "Primary HQ"),
                    ContactEntity(name = "Elena Rostova", phoneNumber = "+44 20 7946 0912", email = "elena@techcorp.com", isFavorite = true, category = "Work", colorHex = "#BF00FF", note = "Lead System Architect"),
                    ContactEntity(name = "Sarah Connor", phoneNumber = "+1 212-555-0143", email = "sarah@sky.net", isFavorite = false, category = "VIP", colorHex = "#FF0055", note = "Emergency contact"),
                    ContactEntity(name = "Neo Anderson", phoneNumber = "+1 312-555-0177", email = "neo@construct.org", isFavorite = true, category = "Cyber", colorHex = "#00FF66", note = "The One"),
                    ContactEntity(name = "Marcus Vance", phoneNumber = "+1 415-555-0188", email = "marcus@vance.io", isFavorite = false, category = "Personal", colorHex = "#0077FF", note = "DevOps Engineer"),
                    ContactEntity(name = "Agent Smith", phoneNumber = "+1 800-000-0000", email = "smith@system.gov", isFavorite = false, category = "Blocked", colorHex = "#FF9900", note = "Rogue Process")
                )
                sampleContacts.forEach { db.contactDao().insertContact(it) }

                // Populate sample call logs
                val now = System.currentTimeMillis()
                val hour = 3600000L
                val sampleCalls = listOf(
                    CallLogEntity(phoneNumber = "+1 800-555-0199", callerName = "Aziz Emad", callType = CallType.INCOMING, timestamp = now - 10 * 60000, durationSeconds = 142, simSlot = 1),
                    CallLogEntity(phoneNumber = "+44 20 7946 0912", callerName = "Elena Rostova", callType = CallType.OUTGOING, timestamp = now - 1 * hour, durationSeconds = 305, simSlot = 1),
                    CallLogEntity(phoneNumber = "+1 212-555-0143", callerName = "Sarah Connor", callType = CallType.MISSED, timestamp = now - 3 * hour, durationSeconds = 0, simSlot = 2),
                    CallLogEntity(phoneNumber = "+1 999-888-7777", callerName = "Unknown Telemarketer", callType = CallType.BLOCKED, timestamp = now - 5 * hour, durationSeconds = 0, simSlot = 1),
                    CallLogEntity(phoneNumber = "+1 312-555-0177", callerName = "Neo Anderson", callType = CallType.INCOMING, timestamp = now - 24 * hour, durationSeconds = 512, simSlot = 1)
                )
                sampleCalls.forEach { db.callLogDao().insertCallLog(it) }

                // Populate blocked numbers
                val blocked = listOf(
                    BlockedNumberEntity(phoneNumber = "+1 999-888-7777", reason = "Known Telemarketing Spam"),
                    BlockedNumberEntity(phoneNumber = "+1 800-000-0000", reason = "Robocaller")
                )
                blocked.forEach { db.blockedNumberDao().insertBlockedNumber(it) }

                // Populate sample fake chats
                val chatId1 = db.fakeChatDao().insertFakeChat(
                    FakeChatEntity(contactName = "Matrix HQ", phoneNumber = "+1 888-999-0000", chatStyle = ChatStyle.WHATSAPP, lastMessage = "System breach prevented.", lastTimestamp = now)
                )
                db.fakeChatDao().insertMessage(FakeChatMessageEntity(chatId = chatId1, text = "Cyber status update?", isOutgoing = true, timestamp = now - 300000))
                db.fakeChatDao().insertMessage(FakeChatMessageEntity(chatId = chatId1, text = "System breach prevented.", isOutgoing = false, timestamp = now))

                val chatId2 = db.fakeChatDao().insertFakeChat(
                    FakeChatEntity(contactName = "Elena Rostova", phoneNumber = "+44 20 7946 0912", chatStyle = ChatStyle.TELEGRAM, lastMessage = "Let's review the CyberDialer architecture.", lastTimestamp = now - 1800000)
                )
                db.fakeChatDao().insertMessage(FakeChatMessageEntity(chatId = chatId2, text = "Hey! Did you check the new neon UI?", isOutgoing = true, timestamp = now - 2000000))
                db.fakeChatDao().insertMessage(FakeChatMessageEntity(chatId = chatId2, text = "Let's review the CyberDialer architecture.", isOutgoing = false, timestamp = now - 1800000))
            }
        }
    }
}
