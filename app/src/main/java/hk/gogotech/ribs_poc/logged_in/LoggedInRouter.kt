package hk.gogotech.ribs_poc.logged_in

import android.view.ViewGroup

import com.uber.rib.core.Router

import hk.gogotech.ribs_poc.logged_in.off_game.OffGameBuilder
import hk.gogotech.ribs_poc.logged_in.off_game.OffGameRouter
import hk.gogotech.ribs_poc.logged_in.tic_tae_toe.TicTacToeBuilder
import hk.gogotech.ribs_poc.logged_in.tic_tae_toe.TicTacToeRouter

class LoggedInRouter internal constructor(
        interactor: LoggedInInteractor,
        component: LoggedInBuilder.Component,
        private val parentView: ViewGroup,
        private val offGameBuilder: OffGameBuilder,
        private val ticTacToeBuilder: TicTacToeBuilder) : Router<LoggedInInteractor, LoggedInBuilder.Component>(interactor, component) {
    private var offGameRouter: OffGameRouter? = null
    private var ticTacToeRouter: TicTacToeRouter? = null

    override fun willDetach() {
        super.willDetach()
        detachOffGame()
        detachTicTacToe()
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

    internal fun attachTicTacToe() {
        ticTacToeRouter = ticTacToeBuilder.build(parentView)
        attachChild(ticTacToeRouter!!)
        parentView.addView(ticTacToeRouter!!.getView())
    }

    internal fun detachTicTacToe() {
        if (ticTacToeRouter != null) {
            detachChild(ticTacToeRouter!!)
            parentView.removeView(ticTacToeRouter!!.getView())
            ticTacToeRouter = null
        }
    }
}
