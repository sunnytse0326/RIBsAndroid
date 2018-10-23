package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.jakewharton.rxbinding2.InitialValueObservable
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.chatlist_rib.view.*
import java.util.*

class ChatListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle), ChatListInteractor.ChatListPresenter {
    override fun getTextFromEditText(): InitialValueObservable<CharSequence> {
        return RxTextView.textChanges(edittext_chat_message)
    }

    override fun displayErrMsg(msg: String) {
        err_msg.text = msg
    }

    override fun setLoadingVisibility(visibility: Int) {
        channel_list_progress_lty.visibility = visibility
    }


}
