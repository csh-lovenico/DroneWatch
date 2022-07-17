package tech.tennoji.dronewatch.droneimage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import tech.tennoji.dronewatch.network.Api
import tech.tennoji.dronewatch.network.DroneRecord

class DroneImageViewModel : ViewModel() {

    private var viewModelJob = SupervisorJob()

    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.IO
    )

    private var active = false

    private val _record = MutableLiveData<DroneRecord>()
    val record: LiveData<DroneRecord>
        get() = _record

    private val _droneId = MutableLiveData<String>()
    val droneId: LiveData<String>
        get() = _droneId

    private val _isPaused = MutableLiveData<Boolean>()
    val isPaused: LiveData<Boolean>
        get() = _isPaused


    fun setDroneId(droneId: String) {
        _droneId.value = droneId
    }

    private fun refreshData() {
        coroutineScope.launch {
            while (active) {
                val result =
                    droneId.value?.let { Api.retrofitService.getLatestDroneRecordAsync(it).await() }
                if (result != null) {
                    if (result.code == 200) {
                        result.data?.let {
                            withContext(Dispatchers.Main) {
                                _record.value = result.data
                            }
                        }
                    }
                }
                delay(1000)
            }
        }
    }

    fun launch() {
        active = true
        _isPaused.value = false
        refreshData()
    }

    fun end() {
        active = false
        _isPaused.value = true
    }

}