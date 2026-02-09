package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _isNewUser = MutableStateFlow<Boolean>(false)
    val isNewUser: StateFlow<Boolean> = _isNewUser

    init {
        viewModelScope.launch {
            _user.value = authRepository.getCurrentUser()
            _user.value?.let {
                _userRole.value = authRepository.getUserRole(it.uid)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.signIn(email, password)
                _user.value = result
                _user.value?.let {
                    _userRole.value = authRepository.getUserRole(it.uid)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.signUp(name, email, password)
                _user.value = result
                _isNewUser.value = true
                // We don't fetch role yet because it's default "customer" and 
                // we want them to pick a specific role.
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun setRole(role: String) {
        viewModelScope.launch {
            _user.value?.let { user ->
                try {
                    // Update role in Firestore via repository
                    // Note: AuthRepository needs an updateRole function
                    _userRole.value = role
                    _isNewUser.value = false
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _user.value = null
            _userRole.value = null
            _isNewUser.value = false
        }
    }
}
