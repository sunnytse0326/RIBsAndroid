package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.view.ViewGroup
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.uber.rib.core.ViewRouter
import hk.gogotech.ribs_poc.BaseApplication
import hk.gogotech.ribs_poc.R
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint.TypeHintBuilder
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint.TypeHintRouter

/**
 * Adds and removes children of {@link ChatListBuilder.ChatListScope}.
 *
 * TODO describe the possible child configurations of this scope.
 */
class ChatListRouter(
        view: ChatListView,
        interactor: ChatListInteractor,
        component: ChatListBuilder.Component,
        private val parentView: ViewGroup,
        private val typeHintBuilder: TypeHintBuilder) : ViewRouter<ChatListView, ChatListInteractor, ChatListBuilder.Component>(view, interactor, component) {
    private var typeHintRouter: TypeHintRouter? = null

    fun attachTypeHint() {
        typeHintRouter = typeHintBuilder.build(parentView)
        attachChild(typeHintRouter)
        parentView.addView(typeHintRouter?.view)
    }

    fun setTypeTextContext(text: String) {
        typeHintRouter?.interactor?.presenter?.setTypingText(String.format(BaseApplication.context.getString(R.string.is_typing), text))
    }

    fun runAnimation(enable: Boolean){
        typeHintRouter?.interactor?.presenter?.animateTypeView(enable)
    }

    fun detachTypeHint() {
        if (typeHintRouter != null) {
            detachChild(typeHintRouter)
            parentView.removeView(typeHintRouter?.view)
            typeHintRouter = null
        }
    }
}
