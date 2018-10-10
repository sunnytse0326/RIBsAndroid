package hk.gogotech.ribs_poc.logged_out

import android.view.LayoutInflater
import android.view.ViewGroup

import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder

import java.lang.annotation.Retention

import javax.inject.Qualifier
import javax.inject.Scope

import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import hk.gogotech.ribs_poc.R

import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Inject

/**
 * Builder for the [LoggedOutScope].
 */
class LoggedOutBuilder(dependency: ParentComponent) : ViewBuilder<LoggedOutView, LoggedOutRouter, LoggedOutBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [LoggedOutRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [LoggedOutRouter].
     */
    fun build(parentViewGroup: ViewGroup): LoggedOutRouter {
        val view = createView(parentViewGroup)
        val interactor = LoggedOutInteractor()
        val component = DaggerLoggedOutBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.loggedoutRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): LoggedOutView {
        return inflater.inflate(R.layout.logged_out_rib, parentViewGroup, false) as LoggedOutView
    }

    interface ParentComponent {
        fun listener(): LoggedOutInteractor.Listener
    }

    @dagger.Module
    abstract class Module {
        @LoggedOutScope
        @Binds
        internal abstract fun presenter(view: LoggedOutView): LoggedOutInteractor.LoggedOutPresenter
        @dagger.Module
        companion object {
            @LoggedOutScope
            @JvmStatic
            @Provides
            internal fun router(
                    component: Component,
                    view: LoggedOutView,
                    interactor: LoggedOutInteractor): LoggedOutRouter {
                return LoggedOutRouter(view, interactor, component)
            }
        }
        // TODO: Create provider methods for dependencies created by this Rib. These should be static.
    }

    @LoggedOutScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<LoggedOutInteractor>, BuilderComponent/*, InteractorBaseComponent*/ {

        @dagger.Component.Builder
        interface Builder {

            @BindsInstance
            fun interactor(interactor: LoggedOutInteractor): Builder

            @BindsInstance
            fun view(view: LoggedOutView): Builder

            fun parentComponent(component: ParentComponent): Builder

            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun loggedoutRouter(): LoggedOutRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class LoggedOutScope

    @Qualifier
    @Retention(CLASS)
    internal annotation class LoggedOutInternal
}