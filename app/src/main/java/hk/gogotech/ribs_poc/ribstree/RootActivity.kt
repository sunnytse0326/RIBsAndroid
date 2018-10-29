package hk.gogotech.ribs_poc.ribstree

import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import hk.gogotech.ribs_poc.model.OrderItem
import hk.gogotech.ribs_poc.model.SendBirdUser
import hk.gogotech.ribs_poc.ribstree.root.RootBuilder
import hk.gogotech.ribs_poc.storage.LocalStorage

class RootActivity : RibActivity() {
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
        val user = SendBirdUser("985737575894", "SunnyTse22222", "ORDERID1999", "https://media.whatscap.com/204/442/20444223eba1e5ce6d989287ccfcf45f9c08f045_b.jpg")
        //val user = SendBirdUser("123453453425623", "SunnyTse", "ORDERID1999", "https://media.whatscap.com/204/442/20444223eba1e5ce6d989287ccfcf45f9c08f045_b.jpg")
        val order = OrderItem("985737575894", "123453453425623")
        return LocalStorage(user, order)
    }
}
