package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.util.Log
import android.view.View
import com.jakewharton.rxbinding2.InitialValueObservable
import com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import com.sendbird.android.*
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import hk.gogotech.ribs_poc.BaseApplication
import hk.gogotech.ribs_poc.R
import hk.gogotech.ribs_poc.model.OrderItem
import hk.gogotech.ribs_poc.model.SendBirdUser
import hk.gogotech.ribs_poc.storage.LocalStorage
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.concurrent.schedule


@RibInteractor
class ChatListInteractor : Interactor<ChatListInteractor.ChatListPresenter, ChatListRouter>() {
    @Inject
    @JvmField
    @field:Named("local_memory")
    internal var localMemory: LocalStorage? = null

    @Inject
    lateinit var presenter: ChatListPresenter

    private var validChannel: GroupChannel? = null
    lateinit var user: SendBirdUser
    lateinit var order: OrderItem

    private val CHANNEL_LIST_LIMIT = 30

    private var editTextEnterButtonDisposal: Disposable? = null
    private var editTextDisposal: Disposable? = null
    private var sendMsgBtnDisposal: Disposable? = null

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        initData()
        monitorInputMessageAction()
        attachTypeView()
    }

    private fun initData(){
        user = localMemory?.getUser()!!
        order = localMemory?.getOrder()!!
    }

    private fun attachTypeView(){
        router.attachTypeHint()
    }

    private fun monitorInputMessageAction() {
        editTextDisposal = presenter.monitorTextFromEditText().subscribe {
            presenter.setSendBtnEnable(it.isNotEmpty())
            validChannel?.startTyping()
            Log.e("aa","startTyping")
            Timer().schedule(2000) {
                validChannel?.endTyping()
                Log.e("aa","endTyping")
            }
        }
        editTextEnterButtonDisposal = presenter.getTextFromEditText().subscribe {

        }
        sendMsgBtnDisposal = presenter.getTextFromSendBtn().subscribe {

        }
        listenChannelInfo()
    }

    private fun listenChannelInfo() {
        SendBird.addChannelHandler("${order.clientId}${order.driverId}", object : SendBird.ChannelHandler() {
            override fun onMessageReceived(p0: BaseChannel?, p1: BaseMessage?) {}

            override fun onTypingStatusUpdated(groupChannel: GroupChannel?) {
                if (validChannel?.url.equals(groupChannel!!.url)) {
                    val members = groupChannel?.typingMembers
                    if (members?.size ?: 0 > 0 && members!![0].nickname != user.userName){
                        router.setTypeTextContext(members!![0].nickname)
                        router.runAnimation(true)
                    } else{
                        router.runAnimation(false)
                    }
                }
            }
        })
    }

    override fun willResignActive() {
        super.willResignActive()
    }

    override fun dispatchDetach(): ChatListPresenter {
        editTextDisposal?.dispose()
        editTextEnterButtonDisposal?.dispose()
        sendMsgBtnDisposal?.dispose()
        return super.dispatchDetach()
    }

    fun updateSendBirdUserResult(result: Pair<Boolean, Pair<User, SendBirdException>>) {
        val user = localMemory?.getUser()
        var orderItem = localMemory?.getOrder()
        if (result.first) {
            GroupChannel.getChannel(user?.channelId) { channel, err ->
                if (err != null) {
                    createChannel(user, orderItem)
                } else {
                    enterChannel(channel, err)
                }
            }
        } else {
            showErrMsg(result.second.second)
        }
    }


    private fun createChannel(user: SendBirdUser?, orderItem: OrderItem?) {
        GroupChannel.createChannelWithUserIds(arrayListOf(orderItem?.driverId, orderItem?.clientId),
                true, user?.channelId, null, null, null) { channel, err ->
            if (err == null) {
                enterChannel(channel, err)
            } else {
                showErrMsg(err)
            }
        }
    }

    private fun enterChannel(channel: GroupChannel, err: SendBirdException?) {
        validChannel = channel
        fetchMessages()
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
        /* Observable Pattern */
        fun getTextFromEditText(): Observable<TextViewEditorActionEvent>

        fun getTextFromSendBtn(): Observable<*>
        fun monitorTextFromEditText(): InitialValueObservable<CharSequence>

        /* Normal View */
        fun setLoadingVisibility(visibility: Int)
        fun displayErrMsg(msg: String)
        fun getEditTextContent(): String
        fun setSendBtnEnable(enable: Boolean)
    }
}
