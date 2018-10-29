package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import hk.gogotech.ribs_poc.R
import hk.gogotech.ribs_poc.storage.LocalStorage
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Qualifier
import javax.inject.Scope

class TypeHintBuilder(dependency: ParentComponent) : ViewBuilder<TypeHintView, TypeHintRouter, TypeHintBuilder.ParentComponent>(dependency) {
    fun build(parentViewGroup: ViewGroup): TypeHintRouter {
        val view = createView(parentViewGroup)
        val interactor = TypeHintInteractor()
        val component = DaggerTypeHintBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.typehintRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): TypeHintView? {
        return inflater.inflate(R.layout.typehint_rib, parentViewGroup, false) as TypeHintView
    }

    interface ParentComponent {
        fun userName(): String
    }

    @dagger.Module
    abstract class Module {

        @TypeHintScope
        @Binds
        internal abstract fun presenter(view: TypeHintView): TypeHintInteractor.TypeHintPresenter

        @dagger.Module
        companion object {

            @TypeHintScope
            @Provides
            @JvmStatic
            internal fun router(
                    component: Component,
                    view: TypeHintView,
                    interactor: TypeHintInteractor): TypeHintRouter {
                return TypeHintRouter(view, interactor, component)
            }
        }
    }

    @TypeHintScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<TypeHintInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: TypeHintInteractor): Builder

            @BindsInstance
            fun view(view: TypeHintView): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun typehintRouter(): TypeHintRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class TypeHintScope

    @Qualifier
    @Retention(CLASS)
    internal annotation class TypeHintInternal
}
