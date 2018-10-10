package hk.gogotech.ribs_poc.root

import com.uber.rib.core.ViewRouter
import hk.gogotech.ribs_poc.logged_in.LoggedInBuilder
import hk.gogotech.ribs_poc.logged_out.LoggedOutBuilder
import hk.gogotech.ribs_poc.logged_out.LoggedOutRouter

class RootRouter internal constructor(
        view: RootView,
        interactor: RootInteractor,
        component: RootBuilder.Component,
        private val loggedOutBuilder: LoggedOutBuilder,
        private val loggedInBuilder: LoggedInBuilder) : ViewRouter<RootView, RootInteractor, RootBuilder.Component>(view, interactor, component) {
    private var loggedOutRouter: LoggedOutRouter? = null

    internal fun attachLoggedOut() {
        loggedOutRouter = loggedOutBuilder.build(view)
        attachChild(loggedOutRouter!!)
        view.addView(loggedOutRouter!!.getView())
    }

    internal fun detachLoggedOut() {
        if (loggedOutRouter != null) {
            detachChild(loggedOutRouter!!)
            view.removeView(loggedOutRouter!!.getView())
            loggedOutRouter = null
        }
    }

    internal fun attachLoggedIn(playerOne: String, playerTwo: String) {
        attachChild(loggedInBuilder.build(playerOne, playerTwo))
    }
}