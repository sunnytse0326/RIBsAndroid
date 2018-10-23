package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of {@link ChatListBuilder.ChatListScope}.
 *
 * TODO describe the possible child configurations of this scope.
 */
class ChatListRouter(
        view: ChatListView,
        interactor: ChatListInteractor,
        component: ChatListBuilder.Component) : ViewRouter<ChatListView, ChatListInteractor, ChatListBuilder.Component>(view, interactor, component)
