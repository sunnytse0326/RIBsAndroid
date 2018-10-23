package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import hk.gogotech.ribs_poc.R
import hk.gogotech.ribs_poc.storage.LocalStorage
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Scope

class ChatListBuilder(dependency: ParentComponent) : ViewBuilder<ChatListView, ChatListRouter, ChatListBuilder.ParentComponent>(dependency) {
    val interactor = ChatListInteractor()

    fun build(parentViewGroup: ViewGroup): ChatListRouter {
        val view = createView(parentViewGroup)
        val component = DaggerChatListBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.chatlistRouter()
    }

    fun updateSendBirdUserResult(result: Pair<Boolean, Pair<User, SendBirdException>>){
        interactor.updateSendBirdUserResult(result)
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): ChatListView? {
        return inflater.inflate(R.layout.chatlist_rib, parentViewGroup, false) as ChatListView
    }

    interface ParentComponent {
        @Named("local_memory")
        fun localMemory(): LocalStorage
    }

    @dagger.Module
    abstract class Module {

        @ChatListScope
        @Binds
        internal abstract fun presenter(view: ChatListView): ChatListInteractor.ChatListPresenter

        @dagger.Module
        companion object {

            @ChatListScope
            @Provides
            @JvmStatic
            internal fun router(
                    component: Component,
                    view: ChatListView,
                    interactor: ChatListInteractor): ChatListRouter {
                return ChatListRouter(view, interactor, component)
            }
        }
    }

    @ChatListScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<ChatListInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: ChatListInteractor): Builder

            @BindsInstance
            fun view(view: ChatListView): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun chatlistRouter(): ChatListRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class ChatListScope

    @Qualifier
    @Retention(CLASS)
    internal annotation class ChatListInternal
}
