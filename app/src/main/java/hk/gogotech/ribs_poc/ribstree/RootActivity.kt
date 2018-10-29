package hk.gogotech.ribs_poc.ribstree

import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import hk.gogotech.ribs_poc.model.OrderItem
import hk.gogotech.ribs_poc.model.SendBirdUser
import hk.gogotech.ribs_poc.ribstree.root.RootBuilder
import hk.gogotech.ribs_poc.storage.LocalStorage

class RootActivity : RibActivity() {
    val userID1 = "12345345342564"
    val userID2 = "985737575325"

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *, *> {
        val rootBuilder = RootBuilder(object : RootBuilder.ParentComponent {
            override fun localMemory(): LocalStorage {
                return getLocalStorage()
            }
        })
        return rootBuilder.build(parentViewGroup)
    }

    private fun getLocalStorage(): LocalStorage {
        // TODO user data are required to handle separately
        //val user = SendBirdUser(userID2, "SunnyTse22222", "ORDERID199999", "https://media.whatscap.com/4f0/66a/4f066a068ebbd865fdf66af8288f894aca8e5a84_n.png")
        val user = SendBirdUser(userID1, "SunnyTse", "ORDERID19999", "https://media.whatscap.com/204/442/20444223eba1e5ce6d989287ccfcf45f9c08f045_b.jpg")
        val order = OrderItem(userID1, userID2)
        return LocalStorage(user, order)
    }
}
