package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.InteractorHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TypeHintInteractorTest : RibTestBasePlaceholder() {

  @Mock internal lateinit var presenter: TypeHintInteractor.TypeHintPresenter
  @Mock internal lateinit var router: TypeHintRouter

  private var interactor: TypeHintInteractor? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)

    interactor = TestTypeHintInteractor.create(presenter)
  }

  /**
   * TODO: Delete this example and add real tests.
   */
  @Test
  fun anExampleTest_withSomeConditions_shouldPass() {
    // Use InteractorHelper to drive your interactor's lifecycle.
    InteractorHelper.attach<TypeHintInteractor.TypeHintPresenter, TypeHintRouter>(interactor!!, presenter, router, null)
    InteractorHelper.detach(interactor!!)

    throw RuntimeException("Remove this test and add real tests.")
  }
}