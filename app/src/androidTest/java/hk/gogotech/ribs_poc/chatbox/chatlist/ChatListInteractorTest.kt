package hk.gogotech.ribs_poc.chatbox.chatlist

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.InteractorHelper
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListInteractor
import hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.ChatListRouter

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ChatListInteractorTest : RibTestBasePlaceholder() {

  @Mock internal lateinit var presenter: ChatListInteractor.ChatListPresenter
  @Mock internal lateinit var router: ChatListRouter

  private var interactor: ChatListInteractor? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)

    interactor = TestChatListInteractor.create(presenter)
  }

  /**
   * TODO: Delete this example and add real tests.
   */
  @Test
  fun anExampleTest_withSomeConditions_shouldPass() {
    // Use InteractorHelper to drive your interactor's lifecycle.
    InteractorHelper.attach<ChatListInteractor.ChatListPresenter, ChatListRouter>(interactor!!, presenter, router, null)
    InteractorHelper.detach(interactor!!)

    throw RuntimeException("Remove this test and add real tests.")
  }
}