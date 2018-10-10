package hk.gogotech.ribs_poc.logged_in.off_game

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.jakewharton.rxbinding2.view.RxView
import com.uber.rib.core.Initializer

import hk.gogotech.ribs_poc.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.off_game_rib.view.*

class OffGameView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle), OffGameInteractor.OffGamePresenter {
    private var button: Button? = null
    private var playerOneName: TextView? = null
    private var playerTwoName: TextView? = null
    private var playerOneScore: TextView? = null
    private var playerTwoScore: TextView? = null

    @Initializer
    override fun onFinishInflate() {
        super.onFinishInflate()
        button = findViewById(R.id.start_game_button)
        playerOneName = findViewById(R.id.player_one_name)
        playerTwoName = findViewById(R.id.player_two_name)
        playerOneScore = findViewById(R.id.player_one_win_count)
        playerTwoScore = findViewById(R.id.player_two_win_count)
    }

    override fun startGameRequest(): Observable<Any> {
        return RxView.clicks(button!!)
    }

    override fun setPlayerNames(playerOne: String, playerTwo: String) {
        player_one_name.text = playerOne
        player_two_name.text = playerTwo
    }
}