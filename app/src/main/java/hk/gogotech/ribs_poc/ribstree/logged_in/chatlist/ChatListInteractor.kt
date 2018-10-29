package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
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
import com.sendbird.android.SendBirdException
import com.sendbird.android.UserMessage
import com.sendbird.android.BaseChannel
import hk.gogotech.ribs_poc.uicomponent.ChatListAdapter
import org.jetbrains.anko.collections.forEachByIndex


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

    private var adapter: ChatListAdapter? = null

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        initData()
        monitorInputMessageAction()
        attachTypeView()
        initAdapter()
    }

    private fun initData(){
        user = localMemory?.getUser()!!
        order = localMemory?.getOrder()!!
    }

    private fun initAdapter(){
        adapter = ChatListAdapter(presenter.getContent())
        val recyclerView = presenter.getRecyclerView()
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    private fun attachTypeView(){
        router.attachTypeHint()
    }

    private fun monitorInputMessageAction() {
        editTextDisposal = presenter.monitorTextFromEditText().subscribe {
            presenter.setSendBtnEnable(it.isNotEmpty())
            validChannel?.startTyping()
            Timer().schedule(2000) {
                validChannel?.endTyping()
            }
        }
        editTextEnterButtonDisposal = presenter.getTextFromEditText().subscribe {
            if(it.view().text.isNotEmpty()){
                sendMessage(it.view().text.toString())
                presenter.setEditTextContent("")
            }
        }
        sendMsgBtnDisposal = presenter.getTextFromSendBtn().subscribe {
            if(presenter.getEditTextContent().isNotEmpty()){
                sendMessage(presenter.getEditTextContent())
                presenter.setEditTextContent("")
            }
        }
        listenChannelInfo()
    }

    private fun sendMessage(msg: String){
        validChannel?.sendUserMessage(msg){ userMessage, e -> if (e == null) {
            adapter?.markMessageSent(userMessage)
            fetchMessages()
        } }
    }

    private fun listenChannelInfo() {
        SendBird.addChannelHandler("${order.clientId}${order.driverId}", object : SendBird.ChannelHandler() {
            override fun onMessageReceived(p0: BaseChannel?, p1: BaseMessage?) {
                fetchMessages()
            }

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
        validChannel?.createPreviousMessageListQuery()?.load(CHANNEL_LIST_LIMIT, false) { list, err ->
            adapter?.updateList(list)
            presenter.setLoadingVisibility(View.GONE)
            presenter.getRecyclerView().scrollToPosition(list.size - 1)
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
        fun setEditTextContent(msg: String)
        fun setSendBtnEnable(enable: Boolean)
        fun getContent(): Context
        fun getRecyclerView():RecyclerView
    }
}
