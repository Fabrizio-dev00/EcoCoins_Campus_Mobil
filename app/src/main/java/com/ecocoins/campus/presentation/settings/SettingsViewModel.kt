package com.ecocoins.campus.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _notificationsEnabled = MutableLiveData<Boolean>(true)
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled

    private val _darkModeEnabled = MutableLiveData<Boolean>(false)
    val darkModeEnabled: LiveData<Boolean> = _darkModeEnabled

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val name = userPreferences.userName.firstOrNull() ?: ""
            val email = userPreferences.userEmail.firstOrNull() ?: ""

            _userName.value = name
            _userEmail.value = email
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        // Aquí puedes guardar la preferencia en DataStore si lo deseas
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        // Aquí puedes guardar la preferencia en DataStore si lo deseas
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}