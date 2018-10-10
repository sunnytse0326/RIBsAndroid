package hk.gogotech.ribs_poc.logged_in.off_game

import com.uber.rib.core.ViewRouter

class OffGameRouter (
        view: OffGameView,
        interactor: OffGameInteractor,
        component: OffGameBuilder.Component) : ViewRouter<OffGameView, OffGameInteractor, OffGameBuilder.Component>(view, interactor, component)