package hk.gogotech.ribs_poc.logged_in

import com.uber.rib.core.Builder
import com.uber.rib.core.EmptyPresenter
import com.uber.rib.core.InteractorBaseComponent

import java.lang.annotation.Retention

import javax.inject.Qualifier
import javax.inject.Scope

import dagger.BindsInstance
import dagger.Provides
import hk.gogotech.ribs_poc.logged_in.off_game.OffGameBuilder
import hk.gogotech.ribs_poc.logged_in.off_game.OffGameInteractor
import hk.gogotech.ribs_poc.logged_in.tic_tae_toe.TicTacToeBuilder
import hk.gogotech.ribs_poc.root.RootView

import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Named

class LoggedInBuilder(dependency: ParentComponent) : Builder<LoggedInRouter, LoggedInBuilder.ParentComponent>(dependency) {
    fun build(playerOne: String, playerTwo: String): LoggedInRouter {
        val interactor = LoggedInInteractor()
        val component = DaggerLoggedInBuilder_Component.builder()
                .parentComponent(dependency)
                .interactor(interactor)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .build()
        return component.loggedinRouter()
    }

    interface ParentComponent {
        fun rootView(): RootView
    }

    @dagger.Module
    object Module {
        @LoggedInScope
        @JvmStatic
        @Provides
        internal fun presenter(): EmptyPresenter {
            return EmptyPresenter()
        }

        @LoggedInScope
        @JvmStatic
        @Provides
        internal fun router(component: Component, interactor: LoggedInInteractor,
                            rootView: RootView): LoggedInRouter {
            return LoggedInRouter(
                    interactor,
                    component,
                    rootView,
                    OffGameBuilder(component),
                    TicTacToeBuilder(component))
        }

        @LoggedInScope
        @JvmStatic
        @Provides
        internal fun listener(interactor: LoggedInInteractor): OffGameInteractor.Listener {
            return interactor.OffGameListener()
        }

    }

    @LoggedInScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<LoggedInInteractor>, BuilderComponent, OffGameBuilder.ParentComponent, TicTacToeBuilder.ParentComponent {
        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun playerOne(@Named("player_one") playerOne: String): Builder

            @BindsInstance
            fun playerTwo(@Named("player_two") playerTwo: String): Builder

            @BindsInstance
            fun interactor(interactor: LoggedInInteractor): Builder

            fun parentComponent(component: ParentComponent): Builder

            fun build(): Component
        }

    }

    interface BuilderComponent {
        fun loggedinRouter(): LoggedInRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class LoggedInScope


    @Qualifier
    @Retention(CLASS)
    internal annotation class LoggedInInternal
}