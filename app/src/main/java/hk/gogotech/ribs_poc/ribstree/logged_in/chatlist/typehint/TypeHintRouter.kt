package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint

import android.view.View

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of {@link TypeHintBuilder.TypeHintScope}.
 *
 * TODO describe the possible child configurations of this scope.
 */
class TypeHintRouter(
    view: TypeHintView,
    interactor: TypeHintInteractor,
    component: TypeHintBuilder.Component) : ViewRouter<TypeHintView, TypeHintInteractor, TypeHintBuilder.Component>(view, interactor, component)
