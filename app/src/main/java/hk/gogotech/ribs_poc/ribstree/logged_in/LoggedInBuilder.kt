package hk.gogotech.ribs_poc.ribstree.logged_in

import com.uber.rib.core.Builder
import com.uber.rib.core.EmptyPresenter
import com.uber.rib.core.InteractorBaseComponent
import java.lang.annotation.Retention

import javax.inject.Qualifier
import javax.inject.Scope

import dagger.Provides
import dagger.BindsInstance
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListBuilder
import hk.gogotech.ribs_poc.ribstree.root.RootView
import hk.gogotech.ribs_poc.storage.LocalStorage

import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Named

class LoggedInBuilder(dependency: ParentComponent) : Builder<LoggedInRouter, LoggedInBuilder.ParentComponent>(dependency) {
    fun build(): LoggedInRouter {
        val interactor = LoggedInInteractor()
        val component = DaggerLoggedInBuilder_Component.builder()
                .parentComponent(dependency)
                .interactor(interactor)
                .build()
        return component.requestRouter()
    }

    interface ParentComponent {
        @Named("local_memory")
        fun localMemory(): LocalStorage
        fun rootView(): RootView
    }

    @dagger.Module
    abstract class Module {

        @dagger.Module
        companion object {
            @RequestScope
            @Provides
            @JvmStatic
            internal fun presenter(): EmptyPresenter {
                return EmptyPresenter()
            }
            @RequestScope
            @Provides
            @JvmStatic
            internal fun router(component: Component, interactor: LoggedInInteractor, rootView: RootView): LoggedInRouter {
                return LoggedInRouter(interactor, component, rootView, ChatListBuilder(object: ChatListBuilder.ParentComponent{
                    override fun rootView(): RootView {
                        return rootView
                    }
                    override fun localMemory(): LocalStorage {
                        return interactor.localMemory!!
                    }
                }))
            }
        }
    }


    @RequestScope
    @dagger.Component(modules = [Module::class], dependencies = [ParentComponent::class])
    interface Component : InteractorBaseComponent<LoggedInInteractor>, BuilderComponent {
        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: LoggedInInteractor): Builder
            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }

    }

    interface BuilderComponent {
        fun requestRouter(): LoggedInRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class RequestScope


    @Qualifier
    @Retention(CLASS)
    internal annotation class RequestInternal
}
