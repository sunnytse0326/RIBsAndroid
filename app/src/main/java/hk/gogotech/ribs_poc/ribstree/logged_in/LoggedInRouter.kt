package hk.gogotech.ribs_poc.ribstree.logged_in

import android.view.ViewGroup
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.uber.rib.core.Router
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListBuilder
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListRouter

class LoggedInRouter(
        interactor: LoggedInInteractor,
        component: LoggedInBuilder.Component,
        private val parentView: ViewGroup,
        private val chatListBuilder: ChatListBuilder
) : Router<LoggedInInteractor, LoggedInBuilder.Component>(interactor, component){
    private var chatListRouter: ChatListRouter? = null

    fun attachChatList() {
        chatListRouter = chatListBuilder.build(parentView)
        attachChild(chatListRouter)
        parentView.addView(chatListRouter?.view)
    }

    fun updateChatList(result: Pair<Boolean, Pair<User, SendBirdException>> ){
        chatListBuilder.updateSendBirdUserResult(result)
    }

    fun detachChatList() {
        if (chatListRouter != null) {
            detachChild(chatListRouter)
            parentView.removeView(chatListRouter?.view)
            chatListRouter = null
        }
    }
}
