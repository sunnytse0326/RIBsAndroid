package hk.gogotech.ribs_poc.ribstree.root

import com.uber.rib.core.ViewRouter
import hk.gogotech.ribs_poc.ribstree.logged_in.LoggedInBuilder
import hk.gogotech.ribs_poc.ribstree.logged_in.LoggedInRouter

class RootRouter internal constructor(
        view: RootView,
        interactor: RootInteractor,
        component: RootBuilder.Component,
        private val loggedInBuilder: LoggedInBuilder) : ViewRouter<RootView, RootInteractor, RootBuilder.Component>(view, interactor, component) {

    private var loggedInRouter: LoggedInRouter? = null

    internal fun attachLoggedIn() {
        loggedInRouter = loggedInBuilder.build()
        attachChild(loggedInRouter)
    }
}