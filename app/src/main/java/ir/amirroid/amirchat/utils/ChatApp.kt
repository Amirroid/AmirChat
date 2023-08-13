package ir.amirroid.amirchat.utils

import android.app.Application
import com.backendless.Backendless
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ChatApp : Application() {
    override fun onCreate() {
        initialBackEnd()
        super.onCreate()
    }

    private fun initialBackEnd() {
        Backendless.initApp(
            this,
            Constants.BACK_END_APPLICATION_ID,
            Constants.BACK_END_APPLICATION_API_KEY,
        )
    }
}