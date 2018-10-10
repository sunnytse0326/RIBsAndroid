package hk.gogotech.ribs_poc.logged_in.off_game

import android.view.LayoutInflater
import android.view.ViewGroup

import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder

import java.lang.annotation.Retention

import javax.inject.Qualifier
import javax.inject.Scope

import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import hk.gogotech.ribs_poc.R

import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Inject
import javax.inject.Named

class OffGameBuilder(dependency: ParentComponent) : ViewBuilder<OffGameView, OffGameRouter, OffGameBuilder.ParentComponent>(dependency) {

    fun build(parentViewGroup: ViewGroup): OffGameRouter {
        val view = createView(parentViewGroup)
        val interactor = OffGameInteractor()
        val component = DaggerOffGameBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.offgameRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): OffGameView {
        return inflater.inflate(R.layout.off_game_rib, parentViewGroup, false) as OffGameView
    }

    interface ParentComponent {
        @Named("player_one")
        fun playerOne(): String
        @Named("player_two")
        fun playerTwo(): String
        fun listener(): OffGameInteractor.Listener
    }

    @dagger.Module
    abstract class Module {
        @OffGameScope
        @Binds
        internal abstract fun presenter(view: OffGameView): OffGameInteractor.OffGamePresenter
        @dagger.Module
        companion object {
            @OffGameScope
            @JvmStatic
            @Provides
            internal fun router(
                    component: Component,
                    view: OffGameView,
                    interactor: OffGameInteractor): OffGameRouter {
                return OffGameRouter(view, interactor, component)
            }
        }
    }

    @OffGameScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<OffGameInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {

            @BindsInstance
            fun interactor(interactor: OffGameInteractor): Builder

            @BindsInstance
            fun view(view: OffGameView): Builder

            fun parentComponent(component: ParentComponent): Builder

            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun offgameRouter(): OffGameRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class OffGameScope

    @Qualifier
    @Retention(CLASS)
    internal annotation class OffGameInternal
}