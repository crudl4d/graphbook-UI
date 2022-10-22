package com.dogebook.login.ui.main.login.ui

import android.content.Context
import android.net.Credentials
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogebook.R
import com.dogebook.login.ui.main.login.data.LoginRepository
import com.dogebook.login.ui.main.login.data.Result
import com.dogebook.login.ui.main.login.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, ctx: Context) {
        val credentials = okhttp3.Credentials.basic(username, password)
        val sharedPref = ctx.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("TOKEN", credentials)
        }.commit()
        var result: Result<LoggedInUser>?
        viewModelScope.launch(Dispatchers.IO) {
            result = loginRepository.login(credentials)
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = (result as Result.Success<LoggedInUser>).data.displayName))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}