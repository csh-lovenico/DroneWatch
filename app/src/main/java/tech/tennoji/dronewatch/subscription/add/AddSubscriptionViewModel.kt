package tech.tennoji.dronewatch.subscription.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import tech.tennoji.dronewatch.network.Api

class AddSubscriptionViewModel : ViewModel() {
    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.IO
    )
    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    private val _areaList = MutableLiveData<List<String>>()
    val areaList: LiveData<List<String>>
        get() = _areaList

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun setToken(token: String) {
        _token.value = token
    }

    private val _areaName = MutableLiveData<String>()
    val areaName: LiveData<String>
        get() = _areaName

    fun navigate(areaName: String) {
        _areaName.value = areaName
    }

    fun navigateComplete() {
        _areaName.value = null
    }

    fun refreshList() {
        _loading.value = true
        coroutineScope.launch {
            val result =
                token.value?.let { Api.retrofitService.getNotSubscribedAreasAsync(it).await() }
            withContext(Dispatchers.Main) {
                if (result != null) {
                    _areaList.value = result.data
                    _loading.value = false
                }
            }
        }
    }
}