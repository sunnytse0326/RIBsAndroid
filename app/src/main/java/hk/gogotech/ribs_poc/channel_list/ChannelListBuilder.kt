package hk.gogotech.ribs_poc.channel_list

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
import hk.gogotech.ribs_poc.R

import java.lang.annotation.RetentionPolicy.CLASS

/**
 * Builder for the [LoggedOutScope].
 */
class ChannelListBuilder(dependency: ParentComponent) : ViewBuilder<ChannelListView, ChannelListRouter, ChannelListBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [ChannelListRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [ChannelListRouter].
     */
    fun build(parentViewGroup: ViewGroup): ChannelListRouter {
        val view = createView(parentViewGroup)
        val interactor = ChannelListInteractor()
        val component = DaggerLoggedOutBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.loggedoutRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): ChannelListView {
        return inflater.inflate(R.layout.logged_out_rib, parentViewGroup, false) as ChannelListView
    }

    interface ParentComponent {
        fun listener(): ChannelListInteractor.Listener
    }

    @dagger.Module
    abstract class Module {
        @LoggedOutScope
        @Binds
        internal abstract fun presenter(view: ChannelListView): ChannelListInteractor.LoggedOutPresenter
        @dagger.Module
        companion object {
            @LoggedOutScope
            @JvmStatic
            @Provides
            internal fun router(
                    component: Component,
                    view: ChannelListView,
                    interactor: ChannelListInteractor): ChannelListRouter {
                return ChannelListRouter(view, interactor, component)
            }
        }
        // TODO: Create provider methods for dependencies created by this Rib. These should be static.
    }

    @LoggedOutScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<ChannelListInteractor>, BuilderComponent/*, InteractorBaseComponent*/ {

        @dagger.Component.Builder
        interface Builder {

            @BindsInstance
            fun interactor(interactor: ChannelListInteractor): Builder

            @BindsInstance
            fun view(view: ChannelListView): Builder

            fun parentComponent(component: ParentComponent): Builder

            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun loggedoutRouter(): ChannelListRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class LoggedOutScope

    @Qualifier
    @Retention(CLASS)
    internal annotation class LoggedOutInternal
}