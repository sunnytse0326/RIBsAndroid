package hk.gogotech.ribs_poc.logged_in


import com.uber.rib.core.Bundle
import com.uber.rib.core.EmptyPresenter
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor

import hk.gogotech.ribs_poc.logged_in.off_game.OffGameInteractor

/**
 * Coordinates Business Logic for [LoggedInBuilder.LoggedInScope].
 */
@RibInteractor
class LoggedInInteractor : Interactor<EmptyPresenter, LoggedInRouter>() {

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        router.attachOffGame()
    }

    inner class OffGameListener : OffGameInteractor.Listener {
        override fun onStartGame() {
            router.detachOffGame()
        }
    }
}