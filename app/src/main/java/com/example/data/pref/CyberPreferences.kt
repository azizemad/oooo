package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.CyberThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CyberPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cyber_dialer_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getSavedThemeMode())
    val themeMode: StateFlow<CyberThemeMode> = _themeMode

    private val _language = MutableStateFlow(prefs.getString("app_language", "EN") ?: "EN")
    val language: StateFlow<String> = _language

    private val _fontSize = MutableStateFlow(prefs.getFloat("font_size", 16f))
    val fontSize: StateFlow<Float> = _fontSize

    private val _glowIntensity = MutableStateFlow(prefs.getFloat("glow_intensity", 1.0f))
    val glowIntensity: StateFlow<Float> = _glowIntensity

    private val _pinLockEnabled = MutableStateFlow(prefs.getBoolean("pin_lock_enabled", false))
    val pinLockEnabled: StateFlow<Boolean> = _pinLockEnabled

    private val _pinCode = MutableStateFlow(prefs.getString("pin_code", "1234") ?: "1234")
    val pinCode: StateFlow<String> = _pinCode

    private val _activeSimSlot = MutableStateFlow(prefs.getInt("active_sim_slot", 1))
    val activeSimSlot: StateFlow<Int> = _activeSimSlot

    private fun getSavedThemeMode(): CyberThemeMode {
        val name = prefs.getString("theme_mode", CyberThemeMode.CYBER_CYAN.name)
        return try {
            CyberThemeMode.valueOf(name ?: CyberThemeMode.CYBER_CYAN.name)
        } catch (e: Exception) {
            CyberThemeMode.CYBER_CYAN
        }
    }

    fun setThemeMode(mode: CyberThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        _language.value = lang
    }

    fun setFontSize(size: Float) {
        prefs.edit().putFloat("font_size", size).apply()
        _fontSize.value = size
    }

    fun setGlowIntensity(intensity: Float) {
        prefs.edit().putFloat("glow_intensity", intensity).apply()
        _glowIntensity.value = intensity
    }

    fun setPinLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("pin_lock_enabled", enabled).apply()
        _pinLockEnabled.value = enabled
    }

    fun setPinCode(code: String) {
        prefs.edit().putString("pin_code", code).apply()
        _pinCode.value = code
    }

    fun setActiveSimSlot(slot: Int) {
        prefs.edit().putInt("active_sim_slot", slot).apply()
        _activeSimSlot.value = slot
    }
}
