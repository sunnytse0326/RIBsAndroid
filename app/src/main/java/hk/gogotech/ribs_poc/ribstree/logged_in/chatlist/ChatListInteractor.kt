package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.view.View
import com.jakewharton.rxbinding2.InitialValueObservable
import com.sendbird.android.OpenChannel
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import hk.gogotech.ribs_poc.BaseApplication
import hk.gogotech.ribs_poc.R
import hk.gogotech.ribs_poc.model.SendBirdUser
import hk.gogotech.ribs_poc.storage.LocalStorage
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Named

@RibInteractor
class ChatListInteractor : Interactor<ChatListInteractor.ChatListPresenter, ChatListRouter>() {
    @Inject
    @JvmField
    @field:Named("local_memory")
    internal var localMemory: LocalStorage? = null

    @Inject
    lateinit var presenter: ChatListPresenter

    private var validChannel: OpenChannel? = null

    private val CHANNEL_LIST_LIMIT = 30

    private var editTextDisposal: Disposable? = null

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        monitorInputMessageAction()
    }

    private fun monitorInputMessageAction(){
        editTextDisposal = presenter.getTextFromEditText().subscribe {
            
        }
    }

    override fun willResignActive() {
        super.willResignActive()
    }

    override fun dispatchDetach(): ChatListPresenter {
        return super.dispatchDetach()
    }

    fun updateSendBirdUserResult(result: Pair<Boolean, Pair<User, SendBirdException>>) {
        val user = localMemory?.getUser()
        if (result.first) {
            OpenChannel.getChannel(user?.channelId) { channel, err ->
                if (err != null) {
                    createChannel(user)
                } else {
                    enterChannel(channel, err)
                }
            }
        } else {
            showErrMsg(result.second.second)
        }
    }


    private fun createChannel(user: SendBirdUser?) {
        OpenChannel.createChannelWithOperatorUserIds(user?.channelId ?: "",
                "", "", null) { channel, err ->
            if (err == null) {
                enterChannel(channel, err)
            } else {
                showErrMsg(err)
            }
        }
    }

    private fun enterChannel(channel: OpenChannel, err: SendBirdException?) {
        channel.enter {
            if (err != null) {
                showErrMsg(err)
            } else {
                validChannel = channel
                fetchMessages()
            }
        }
    }

    private fun fetchMessages() {
        validChannel?.createPreviousMessageListQuery()?.load(CHANNEL_LIST_LIMIT, true) { list, err ->

            presenter.setLoadingVisibility(View.GONE)
        }
    }

    private fun showErrMsg(err: SendBirdException) {
        presenter.setLoadingVisibility(View.GONE)
        presenter.displayErrMsg(err.message
                ?: BaseApplication.context.getString(R.string.unknown_err))
    }



    interface ChatListPresenter {
        fun setLoadingVisibility(visibility: Int)
        fun displayErrMsg(msg: String)
        fun getTextFromEditText(): InitialValueObservable<CharSequence>
    }
}
