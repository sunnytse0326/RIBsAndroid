package hk.gogotech.ribs_poc.logged_out

import android.util.Log
import android.util.Pair
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor

import javax.inject.Inject
import io.reactivex.Observable
import io.reactivex.annotations.Nullable

/**
 * Coordinates Business Logic for [LoggedOutBuilder.LoggedOutScope].
 */
@RibInteractor
class LoggedOutInteractor : Interactor<LoggedOutInteractor.LoggedOutPresenter, LoggedOutRouter>() {
    @Inject
    lateinit var listener: Listener

    @Inject
    lateinit var presenter: LoggedOutPresenter

    override fun didBecomeActive(@Nullable savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
        presenter!!
                .loginName()
                .subscribe { names ->
                    if (!isEmpty(names.first) && !isEmpty(names.second)) {
                        listener!!.login(names.first, names.second)
                    }
                }
    }

    private fun isEmpty(@Nullable string: String?): Boolean {
        return string == null || string.length == 0
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface LoggedOutPresenter {
        fun loginName(): Observable<Pair<String, String>>
    }

    interface Listener {
        fun login(userNameA: String, userNameB: String)
    }

}