package hk.gogotech.ribs_poc.chatbox.chatlist

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.RouterHelper
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListBuilder
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListInteractor
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListRouter
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListView

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ChatListRouterTest : RibTestBasePlaceholder() {

  @Mock internal lateinit var component: ChatListBuilder.Component
  @Mock internal lateinit var interactor: ChatListInteractor
  @Mock internal lateinit var view: ChatListView

  private var router: ChatListRouter? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)

    router = ChatListRouter(view, interactor, component)
  }

  /**
   * TODO: Delete this example and add real tests.
   */
  @Test
  fun anExampleTest_withSomeConditions_shouldPass() {
    // Use RouterHelper to drive your router's lifecycle.
    RouterHelper.attach(router!!)
    RouterHelper.detach(router!!)

    throw RuntimeException("Remove this test and add real tests.")
  }

}

