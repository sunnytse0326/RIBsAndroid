package hk.gogotech.ribs_poc.logged_in.tic_tae_toe


import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * Coordinates Business Logic for [TicTacToeScope].
 */
@RibInteractor
class TicTacToeInteractor : Interactor<TicTacToeInteractor.TicTacToePresenter, TicTacToeRouter>() {

    @Inject
    lateinit var board: Board
    @Inject
    lateinit var presenter: TicTacToePresenter

    private val playerOne = "Fake name 1"
    private val playerTwo = "Fake name 2"

    private var currentPlayer = Board.MarkerType.CROSS

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        presenter!!
                .squareClicks()
                .subscribe { xy ->
                    if (board!!.cells[xy.x][xy.y] == null) {
                        if (currentPlayer === Board.MarkerType.CROSS) {
                            board!!.cells[xy.x][xy.y] = Board.MarkerType.CROSS
                            board!!.currentRow = xy.x
                            board!!.currentCol = xy.y
                            presenter!!.addCross(xy)
                            currentPlayer = Board.MarkerType.NOUGHT
                        } else {
                            board!!.cells[xy.x][xy.y] = Board.MarkerType.NOUGHT
                            board!!.currentRow = xy.x
                            board!!.currentCol = xy.y
                            presenter!!.addNought(xy)
                            currentPlayer = Board.MarkerType.CROSS
                        }
                    }
                    if (board!!.hasWon(Board.MarkerType.CROSS)) {
                        presenter!!.setPlayerWon(playerOne)
                    } else if (board!!.hasWon(Board.MarkerType.NOUGHT)) {
                        presenter!!.setPlayerWon(playerTwo)
                    } else if (board!!.isDraw) {
                        presenter!!.setPlayerTie()
                    } else {
                        updateCurrentPlayer()
                    }
                }
        updateCurrentPlayer()
    }

    private fun updateCurrentPlayer() {
        if (currentPlayer === Board.MarkerType.CROSS) {
            presenter!!.setCurrentPlayerName(playerOne)
        } else {
            presenter!!.setCurrentPlayerName(playerTwo)
        }
    }


    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface TicTacToePresenter {
        fun squareClicks(): Observable<BoardCoordinate>

        fun setCurrentPlayerName(currentPlayer: String)

        fun setPlayerWon(playerName: String)

        fun setPlayerTie()

        fun addCross(xy: BoardCoordinate)

        fun addNought(xy: BoardCoordinate)
    }

    interface Listener
}