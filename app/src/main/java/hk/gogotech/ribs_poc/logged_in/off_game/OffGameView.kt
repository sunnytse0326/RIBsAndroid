package hk.gogotech.ribs_poc.logged_in.off_game

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.jakewharton.rxbinding2.view.RxView
import com.uber.rib.core.Initializer

import io.reactivex.Observable
import kotlinx.android.synthetic.main.off_game_rib.view.*

class OffGameView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle), OffGameInteractor.OffGamePresenter {
    @Initializer
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun startGameRequest(): Observable<Any> {
        return RxView.clicks(start_game_button)
    }

    override fun setPlayerNames(playerOne: String, playerTwo: String) {
        player_one_name.text = playerOne
        player_two_name.text = playerTwo
    }
}