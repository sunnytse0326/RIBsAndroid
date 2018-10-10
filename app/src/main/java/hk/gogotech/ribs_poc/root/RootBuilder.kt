package hk.gogotech.ribs_poc.root

import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import hk.gogotech.ribs_poc.R

import hk.gogotech.ribs_poc.logged_in.LoggedInBuilder
import hk.gogotech.ribs_poc.logged_out.LoggedOutBuilder
import hk.gogotech.ribs_poc.logged_out.LoggedOutInteractor
import java.lang.annotation.Retention
import javax.inject.Scope
import java.lang.annotation.RetentionPolicy.CLASS


class RootBuilder(dependency: ParentComponent) : ViewBuilder<RootView, RootRouter, RootBuilder.ParentComponent>(dependency) {
    fun build(parentViewGroup: ViewGroup): RootRouter {
        val view = createView(parentViewGroup)
        val interactor = RootInteractor()
        val component = DaggerRootBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.rootRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): RootView {
        return inflater.inflate(R.layout.root_rib, parentViewGroup, false) as RootView
    }

    interface ParentComponent// Define dependencies required from your parent interactor here.

    @dagger.Module
    abstract class Module {
        @RootScope
        @Binds
        internal abstract fun presenter(view: RootView): RootInteractor.RootPresenter
        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            @RootScope
            internal fun loggedOutListener(rootInteractor: RootInteractor): LoggedOutInteractor.Listener {
                return rootInteractor.LoggedOutListener()
            }
            @JvmStatic
            @Provides
            @RootScope
            internal fun router(component: Component, view: RootView, interactor: RootInteractor): RootRouter {
                return RootRouter(
                        view,
                        interactor,
                        component,
                        LoggedOutBuilder(component),
                        LoggedInBuilder(component))
            }
        }
    }

    @RootScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<RootInteractor>, LoggedOutBuilder.ParentComponent, LoggedInBuilder.ParentComponent, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {

            @BindsInstance
            fun interactor(interactor: RootInteractor): Builder

            @BindsInstance
            fun view(view: RootView): Builder

            fun parentComponent(component: ParentComponent): Builder

            fun build(): Component
        }
    }

    interface BuilderComponent {

        fun rootRouter(): RootRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class RootScope
}