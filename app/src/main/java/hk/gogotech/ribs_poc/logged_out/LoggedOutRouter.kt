package hk.gogotech.ribs_poc.logged_out

import com.uber.rib.core.ViewRouter

class LoggedOutRouter(
        view: LoggedOutView,
        interactor: LoggedOutInteractor,
        component: LoggedOutBuilder.Component) : ViewRouter<LoggedOutView, LoggedOutInteractor, LoggedOutBuilder.Component>(view, interactor, component)