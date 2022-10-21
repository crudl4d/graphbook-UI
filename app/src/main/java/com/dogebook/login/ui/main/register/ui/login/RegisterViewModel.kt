package com.dogebook.login.ui.main.register.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.dogebook.R
import com.dogebook.login.ui.main.register.data.RegisterRepository
import com.dogebook.login.ui.main.register.data.Result
import com.dogebook.login.ui.main.register.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterViewModel(val loginRepository: RegisterRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _loginForm

    private val _loginResult = MutableLiveData<RegisterResult>()
    val loginResult: LiveData<RegisterResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        var result: Result<LoggedInUser>? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = loginRepository.login(username, password)
        }
        if (result is Result.Success) {
            _loginResult.value =
                RegisterResult(success = RegisteredUserView(displayName = (result as Result.Success<LoggedInUser>).data.displayName))
        } else {
            _loginResult.value = RegisterResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = RegisterFormState(isDataValid = true)
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
}