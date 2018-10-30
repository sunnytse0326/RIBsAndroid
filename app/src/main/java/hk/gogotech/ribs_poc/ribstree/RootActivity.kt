package hk.gogotech.ribs_poc.ribstree

import android.os.Build
import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import hk.gogotech.ribs_poc.BuildConfig
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
        val myID = BuildConfig.ID
        val user = SendBirdUser(myID.toString(), BuildConfig.userName, "ORDERID199999", "https://media.whatscap.com/4f0/66a/4f066a068ebbd865fdf66af8288f894aca8e5a84_n.png")
        val order = OrderItem(BuildConfig.firstUserID.toString(), BuildConfig.secondUserID.toString())
        return LocalStorage(user, order)
    }
}
