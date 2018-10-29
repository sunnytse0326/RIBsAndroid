package hk.gogotech.ribs_poc.ribstree.logged_in.chatlist.typehint

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import hk.gogotech.ribs_poc.R
import kotlinx.android.synthetic.main.typehint_rib.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.runOnUiThread

class TypeHintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle), TypeHintInteractor.TypeHintPresenter {
    override fun setTypingText(value: String) {
        hintMessage.text = value
    }

    override fun animateTypeView(enable: Boolean) {
        context.runOnUiThread {
            hintMessage.animate().translationY(if (enable) 0F else -dip(40).toFloat()).setDuration(200).start()
        }
    }

    override fun initViewToRoundedBg() {
        val drawable = GradientDrawable()
        drawable.cornerRadius = dip(14).toFloat()
        drawable.setColor(context.resources.getColor(R.color.colorPrimary))
        hintMessage.setBackgroundDrawable(drawable)

        hintMessage.translationY = -dip(40).toFloat()
    }
}
