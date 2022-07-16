package tech.tennoji.dronewatch.areadetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import tech.tennoji.dronewatch.network.Api

class AreaDetailViewModel : ViewModel() {
    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.IO
    )
    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    private val _areaName = MutableLiveData<String>()
    val areaName: LiveData<String>
        get() = _areaName

    private val _isAdd = MutableLiveData<Boolean>()
    val isAdd: LiveData<Boolean>
        get() = _isAdd

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    fun initialize(areaName: String, token: String, isAdd: Boolean) {
        _areaName.value = areaName
        _token.value = token
        _isAdd.value = isAdd
    }

    fun doAction(isAdd: Boolean) {
        _loading.value = true
        coroutineScope.launch {
            val result = if (isAdd) {
                Api.retrofitService.subscribeToTopicAsync(
                    token.value!!,
                    areaName.value!!
                )
                    .await()
            } else {
                Api.retrofitService.unsubscribeToTopicAsync(
                    token.value!!,
                    areaName.value!!
                )
                    .await()
            }
            withContext(Dispatchers.Main) {
                _loading.value = false
            }
            if (result.code == 200) {
                withContext(Dispatchers.Main) {
                    _success.value = true
                    _message.value = result.message
                }
            } else {
                withContext(Dispatchers.Main) {
                    _success.value = false
                    _message.value = result.message
                }
            }
        }
    }
}