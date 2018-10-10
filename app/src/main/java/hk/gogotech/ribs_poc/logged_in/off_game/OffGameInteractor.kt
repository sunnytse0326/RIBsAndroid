package hk.gogotech.ribs_poc.logged_in.off_game


import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import dagger.Provides

import javax.inject.Inject

import io.reactivex.Observable
import javax.inject.Named

@RibInteractor
class OffGameInteractor : Interactor<OffGameInteractor.OffGamePresenter, OffGameRouter>() {
    @Inject
    lateinit var listener: Listener
    @Inject
    lateinit var presenter: OffGamePresenter

    @Inject
    @JvmField
    @field:Named("player_one")
    internal var playerOne: String? = null

    @Inject
    @JvmField
    @field:Named("player_two")
    internal var playerTwo: String? = null

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        presenter.setPlayerNames(playerOne?:"", playerTwo?:"")
        presenter!!
                .startGameRequest()
                .subscribe { listener!!.onStartGame() }
    }

    interface Listener {
        fun onStartGame()
    }

    interface OffGamePresenter {
        fun setPlayerNames(playerOne: String, playerTwo: String)
        fun startGameRequest(): Observable<Any>
    }
}