package hk.gogotech.ribs_poc.logged_in.tic_tae_toe

import com.uber.rib.core.ViewRouter

class TicTacToeRouter(
        view: TicTacToeView,
        interactor: TicTacToeInteractor,
        component: TicTacToeBuilder.Component) : ViewRouter<TicTacToeView, TicTacToeInteractor, TicTacToeBuilder.Component>(view, interactor, component)