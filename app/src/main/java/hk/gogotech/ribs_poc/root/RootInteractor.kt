package hk.gogotech.ribs_poc.root

import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import hk.gogotech.ribs_poc.logged_out.LoggedOutInteractor
import javax.inject.Inject

@RibInteractor
open class RootInteractor : Interactor<RootInteractor.RootPresenter, RootRouter>() {

    @Inject
    lateinit var presenter: RootPresenter

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        router.attachLoggedOut()
    }

    open inner class LoggedOutListener : LoggedOutInteractor.Listener {
        override fun login(userNameA: String, userNameB: String) {
            router.detachLoggedOut()
            router.attachLoggedIn(userNameA, userNameB)
        }
    }

    interface RootPresenter
}