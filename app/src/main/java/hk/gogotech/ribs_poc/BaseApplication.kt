package hk.gogotech.ribs_poc

import android.app.Application
import android.content.Context
import com.sendbird.android.SendBird

class BaseApplication : Application() {
    private val APP_ID = "9DA1B1F4-0BE6-4DA8-82C5-2E81DAB56F23" //Example of SDK

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        SendBird.init(APP_ID, applicationContext)
        context = this.applicationContext
    }
}