package hk.gogotech.ribs_poc.channel_list

import com.uber.rib.core.ViewRouter

class ChannelListRouter(
        view: ChannelListView,
        interactor: ChannelListInteractor,
        component: ChannelListBuilder.Component) : ViewRouter<ChannelListView, ChannelListInteractor, ChannelListBuilder.Component>(view, interactor, component)