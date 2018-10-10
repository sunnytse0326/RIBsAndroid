package hk.gogotech.ribs_poc.logged_in.tic_tae_toe

import android.content.Context
import android.support.percent.PercentRelativeLayout
import android.util.AttributeSet
import android.widget.TextView

import com.jakewharton.rxbinding2.view.RxView
import com.uber.rib.core.Initializer

import java.util.ArrayList

import hk.gogotech.ribs_poc.R
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Top level view for [.TicTacToeScope].
 */
class TicTacToeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : PercentRelativeLayout(context, attrs, defStyle), TicTacToeInteractor.TicTacToePresenter {

    private var imageButtons: Array<Array<TextView>>? = null
    private var titleView: TextView? = null

    @Initializer
    override fun onFinishInflate() {
        super.onFinishInflate()
        imageButtons = arrayOf()
        imageButtons?.set(0, arrayOf(findViewById(R.id.button11), findViewById(R.id.button12), findViewById(R.id.button13)))
        imageButtons?.set(1, arrayOf(findViewById(R.id.button21), findViewById(R.id.button22), findViewById(R.id.button23)))
        imageButtons?.set(2, arrayOf(findViewById(R.id.button31), findViewById(R.id.button32), findViewById(R.id.button33)))
        titleView = findViewById(R.id.title)
    }

    override fun squareClicks(): Observable<BoardCoordinate> {
        val observables = ArrayList<Observable<BoardCoordinate>>()
        for (i in 0..2) {
            for (j in 0..2) {
                observables.add(
                        RxView.clicks(imageButtons!![i][j])
                                .map { BoardCoordinate(i, j) })
            }
        }
        return Observable.merge(observables)
    }

    override fun addCross(xy: BoardCoordinate) {
        val textView = imageButtons!![xy.x][xy.y]
        textView.text = "x"
        textView.isClickable = false
    }

    override fun addNought(xy: BoardCoordinate) {
        val textView = imageButtons!![xy.x][xy.y]
        textView.text = "O"
        textView.isClickable = false
    }

    override fun setCurrentPlayerName(currentPlayer: String) {
        titleView!!.text = "Current Player: $currentPlayer"
    }

    override fun setPlayerWon(playerName: String) {
        titleView!!.text = "Player won: $playerName!!!"
    }

    override fun setPlayerTie() {
        titleView!!.text = "Tie game!"
    }
}