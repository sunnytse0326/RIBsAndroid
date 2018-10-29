package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.Toast
import com.jakewharton.rxbinding2.InitialValueObservable
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.chatlist_rib.view.*
import java.util.*

class ChatListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle), ChatListInteractor.ChatListPresenter {
    override fun getRecyclerView(): RecyclerView {
        return recycler_open_channel_chat
    }

    override fun getContent(): Context {
        return context
    }

    override fun setEditTextContent(msg: String) {
        edittext_chat_message.setText(msg)
    }

    override fun monitorTextFromEditText(): InitialValueObservable<CharSequence> {
        return RxTextView.textChanges(edittext_chat_message)
    }

    override fun getTextFromSendBtn(): Observable<*> {
        return RxView.clicks(button_open_channel_chat_send)
    }

    override fun getTextFromEditText(): Observable<TextViewEditorActionEvent> {
        return RxTextView.editorActionEvents(edittext_chat_message)
    }

    override fun displayErrMsg(msg: String) {
        err_msg.text = msg
    }

    override fun setLoadingVisibility(visibility: Int) {
        channel_list_progress_lty.visibility = visibility
    }

    override fun getEditTextContent(): String {
        return edittext_chat_message.text.toString()
    }

    override fun setSendBtnEnable(enable: Boolean){
        button_open_channel_chat_send.isEnabled = enable
    }
}
