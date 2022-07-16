package tech.tennoji.dronewatch.mainscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import tech.tennoji.dronewatch.network.Api
import tech.tennoji.dronewatch.network.FenceStatus

class MainViewModel : ViewModel() {

    init {
        getToken()
    }

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.IO
    )
    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    private val _statusList = MutableLiveData<List<FenceStatus>>()
    val statusList: LiveData<List<FenceStatus>>
        get() = _statusList

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading



    fun fetchStatus() {
        _loading.value = true
        coroutineScope.launch {
            val result =
                token.value?.let { Api.retrofitService.getSubscribedAreaStatusAsync(it).await() }
            withContext(Dispatchers.Main) {
                if (result != null) {
                    _statusList.value = result.data
                    _loading.value = false
                }
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    this.javaClass.toString(),
                    "Fetching FCM registration token failed",
                    task.exception
                )
                _token.value = "error"
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val returnToken = task.result

            Log.i(this.javaClass.name, returnToken)

            _token.value = returnToken
        })
    }
}