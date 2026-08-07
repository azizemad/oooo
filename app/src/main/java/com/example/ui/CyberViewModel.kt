package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.CyberDatabase
import com.example.data.db.entities.*
import com.example.data.pref.CyberPreferences
import com.example.ui.theme.CyberThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class CyberScreen {
    object Splash : CyberScreen()
    object Lock : CyberScreen()
    object Main : CyberScreen()
    data class ActiveCall(val name: String, val number: String, val isIncoming: Boolean) : CyberScreen()
    data class FakeChatDetail(val chatId: Long, val name: String) : CyberScreen()
}

data class ActiveCallState(
    val callerName: String = "",
    val phoneNumber: String = "",
    val isIncoming: Boolean = false,
    val isConnected: Boolean = false,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isBluetoothOn: Boolean = false,
    val isOnHold: Boolean = false,
    val isRecording: Boolean = false
)

class CyberViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CyberDatabase.getDatabase(application)
    val repository = com.example.data.repository.CyberRepository(db)
    val preferences = CyberPreferences(application)

    // Navigation state
    private val _currentScreen = MutableStateFlow<CyberScreen>(CyberScreen.Splash)
    val currentScreen: StateFlow<CyberScreen> = _currentScreen

    // Unlock status
    private val _isUnlocked = MutableStateFlow(!preferences.pinLockEnabled.value)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked

    // Preferences
    val themeMode = preferences.themeMode
    val language = preferences.language
    val activeSimSlot = preferences.activeSimSlot

    // T9 / Dialpad input
    private val _dialPadInput = MutableStateFlow("")
    val dialPadInput: StateFlow<String> = _dialPadInput

    // Active Call
    private val _activeCallState = MutableStateFlow(ActiveCallState())
    val activeCallState: StateFlow<ActiveCallState> = _activeCallState

    private var callTimerJob: Job? = null

    // Room DB Streams
    val contacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteContacts: StateFlow<List<ContactEntity>> = repository.favoriteContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogs: StateFlow<List<CallLogEntity>> = repository.allCallLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedNumbers: StateFlow<List<BlockedNumberEntity>> = repository.allBlockedNumbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fakeCalls: StateFlow<List<FakeCallEntity>> = repository.allFakeCalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fakeChats: StateFlow<List<FakeChatEntity>> = repository.allFakeChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // T9 Matched Contacts
    val t9Matches: StateFlow<List<ContactEntity>> = _dialPadInput
        .flatMapLatest { input ->
            if (input.isEmpty()) flowOf(emptyList())
            else repository.searchContacts(input)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: CyberScreen) {
        _currentScreen.value = screen
    }

    fun finishSplash() {
        if (preferences.pinLockEnabled.value && !_isUnlocked.value) {
            _currentScreen.value = CyberScreen.Lock
        } else {
            _currentScreen.value = CyberScreen.Main
        }
    }

    fun unlockWithPin(enteredPin: String): Boolean {
        if (enteredPin == preferences.pinCode.value) {
            _isUnlocked.value = true
            _currentScreen.value = CyberScreen.Main
            return true
        }
        return false
    }

    fun appendDialPadDigit(digit: String) {
        _dialPadInput.value += digit
    }

    fun deleteDialPadDigit() {
        if (_dialPadInput.value.isNotEmpty()) {
            _dialPadInput.value = _dialPadInput.value.dropLast(1)
        }
    }

    fun clearDialPad() {
        _dialPadInput.value = ""
    }

    fun setDialPadInput(number: String) {
        _dialPadInput.value = number
    }

    // Call Simulation / Trigger
    fun startCall(name: String, number: String, isIncoming: Boolean = false) {
        val callerName = name.ifEmpty { number.ifEmpty { "Unknown Cyber Contact" } }
        val phoneNum = number.ifEmpty { "+1 800-CYBER" }

        _activeCallState.value = ActiveCallState(
            callerName = callerName,
            phoneNumber = phoneNum,
            isIncoming = isIncoming,
            isConnected = !isIncoming,
            durationSeconds = 0
        )

        if (!isIncoming) {
            startCallTimer()
        }

        _currentScreen.value = CyberScreen.ActiveCall(callerName, phoneNum, isIncoming)
    }

    fun answerCall() {
        _activeCallState.value = _activeCallState.value.copy(
            isIncoming = false,
            isConnected = true
        )
        startCallTimer()
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeCallState.value = _activeCallState.value.copy(
                    durationSeconds = _activeCallState.value.durationSeconds + 1
                )
            }
        }
    }

    fun endCall() {
        callTimerJob?.cancel()
        val current = _activeCallState.value
        viewModelScope.launch {
            if (current.phoneNumber.isNotEmpty()) {
                val callType = if (current.isIncoming && !current.isConnected) CallType.MISSED
                               else if (current.isIncoming) CallType.INCOMING
                               else CallType.OUTGOING

                repository.insertCallLog(
                    CallLogEntity(
                        phoneNumber = current.phoneNumber,
                        callerName = current.callerName,
                        callType = callType,
                        timestamp = System.currentTimeMillis(),
                        durationSeconds = current.durationSeconds,
                        simSlot = preferences.activeSimSlot.value
                    )
                )
            }
        }
        _currentScreen.value = CyberScreen.Main
    }

    fun toggleMute() {
        _activeCallState.value = _activeCallState.value.copy(isMuted = !_activeCallState.value.isMuted)
    }

    fun toggleSpeaker() {
        _activeCallState.value = _activeCallState.value.copy(isSpeakerOn = !_activeCallState.value.isSpeakerOn)
    }

    fun toggleBluetooth() {
        _activeCallState.value = _activeCallState.value.copy(isBluetoothOn = !_activeCallState.value.isBluetoothOn)
    }

    fun toggleHold() {
        _activeCallState.value = _activeCallState.value.copy(isOnHold = !_activeCallState.value.isOnHold)
    }

    fun toggleRecord() {
        _activeCallState.value = _activeCallState.value.copy(isRecording = !_activeCallState.value.isRecording)
    }

    // Schedule Fake Call
    fun scheduleFakeCall(name: String, number: String, delaySeconds: Int, ringtone: String = "Cyber Matrix") {
        viewModelScope.launch {
            val fakeCall = FakeCallEntity(
                callerName = name.ifEmpty { "Fake Caller" },
                phoneNumber = number.ifEmpty { "+1 555-0199" },
                delaySeconds = delaySeconds,
                ringtoneName = ringtone
            )
            repository.insertFakeCall(fakeCall)

            // Trigger after delay
            launch {
                delay(delaySeconds * 1000L)
                startCall(fakeCall.callerName, fakeCall.phoneNumber, isIncoming = true)
            }
        }
    }

    // CRUD Contacts
    fun addContact(name: String, phone: String, email: String, category: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertContact(
                ContactEntity(
                    name = name,
                    phoneNumber = phone,
                    email = email,
                    category = category,
                    colorHex = colorHex
                )
            )
        }
    }

    fun toggleFavorite(contact: ContactEntity) {
        viewModelScope.launch {
            repository.updateContact(contact.copy(isFavorite = !contact.isFavorite))
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    // Call Log Actions
    fun deleteCallLog(log: CallLogEntity) {
        viewModelScope.launch {
            repository.deleteCallLog(log)
        }
    }

    fun clearAllCallLogs() {
        viewModelScope.launch {
            repository.clearCallLogs()
        }
    }

    // Call Blocker Actions
    fun blockNumber(number: String, reason: String) {
        viewModelScope.launch {
            repository.insertBlockedNumber(BlockedNumberEntity(phoneNumber = number, reason = reason))
        }
    }

    fun unblockNumber(blockedNumber: BlockedNumberEntity) {
        viewModelScope.launch {
            repository.deleteBlockedNumber(blockedNumber)
        }
    }

    // Fake Chat Actions
    fun createFakeChat(contactName: String, phone: String, style: ChatStyle) {
        viewModelScope.launch {
            val chatId = repository.insertFakeChat(
                FakeChatEntity(
                    contactName = contactName,
                    phoneNumber = phone,
                    chatStyle = style,
                    lastMessage = "Chat initialized."
                )
            )
            _currentScreen.value = CyberScreen.FakeChatDetail(chatId, contactName)
        }
    }

    fun sendFakeChatMessage(chatId: Long, text: String, isOutgoing: Boolean) {
        viewModelScope.launch {
            repository.insertFakeChatMessage(
                FakeChatMessageEntity(
                    chatId = chatId,
                    text = text,
                    isOutgoing = isOutgoing
                )
            )
        }
    }

    fun setThemeMode(mode: CyberThemeMode) {
        preferences.setThemeMode(mode)
    }

    fun setLanguage(lang: String) {
        preferences.setLanguage(lang)
    }

    fun setFontSize(size: Float) {
        preferences.setFontSize(size)
    }

    fun setGlowIntensity(intensity: Float) {
        preferences.setGlowIntensity(intensity)
    }

    fun setPinLockEnabled(enabled: Boolean) {
        preferences.setPinLockEnabled(enabled)
    }

    fun setPinCode(code: String) {
        preferences.setPinCode(code)
    }

    fun setActiveSimSlot(slot: Int) {
        preferences.setActiveSimSlot(slot)
    }
}
