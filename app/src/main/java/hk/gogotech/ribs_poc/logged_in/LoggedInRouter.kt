package hk.gogotech.ribs_poc.logged_in

import android.view.ViewGroup

import com.uber.rib.core.Router

import hk.gogotech.ribs_poc.logged_in.off_game.OffGameBuilder
import hk.gogotech.ribs_poc.logged_in.off_game.OffGameRouter

class LoggedInRouter internal constructor(
        interactor: LoggedInInteractor,
        component: LoggedInBuilder.Component,
        private val parentView: ViewGroup,
        private val offGameBuilder: OffGameBuilder) : Router<LoggedInInteractor, LoggedInBuilder.Component>(interactor, component) {
    private var offGameRouter: OffGameRouter? = null

    override fun willDetach() {
        super.willDetach()
        detachOffGame()
    }

    fun attachOffGame() {
        offGameRouter = offGameBuilder.build(parentView)
        attachChild(offGameRouter)
        parentView.addView(offGameRouter?.view)
    }

    fun detachOffGame() {
        if (offGameRouter != null) {
            detachChild(offGameRouter)
            parentView.removeView(offGameRouter?.view)
            offGameRouter = null
        }
    }
}
