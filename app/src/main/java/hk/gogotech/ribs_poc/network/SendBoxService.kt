package hk.gogotech.ribs_poc.network

import com.sendbird.android.SendBird
import hk.gogotech.ribs_poc.ribstree.logged_in.LoggedInInteractor

class SendBoxService : LoggedInInteractor.SendBirdConnection {
    override fun connect(userId: String, handler: SendBird.ConnectHandler) {
       SendBird.connect(userId, handler)
    }
}