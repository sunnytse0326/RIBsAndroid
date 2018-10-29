package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint

import android.view.View
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import javax.inject.Inject

/**
 * Coordinates Business Logic for [TypeHintScope].
 *
 * TODO describe the logic of this scope.
 */
@RibInteractor
class TypeHintInteractor : Interactor<TypeHintInteractor.TypeHintPresenter, TypeHintRouter>() {

    @Inject
    lateinit var presenter: TypeHintPresenter

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        presenter.initViewToRoundedBg()
    }

    override fun willResignActive() {
        super.willResignActive()
    }

    interface TypeHintPresenter{
        fun initViewToRoundedBg()
        fun setTypingText(value: String)
        fun animateTypeView(enable: Boolean)
    }
}
