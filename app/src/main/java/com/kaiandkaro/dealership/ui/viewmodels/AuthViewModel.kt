package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kaiandkaro.dealership.DealershipApp
import com.kaiandkaro.dealership.ADMIN_EMAIL
import com.kaiandkaro.dealership.models.User
import com.kaiandkaro.dealership.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _isNewUser = MutableStateFlow(false)
    val isNewUser: StateFlow<Boolean> = _isNewUser

    private val _isInitDone = MutableStateFlow(false)
    val isInitDone: StateFlow<Boolean> = _isInitDone

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    _user.value = currentUser
                    // Immediate admin check
                    if (currentUser.email?.lowercase() == ADMIN_EMAIL.lowercase()) {
                        _userRole.value = "admin"
                    } else {
                        val data = authRepository.getUserData(currentUser.uid)
                        _userData.value = data
                        _userRole.value = data?.role
                    }
                }
            } catch (e: Exception) {
                // Silently ignore init errors
            } finally {
                _isInitDone.value = true
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _error.value = null
                val result = authRepository.signIn(email, password)
                if (result != null) {
                    _user.value = result
                    if (result.email?.lowercase() == ADMIN_EMAIL.lowercase()) {
                        _userRole.value = "admin"
                    } else {
                        val data = authRepository.getUserData(result.uid)
                        _userData.value = data
                        _userRole.value = data?.role
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _error.value = null
                val result = authRepository.signUp(name, email, password)
                if (result != null) {
                    _user.value = result
                    if (result.email?.lowercase() == ADMIN_EMAIL.lowercase()) {
                        _userRole.value = "admin"
                        _isNewUser.value = false
                        authRepository.updateUserRole(result.uid, "admin")
                    } else {
                        _isNewUser.value = true
                        _userRole.value = "" 
                    }
                    _userData.value = authRepository.getUserData(result.uid)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setRole(role: String) {
        viewModelScope.launch {
            _user.value?.let { user ->
                _isLoading.value = true
                try {
                    authRepository.updateUserRole(user.uid, role)
                    _userRole.value = role
                    _userData.value = _userData.value?.copy(role = role)
                    _isNewUser.value = false
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _user.value = null
            _userData.value = null
            _userRole.value = null
            _isNewUser.value = false
            _error.value = null
        }
    }
}
