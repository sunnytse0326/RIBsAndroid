package hk.gogotech.ribs_poc.ribstree.logged_in

import com.sendbird.android.SendBird
import com.uber.rib.core.Bundle
import com.uber.rib.core.EmptyPresenter
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import hk.gogotech.ribs_poc.network.SendBoxService
import hk.gogotech.ribs_poc.storage.LocalStorage
import javax.inject.Inject
import javax.inject.Named

@RibInteractor
class LoggedInInteractor : Interactor<EmptyPresenter, LoggedInRouter>() {
    @Inject
    @JvmField
    @field:Named("local_memory")
    internal var localMemory: LocalStorage? = null

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        connectSendBird()
        router.attachChatList()
    }

    private fun connectSendBird() {
        val user = localMemory?.getUser()
        SendBoxService().connect(user?.userId ?: "", SendBird.ConnectHandler { mUser, err ->
            if (mUser?.nickname?.isEmpty() == true) {
                SendBird.updateCurrentUserInfo(user?.userName, user?.imageURL) {
                    router.updateChatList(Pair(true, Pair(mUser, err)))
                }
            } else {
                router.updateChatList(Pair(err == null, Pair(mUser, err)))
            }
        })
    }

    override fun willResignActive() {
        super.willResignActive()
    }

    interface SendBirdConnection {
        fun connect(userId: String, listener: SendBird.ConnectHandler)
    }
}
