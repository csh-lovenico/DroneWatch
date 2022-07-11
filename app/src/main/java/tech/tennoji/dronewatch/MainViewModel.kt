package tech.tennoji.dronewatch

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainViewModel : ViewModel() {


    private val tokenMutable = MutableLiveData<String>()
    val token: LiveData<String>
        get() = tokenMutable

    // TODO: Implement the ViewModel
    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val returnToken = task.result

            Log.d(this.javaClass.name, returnToken)

            // Log and toast
            tokenMutable.value = returnToken
        })
    }
}