package com.dogebook.login.ui.main.register.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogebook.R
import com.dogebook.login.ui.main.register.data.RegisterRepository
import com.dogebook.login.ui.main.register.data.Result
import com.dogebook.login.ui.main.register.data.model.RegisteredUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException


class RegisterViewModel(val registerRepository: RegisterRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(user: User) {
        // can be launched in a separate asynchronous job
        var result: Result<RegisteredUser>? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = registerRepository.register(user)
        }
        if (result is Result.Success) {
            _registerResult.value =
                RegisterResult(success = RegisteredUserView(displayName = (result as Result.Success<RegisteredUser>).data.displayName))
        } else {
            //_registerResult.value = RegisterResult(error = R.string.register_failed)
        }
    }

    fun registerDataChanged(username: String, password: String, birthDate: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isBirthDateValid(birthDate)) {
            _registerForm.value = RegisterFormState(birthDateError = R.string.invalid_birth_date)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isBirthDateValid(birthDate: String): Boolean {
        try {
            LocalDate.parse(birthDate)
        }
        catch (e: DateTimeParseException) {
            return false
        }
        return true
    }
}