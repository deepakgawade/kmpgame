package org.example.kmpgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _greetingList = MutableStateFlow<List<String>>(listOf())
    val getGreetingList: StateFlow<List<String>> get() = _greetingList

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _userCreatedEvent = MutableSharedFlow<String>(
    )
    val userCreated = _userCreatedEvent.asSharedFlow()


    init {
        viewModelScope.launch {
            Greeting().greet().collect { phrase -> _greetingList.update { list -> list + phrase } }
        }
    }

    fun onCreateUserClick(name: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = Greeting().createUser(name, email)

                if (user != null) {
                    _greetingList.update { list ->
                        list + "User Created Successfully"
                    }
                    _userCreatedEvent.emit("Account created successfully")

                } else {
                    _greetingList.update { list ->
                        list + "User creation failed"
                    }
                }
            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }

        }
    }
}