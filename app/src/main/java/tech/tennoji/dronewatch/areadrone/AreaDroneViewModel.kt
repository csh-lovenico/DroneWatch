package tech.tennoji.dronewatch.areadrone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import tech.tennoji.dronewatch.network.Api

class AreaDroneViewModel : ViewModel() {
    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.IO
    )
    private val _droneList = MutableLiveData<List<String>>()
    val droneList: LiveData<List<String>>
        get() = _droneList

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _areaName = MutableLiveData<String>()
    val areaName: LiveData<String>
        get() = _areaName

    private val _droneId = MutableLiveData<String>()
    val droneId: LiveData<String>
        get() = _droneId


    fun navigate(droneId: String) {
        _droneId.value = droneId
    }

    fun navigateComplete() {
        _droneId.value = null
    }

    fun setArea(areaName: String) {
        _areaName.value = areaName
    }

    fun refreshList() {
        _loading.value = true
        coroutineScope.launch {
            val result =
                _areaName.value?.let { Api.retrofitService.getAreaDroneListAsync(it).await() }
            withContext(Dispatchers.Main) {
                if (result != null) {
                    _droneList.value = result.data
                    _loading.value = false
                }
            }
        }
    }
}