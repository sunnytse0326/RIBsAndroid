package hk.gogotech.ribs_poc.uicomponent

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dinuscxj.progressbar.CircleProgressBar
import com.sendbird.android.*
import hk.gogotech.ribs_poc.R
import hk.gogotech.ribs_poc.utils.*
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.*

internal class ChatListAdapter(private var mContext: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mFileMessageMap: HashMap<FileMessage, CircleProgressBar> = HashMap()
    private var mChannel: GroupChannel? = null
    private var mMessageList: MutableList<BaseMessage>

    private var mItemClickListener: OnItemClickListener? = null
    private var mItemLongClickListener: OnItemLongClickListener? = null

    private val mFailedMessageIdList = ArrayList<String>()
    private val mTempFileMessageUriTable = Hashtable<String, Uri>()
    @get:Synchronized
    @set:Synchronized
    private var isMessageListLoading: Boolean = false

    internal interface OnItemLongClickListener {
        fun onUserMessageItemLongClick(message: UserMessage, position: Int)

        fun onFileMessageItemLongClick(message: FileMessage)

        fun onAdminMessageItemLongClick(message: AdminMessage)
    }

    internal interface OnItemClickListener {
        fun onUserMessageItemClick(message: UserMessage)

        fun onFileMessageItemClick(message: FileMessage)
    }


    init {
        mMessageList = ArrayList()
    }

    fun updateList(list:MutableList<BaseMessage>){
        mMessageList = list
        notifyDataSetChanged()
    }

    fun setContext(context: Context) {
        mContext = context
    }

    fun load(channelUrl: String) {
        try {
            val appDir = File(mContext!!.cacheDir, SendBird.getApplicationId())
            appDir.mkdirs()

            val dataFile = File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().userId + channelUrl) + ".data")

            val content = FileUtils.loadFromFile(dataFile)
            val dataArray = content.split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

            mChannel = GroupChannel.buildFromSerializedData(Base64.decode(dataArray[0], Base64.DEFAULT or Base64.NO_WRAP)) as GroupChannel

            // Reset message list, then add cached messages.
            mMessageList.clear()
            for (i in 1 until dataArray.size) {
                mMessageList.add(BaseMessage.buildFromSerializedData(Base64.decode(dataArray[i], Base64.DEFAULT or Base64.NO_WRAP)))
            }
            notifyDataSetChanged()
        } catch (e: Exception) { }
    }

    fun save() {
        try {
            val sb = StringBuilder()
            if (mChannel != null) {
                // Convert current data into string.
                sb.append(Base64.encodeToString(mChannel!!.serialize(), Base64.DEFAULT or Base64.NO_WRAP))
                var message: BaseMessage? = null
                for (i in 0 until Math.min(mMessageList.size, 100)) {
                    message = mMessageList[i]
                    if (!isTempMessage(message)) {
                        sb.append("\n")
                        sb.append(Base64.encodeToString(message.serialize(), Base64.DEFAULT or Base64.NO_WRAP))
                    }
                }

                val data = sb.toString()
                val md5 = TextUtils.generateMD5(data)

                // Save the data into file.
                val appDir = File(mContext!!.cacheDir, SendBird.getApplicationId())
                appDir.mkdirs()

                val hashFile = File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().userId + mChannel!!.url) + ".hash")
                val dataFile = File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().userId + mChannel!!.url) + ".data")

                try {
                    val content = FileUtils.loadFromFile(hashFile)
                    // If data has not been changed, do not save.
                    if (md5 == content) {
                        return
                    }
                } catch (e: IOException) {
                    // File not found. Save the data.
                }

                FileUtils.saveToFile(dataFile, data)
                FileUtils.saveToFile(hashFile, md5)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Inflates the correct layout according to the View Type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                val myUserMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_user_me, parent, false)
                return MyUserMessageHolder(myUserMsgView)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                val otherUserMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_user_other, parent, false)
                return OtherUserMessageHolder(otherUserMsgView)
            }
            VIEW_TYPE_ADMIN_MESSAGE -> {
                val adminMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_admin, parent, false)
                return AdminMessageHolder(adminMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_ME -> {
                val myFileMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_file_me, parent, false)
                return MyFileMessageHolder(myFileMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_OTHER -> {
                val otherFileMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_file_other, parent, false)
                return OtherFileMessageHolder(otherFileMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_IMAGE_ME -> {
                val myImageFileMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_file_image_me, parent, false)
                return MyImageFileMessageHolder(myImageFileMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER -> {
                val otherImageFileMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_file_image_other, parent, false)
                return OtherImageFileMessageHolder(otherImageFileMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_VIDEO_ME -> {
                val myVideoFileMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_file_video_me, parent, false)
                return MyVideoFileMessageHolder(myVideoFileMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER -> {
                val otherVideoFileMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_file_video_other, parent, false)
                return OtherVideoFileMessageHolder(otherVideoFileMsgView)
            }
            else -> {
                val myUserMsgView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_group_chat_user_me, parent, false)
                return MyUserMessageHolder(myUserMsgView)
            }
        }
    }

    /**
     * Binds variables in the BaseMessage to UI components in the ViewHolder.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]
        var isContinuous = false
        var isNewDay = false
        var isTempMessage = false
        var isFailedMessage = false
        var tempFileMessageUri: Uri? = null

        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size - 1) {
            val prevMessage = mMessageList[position + 1]

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            if (!DateUtils.hasSameDate(message.createdAt, prevMessage.createdAt)) {
                isNewDay = true
                isContinuous = false
            } else {
                isContinuous = isContinuous(message, prevMessage)
            }
        } else if (position == mMessageList.size - 1) {
            isNewDay = true
        }

        isTempMessage = isTempMessage(message)
        tempFileMessageUri = getTempFileMessageUri(message)
        isFailedMessage = isFailedMessage(message)

        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> (holder as MyUserMessageHolder).bind(mContext, message as UserMessage, mChannel, isContinuous, isNewDay, isTempMessage, isFailedMessage, mItemClickListener, mItemLongClickListener, position)
            VIEW_TYPE_USER_MESSAGE_OTHER -> (holder as OtherUserMessageHolder).bind(mContext, message as UserMessage, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener, position)
            VIEW_TYPE_ADMIN_MESSAGE -> (holder as AdminMessageHolder).bind(mContext, message as AdminMessage, mChannel, isNewDay)
            VIEW_TYPE_FILE_MESSAGE_ME -> (holder as MyFileMessageHolder).bind(mContext, message as FileMessage, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener)
            VIEW_TYPE_FILE_MESSAGE_OTHER -> (holder as OtherFileMessageHolder).bind(mContext, message as FileMessage, mChannel, isNewDay, isContinuous, mItemClickListener)
            VIEW_TYPE_FILE_MESSAGE_IMAGE_ME -> (holder as MyImageFileMessageHolder).bind(mContext, message as FileMessage, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener)
            VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER -> (holder as OtherImageFileMessageHolder).bind(mContext, message as FileMessage, mChannel, isNewDay, isContinuous, mItemClickListener)
            VIEW_TYPE_FILE_MESSAGE_VIDEO_ME -> (holder as MyVideoFileMessageHolder).bind(mContext, message as FileMessage, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener)
            VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER -> (holder as OtherVideoFileMessageHolder).bind(mContext, message as FileMessage, mChannel, isNewDay, isContinuous, mItemClickListener)
            else -> {
            }
        }
    }

    /**
     * Declares the View Type according to the type of message and the sender.
     */
    override fun getItemViewType(position: Int): Int {

        val message = mMessageList[position]

        if (message is UserMessage) {
// If the sender is current user
            return if (message.sender.userId == SendBird.getCurrentUser().userId) {
                VIEW_TYPE_USER_MESSAGE_ME
            } else {
                VIEW_TYPE_USER_MESSAGE_OTHER
            }
        } else if (message is FileMessage) {
            return if (message.type.toLowerCase().startsWith("image")) {
                // If the sender is current user
                if (message.sender.userId == SendBird.getCurrentUser().userId) {
                    VIEW_TYPE_FILE_MESSAGE_IMAGE_ME
                } else {
                    VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER
                }
            } else if (message.type.toLowerCase().startsWith("video")) {
                if (message.sender.userId == SendBird.getCurrentUser().userId) {
                    VIEW_TYPE_FILE_MESSAGE_VIDEO_ME
                } else {
                    VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER
                }
            } else {
                if (message.sender.userId == SendBird.getCurrentUser().userId) {
                    VIEW_TYPE_FILE_MESSAGE_ME
                } else {
                    VIEW_TYPE_FILE_MESSAGE_OTHER
                }
            }
        } else if (message is AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE
        }

        return -1
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    fun setChannel(channel: GroupChannel) {
        mChannel = channel
    }

    fun isTempMessage(message: BaseMessage?): Boolean {
        return message!!.messageId == 0L
    }

    fun isFailedMessage(message: BaseMessage): Boolean {
        if (!isTempMessage(message)) {
            return false
        }

        if (message is UserMessage) {
            val index = mFailedMessageIdList.indexOf(message.requestId)
            if (index >= 0) {
                return true
            }
        } else if (message is FileMessage) {
            val index = mFailedMessageIdList.indexOf(message.requestId)
            if (index >= 0) {
                return true
            }
        }

        return false
    }


    fun getTempFileMessageUri(message: BaseMessage): Uri? {
        if (!isTempMessage(message)) {
            return null
        }

        return if (message !is FileMessage) {
            null
        } else mTempFileMessageUriTable[message.requestId]

    }

    fun markMessageFailed(requestId: String) {
        mFailedMessageIdList.add(requestId)
        notifyDataSetChanged()
    }

    fun removeFailedMessage(message: BaseMessage) {
        if (message is UserMessage) {
            mFailedMessageIdList.remove(message.requestId)
            mMessageList.remove(message)
        } else if (message is FileMessage) {
            mFailedMessageIdList.remove(message.requestId)
            mTempFileMessageUriTable.remove(message.requestId)
            mMessageList.remove(message)
        }

        notifyDataSetChanged()
    }

    fun setFileProgressPercent(message: FileMessage, percent: Int) {
        var msg: BaseMessage
        for (i in mMessageList.indices.reversed()) {
            msg = mMessageList[i]
            if (msg is FileMessage) {
                if (message.requestId == msg.requestId) {
                    val circleProgressBar = mFileMessageMap[message]
                    if (circleProgressBar != null) {
                        circleProgressBar!!.setProgress(percent)
                    }
                    break
                }
            }
        }
    }

    fun markMessageSent(message: BaseMessage) {
        var msg: BaseMessage
        for (i in mMessageList.indices.reversed()) {
            msg = mMessageList[i]
            if (message is UserMessage && msg is UserMessage) {
                if (msg.requestId == message.requestId) {
                    mMessageList[i] = message
                    notifyDataSetChanged()
                    return
                }
            } else if (message is FileMessage && msg is FileMessage) {
                if (msg.requestId == message.requestId) {
                    mTempFileMessageUriTable.remove(message.requestId)
                    mMessageList[i] = message
                    notifyDataSetChanged()
                    return
                }
            }
        }
    }

    fun addTempFileMessageInfo(message: FileMessage, uri: Uri) {
        mTempFileMessageUriTable[message.requestId] = uri
    }

    fun addFirst(message: BaseMessage) {
        mMessageList.add(0, message)
        notifyDataSetChanged()
    }

    fun delete(msgId: Long) {
        for (msg in mMessageList) {
            if (msg.messageId == msgId) {
                mMessageList.remove(msg)
                notifyDataSetChanged()
                break
            }
        }
    }

    fun update(message: BaseMessage) {
        var baseMessage: BaseMessage
        for (index in mMessageList.indices) {
            baseMessage = mMessageList[index]
            if (message.messageId == baseMessage.messageId) {
                mMessageList.removeAt(index)
                mMessageList.add(index, message)
                notifyDataSetChanged()
                break
            }
        }
    }

    /**
     * Notifies that the user has read all (previously unread) messages in the channel.
     * Typically, this would be called immediately after the user enters the chat and loads
     * its most recent messages.
     */
    fun markAllMessagesAsRead() {
        if (mChannel != null) {
            mChannel!!.markAsRead()
        }
        notifyDataSetChanged()
    }

    /**
     * Load old message list.
     * @param limit
     * @param handler
     */
    fun loadPreviousMessages(limit: Int, handler: BaseChannel.GetMessagesHandler?) {
        if (isMessageListLoading) {
            return
        }

        var oldestMessageCreatedAt = java.lang.Long.MAX_VALUE
        if (mMessageList.size > 0) {
            oldestMessageCreatedAt = mMessageList[mMessageList.size - 1].createdAt
        }

        isMessageListLoading = true
        mChannel!!.getPreviousMessagesByTimestamp(oldestMessageCreatedAt, false, limit, true, BaseChannel.MessageTypeFilter.ALL, null, BaseChannel.GetMessagesHandler { list, e ->
            handler?.onResult(list, e)

            isMessageListLoading = false
            if (e != null) {
                e.printStackTrace()
                return@GetMessagesHandler
            }

            for (message in list) {
                mMessageList.add(message)
            }

            notifyDataSetChanged()
        })
    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load or refresh.
     */
    fun loadLatestMessages(limit: Int, handler: BaseChannel.GetMessagesHandler?) {
        if (isMessageListLoading) {
            return
        }

        isMessageListLoading = true
        mChannel!!.getPreviousMessagesByTimestamp(java.lang.Long.MAX_VALUE, true, limit, true, BaseChannel.MessageTypeFilter.ALL, null, BaseChannel.GetMessagesHandler { list, e ->
            handler?.onResult(list, e)

            isMessageListLoading = false
            if (e != null) {
                e.printStackTrace()
                return@GetMessagesHandler
            }

            if (list.size <= 0) {
                return@GetMessagesHandler
            }

            for (message in mMessageList) {
                if (isTempMessage(message) || isFailedMessage(message)) {
                    list.add(0, message)
                }
            }

            mMessageList.clear()

            for (message in list) {
                mMessageList.add(message)
            }

            notifyDataSetChanged()
        })
    }

    fun setItemLongClickListener(listener: OnItemLongClickListener) {
        mItemLongClickListener = listener
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
    }

    /**
     * Checks if the current message was sent by the same person that sent the preceding message.
     *
     *
     * This is done so that redundant UI, such as sender nickname and profile picture,
     * does not have to displayed when not necessary.
     */
    private fun isContinuous(currentMsg: BaseMessage?, precedingMsg: BaseMessage?): Boolean {
        // null check
        if (currentMsg == null || precedingMsg == null) {
            return false
        }

        if (currentMsg is AdminMessage && precedingMsg is AdminMessage) {
            return true
        }

        var currentUser: User? = null
        var precedingUser: User? = null

        if (currentMsg is UserMessage) {
            currentUser = currentMsg.sender
        } else if (currentMsg is FileMessage) {
            currentUser = currentMsg.sender
        }

        if (precedingMsg is UserMessage) {
            precedingUser = precedingMsg.sender
        } else if (precedingMsg is FileMessage) {
            precedingUser = precedingMsg.sender
        }

        // If admin message or
        return !(currentUser == null || precedingUser == null) && currentUser.userId == precedingUser.userId


    }


    private inner class AdminMessageHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView
        private val dateText: TextView

        init {

            messageText = itemView.findViewById<View>(R.id.text_group_chat_message) as TextView
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
        }

        internal fun bind(context: Context?, message: AdminMessage, channel: GroupChannel?, isNewDay: Boolean) {
            messageText.text = message.message

            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }
        }
    }

    private inner class MyUserMessageHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView
        internal var editedText: TextView
        internal var timeText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var urlPreviewContainer: ViewGroup
        internal var urlPreviewSiteNameText: TextView
        internal var urlPreviewTitleText: TextView
        internal var urlPreviewDescriptionText: TextView
        internal var urlPreviewMainImageView: ImageView
        internal var padding: View

        init {

            messageText = itemView.findViewById<View>(R.id.text_group_chat_message) as TextView
            editedText = itemView.findViewById<View>(R.id.text_group_chat_edited) as TextView
            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView

            urlPreviewContainer = itemView.findViewById<View>(R.id.url_preview_container) as ViewGroup
            urlPreviewSiteNameText = itemView.findViewById<View>(R.id.text_url_preview_site_name) as TextView
            urlPreviewTitleText = itemView.findViewById<View>(R.id.text_url_preview_title) as TextView
            urlPreviewDescriptionText = itemView.findViewById<View>(R.id.text_url_preview_description) as TextView
            urlPreviewMainImageView = itemView.findViewById<View>(R.id.image_url_preview_main) as ImageView

            // Dynamic padding that can be hidden or shown based on whether the message is continuous.
            padding = itemView.findViewById(R.id.view_group_chat_padding)
        }

        internal fun bind(context: Context?, message: UserMessage, channel: GroupChannel?, isContinuous: Boolean, isNewDay: Boolean, isTempMessage: Boolean, isFailedMessage: Boolean, clickListener: OnItemClickListener?, longClickListener: OnItemLongClickListener?, position: Int) {
            messageText.text = message.message
            timeText.setText(DateUtils.formatTime(message.createdAt))

            if (message.updatedAt > 0) {
                editedText.visibility = View.VISIBLE
            } else {
                editedText.visibility = View.GONE
            }

            if (isFailedMessage) {
                readReceiptText.setText(R.string.message_failed)
                readReceiptText.visibility = View.VISIBLE
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.message_sending)
                readReceiptText.visibility = View.VISIBLE
            } else {

                // Since setChannel is set slightly after adapter is created
                if (channel != null) {
                    val readReceipt = channel.getReadReceipt(message)
                    if (readReceipt > 0) {
                        readReceiptText.visibility = View.VISIBLE
                        readReceiptText.text = readReceipt.toString()
                    } else {
                        readReceiptText.visibility = View.INVISIBLE
                    }
                }
            }

            // If continuous from previous message, remove extra padding.
            if (isContinuous) {
                padding.visibility = View.GONE
            } else {
                padding.visibility = View.VISIBLE
            }

            // If the message is sent on a different date than the previous one, display the date.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            urlPreviewContainer.visibility = View.GONE
            if (message.customType == URL_PREVIEW_CUSTOM_TYPE) {
                try {
                    urlPreviewContainer.visibility = View.VISIBLE
                    val previewInfo = UrlPreviewInfo(message.data)
                    urlPreviewSiteNameText.text = "@" + previewInfo.siteName
                    urlPreviewTitleText.setText(previewInfo.title)
                    urlPreviewDescriptionText.setText(previewInfo.description)
                    ImageUtils.displayImageFromUrl(context!!, previewInfo.imageUrl, urlPreviewMainImageView, null, null)
                } catch (e: JSONException) {
                    urlPreviewContainer.visibility = View.GONE
                    e.printStackTrace()
                }

            }

            if (clickListener != null) {
                itemView.setOnClickListener { clickListener.onUserMessageItemClick(message) }
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener {
                    longClickListener.onUserMessageItemLongClick(message, position)
                    true
                }
            }
        }

    }

    private inner class OtherUserMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView
        internal var editedText: TextView
        internal var nicknameText: TextView
        internal var timeText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var profileImage: ImageView

        internal var urlPreviewContainer: ViewGroup
        internal var urlPreviewSiteNameText: TextView
        internal var urlPreviewTitleText: TextView
        internal var urlPreviewDescriptionText: TextView
        internal var urlPreviewMainImageView: ImageView

        init {

            messageText = itemView.findViewById<View>(R.id.text_group_chat_message) as TextView
            editedText = itemView.findViewById<View>(R.id.text_group_chat_edited) as TextView
            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            nicknameText = itemView.findViewById<View>(R.id.text_group_chat_nickname) as TextView
            profileImage = itemView.findViewById<View>(R.id.image_group_chat_profile) as ImageView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView

            urlPreviewContainer = itemView.findViewById<View>(R.id.url_preview_container) as ViewGroup
            urlPreviewSiteNameText = itemView.findViewById<View>(R.id.text_url_preview_site_name) as TextView
            urlPreviewTitleText = itemView.findViewById<View>(R.id.text_url_preview_title) as TextView
            urlPreviewDescriptionText = itemView.findViewById<View>(R.id.text_url_preview_description) as TextView
            urlPreviewMainImageView = itemView.findViewById<View>(R.id.image_url_preview_main) as ImageView
        }


        internal fun bind(context: Context?, message: UserMessage, channel: GroupChannel?, isNewDay: Boolean, isContinuous: Boolean, clickListener: OnItemClickListener?, longClickListener: OnItemLongClickListener?, position: Int) {

            // Since setChannel is set slightly after adapter is created
            if (channel != null) {
                val readReceipt = channel.getReadReceipt(message)
                if (readReceipt > 0) {
                    readReceiptText.visibility = View.VISIBLE
                    readReceiptText.text = readReceipt.toString()
                } else {
                    readReceiptText.visibility = View.INVISIBLE
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.visibility = View.INVISIBLE
                nicknameText.visibility = View.GONE
            } else {
                profileImage.visibility = View.VISIBLE
                ImageUtils.displayRoundImageFromUrl(context!!, message.sender.profileUrl, profileImage)

                nicknameText.visibility = View.VISIBLE
                nicknameText.text = message.sender.nickname
            }

            messageText.text = message.message
            timeText.setText(DateUtils.formatTime(message.createdAt))

            if (message.updatedAt > 0) {
                editedText.visibility = View.VISIBLE
            } else {
                editedText.visibility = View.GONE
            }

            urlPreviewContainer.visibility = View.GONE
            if (message.customType == URL_PREVIEW_CUSTOM_TYPE) {
                try {
                    urlPreviewContainer.visibility = View.VISIBLE
                    val previewInfo = UrlPreviewInfo(message.data)
                    urlPreviewSiteNameText.text = "@" + previewInfo.siteName
                    urlPreviewTitleText.setText(previewInfo.title)
                    urlPreviewDescriptionText.setText(previewInfo.description)
                    ImageUtils.displayImageFromUrl(context!!, previewInfo.imageUrl, urlPreviewMainImageView, null, null)
                } catch (e: JSONException) {
                    urlPreviewContainer.visibility = View.GONE
                    e.printStackTrace()
                }

            }


            if (clickListener != null) {
                itemView.setOnClickListener { clickListener.onUserMessageItemClick(message) }
            }
            if (longClickListener != null) {
                itemView.setOnLongClickListener {
                    longClickListener.onUserMessageItemLongClick(message, position)
                    true
                }
            }
        }
    }

    private inner class MyFileMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var fileNameText: TextView
        internal var timeText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var circleProgressBar: CircleProgressBar

        init {

            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            fileNameText = itemView.findViewById<View>(R.id.text_group_chat_file_name) as TextView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            circleProgressBar = itemView.findViewById<View>(R.id.circle_progress) as CircleProgressBar
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
        }

        internal fun bind(context: Context?, message: FileMessage, channel: GroupChannel?, isNewDay: Boolean, isTempMessage: Boolean, isFailedMessage: Boolean, tempFileMessageUri: Uri?, listener: OnItemClickListener?) {
            fileNameText.text = message.name
            timeText.setText(DateUtils.formatTime(message.createdAt))

            if (isFailedMessage) {
                readReceiptText.setText(R.string.message_failed)
                readReceiptText.visibility = View.VISIBLE

                circleProgressBar.setVisibility(View.GONE)
                mFileMessageMap.remove(message)
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.message_sending)
                readReceiptText.visibility = View.GONE

                circleProgressBar.setVisibility(View.VISIBLE)
                mFileMessageMap[message] = circleProgressBar
            } else {
                circleProgressBar.setVisibility(View.GONE)
                mFileMessageMap.remove(message)

                if (channel != null) {
                    val readReceipt = channel.getReadReceipt(message)
                    if (readReceipt > 0) {
                        readReceiptText.visibility = View.VISIBLE
                        readReceiptText.text = readReceipt.toString()
                    } else {
                        readReceiptText.visibility = View.INVISIBLE
                    }
                }

            }
            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            if (listener != null) {
                itemView.setOnClickListener { listener.onFileMessageItemClick(message) }
            }
        }
    }

    private inner class OtherFileMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var nicknameText: TextView
        internal var timeText: TextView
        internal var fileNameText: TextView
        internal var fileSizeText: TextView? = null
        internal var dateText: TextView
        internal var readReceiptText: TextView
        internal var profileImage: ImageView

        init {

            nicknameText = itemView.findViewById<View>(R.id.text_group_chat_nickname) as TextView
            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            fileNameText = itemView.findViewById<View>(R.id.text_group_chat_file_name) as TextView
            //            fileSizeText = (TextView) itemView.findViewById(R.id.text_group_chat_file_size);

            profileImage = itemView.findViewById<View>(R.id.image_group_chat_profile) as ImageView
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
        }

        internal fun bind(context: Context?, message: FileMessage, channel: GroupChannel?, isNewDay: Boolean, isContinuous: Boolean, listener: OnItemClickListener?) {
            fileNameText.text = message.name
            timeText.setText(DateUtils.formatTime(message.createdAt))
            //            fileSizeText.setText(String.valueOf(message.getSize()));

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                val readReceipt = channel.getReadReceipt(message)
                if (readReceipt > 0) {
                    readReceiptText.visibility = View.VISIBLE
                    readReceiptText.text = readReceipt.toString()
                } else {
                    readReceiptText.visibility = View.INVISIBLE
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.visibility = View.INVISIBLE
                nicknameText.visibility = View.GONE
            } else {
                profileImage.visibility = View.VISIBLE
                ImageUtils.displayRoundImageFromUrl(context!!, message.sender.profileUrl, profileImage)

                nicknameText.visibility = View.VISIBLE
                nicknameText.text = message.sender.nickname
            }

            if (listener != null) {
                itemView.setOnClickListener { listener.onFileMessageItemClick(message) }
            }
        }
    }

    /**
     * A ViewHolder for file messages that are images.
     * Displays only the image thumbnail.
     */
    private inner class MyImageFileMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var timeText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var fileThumbnailImage: ImageView
        internal var circleProgressBar: CircleProgressBar

        init {

            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            fileThumbnailImage = itemView.findViewById<View>(R.id.image_group_chat_file_thumbnail) as ImageView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            circleProgressBar = itemView.findViewById<View>(R.id.circle_progress) as CircleProgressBar
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
        }

        internal fun bind(context: Context?, message: FileMessage, channel: GroupChannel?, isNewDay: Boolean, isTempMessage: Boolean, isFailedMessage: Boolean, tempFileMessageUri: Uri?, listener: OnItemClickListener?) {
            timeText.setText(DateUtils.formatTime(message.createdAt))

            if (isFailedMessage) {
                readReceiptText.setText(R.string.message_failed)
                readReceiptText.visibility = View.VISIBLE

                circleProgressBar.setVisibility(View.GONE)
                mFileMessageMap.remove(message)
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.message_sending)
                readReceiptText.visibility = View.GONE

                circleProgressBar.setVisibility(View.VISIBLE)
                mFileMessageMap[message] = circleProgressBar
            } else {
                circleProgressBar.setVisibility(View.GONE)
                mFileMessageMap.remove(message)

                // Since setChannel is set slightly after adapter is created, check if null.
                if (channel != null) {
                    val readReceipt = channel.getReadReceipt(message)
                    if (readReceipt > 0) {
                        readReceiptText.visibility = View.VISIBLE
                        readReceiptText.text = readReceipt.toString()
                    } else {
                        readReceiptText.visibility = View.INVISIBLE
                    }
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            if (isTempMessage && tempFileMessageUri != null) {
                ImageUtils.displayImageFromUrl(context!!, tempFileMessageUri.toString(), fileThumbnailImage, null, null)
            } else {
                // Get thumbnails from FileMessage
                val thumbnails = message.thumbnails as ArrayList<FileMessage.Thumbnail>

                // If thumbnails exist, get smallest (first) thumbnail and display it in the message
                if (thumbnails.size > 0) {
                    if (message.type.toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context!!, message.url, fileThumbnailImage, thumbnails[0].url, fileThumbnailImage.drawable)
                    } else {
                        ImageUtils.displayImageFromUrl(context!!, thumbnails[0].url, fileThumbnailImage, fileThumbnailImage.drawable, null)
                    }
                } else {
                    if (message.type.toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context!!, message.url, fileThumbnailImage, null as String?, fileThumbnailImage.drawable)
                    } else {
                        ImageUtils.displayImageFromUrl(context!!, message.url, fileThumbnailImage, fileThumbnailImage.drawable, null)
                    }
                }
            }

            if (listener != null) {
                itemView.setOnClickListener { listener.onFileMessageItemClick(message) }
            }
        }
    }

    private inner class OtherImageFileMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var timeText: TextView
        internal var nicknameText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var profileImage: ImageView
        internal var fileThumbnailImage: ImageView

        init {

            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            nicknameText = itemView.findViewById<View>(R.id.text_group_chat_nickname) as TextView
            fileThumbnailImage = itemView.findViewById<View>(R.id.image_group_chat_file_thumbnail) as ImageView
            profileImage = itemView.findViewById<View>(R.id.image_group_chat_profile) as ImageView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
        }

        internal fun bind(context: Context?, message: FileMessage, channel: GroupChannel?, isNewDay: Boolean, isContinuous: Boolean, listener: OnItemClickListener?) {
            timeText.setText(DateUtils.formatTime(message.createdAt))

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                val readReceipt = channel.getReadReceipt(message)
                if (readReceipt > 0) {
                    readReceiptText.visibility = View.VISIBLE
                    readReceiptText.text = readReceipt.toString()
                } else {
                    readReceiptText.visibility = View.INVISIBLE
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.visibility = View.INVISIBLE
                nicknameText.visibility = View.GONE
            } else {
                profileImage.visibility = View.VISIBLE
                ImageUtils.displayRoundImageFromUrl(context!!, message.sender.profileUrl, profileImage)

                nicknameText.visibility = View.VISIBLE
                nicknameText.text = message.sender.nickname
            }

            // Get thumbnails from FileMessage
            val thumbnails = message.thumbnails as ArrayList<FileMessage.Thumbnail>

            // If thumbnails exist, get smallest (first) thumbnail and display it in the message
            if (thumbnails.size > 0) {
                if (message.type.toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context!!, message.url, fileThumbnailImage, thumbnails[0].url, fileThumbnailImage.drawable)
                } else {
                    ImageUtils.displayImageFromUrl(context!!, thumbnails[0].url, fileThumbnailImage, fileThumbnailImage.drawable, null)
                }
            } else {
                if (message.type.toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context!!, message.url, fileThumbnailImage, null as String?, fileThumbnailImage.drawable)
                } else {
                    ImageUtils.displayImageFromUrl(context!!, message.url, fileThumbnailImage, fileThumbnailImage.drawable, null)
                }
            }

            if (listener != null) {
                itemView.setOnClickListener { listener.onFileMessageItemClick(message) }
            }
        }
    }

    /**
     * A ViewHolder for file messages that are videos.
     * Displays only the video thumbnail.
     */
    private inner class MyVideoFileMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var timeText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var fileThumbnailImage: ImageView
        internal var circleProgressBar: CircleProgressBar

        init {

            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            fileThumbnailImage = itemView.findViewById<View>(R.id.image_group_chat_file_thumbnail) as ImageView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            circleProgressBar = itemView.findViewById<View>(R.id.circle_progress) as CircleProgressBar
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
        }

        internal fun bind(context: Context?, message: FileMessage, channel: GroupChannel?, isNewDay: Boolean, isTempMessage: Boolean, isFailedMessage: Boolean, tempFileMessageUri: Uri?, listener: OnItemClickListener?) {
            timeText.setText(DateUtils.formatTime(message.createdAt))

            if (isFailedMessage) {
                readReceiptText.setText(R.string.message_failed)
                readReceiptText.visibility = View.VISIBLE

                circleProgressBar.setVisibility(View.GONE)
                mFileMessageMap.remove(message)
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.message_sending)
                readReceiptText.visibility = View.GONE

                circleProgressBar.setVisibility(View.VISIBLE)
                mFileMessageMap[message] = circleProgressBar
            } else {
                circleProgressBar.setVisibility(View.GONE)
                mFileMessageMap.remove(message)

                // Since setChannel is set slightly after adapter is created, check if null.
                if (channel != null) {
                    val readReceipt = channel.getReadReceipt(message)
                    if (readReceipt > 0) {
                        readReceiptText.visibility = View.VISIBLE
                        readReceiptText.text = readReceipt.toString()
                    } else {
                        readReceiptText.visibility = View.INVISIBLE
                    }
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            if (isTempMessage && tempFileMessageUri != null) {
                ImageUtils.displayImageFromUrl(context!!, tempFileMessageUri.toString(), fileThumbnailImage, null, null)
            } else {
                // Get thumbnails from FileMessage
                val thumbnails = message.thumbnails as ArrayList<FileMessage.Thumbnail>

                // If thumbnails exist, get smallest (first) thumbnail and display it in the message
                if (thumbnails.size > 0) {
                    ImageUtils.displayImageFromUrl(context!!, thumbnails[0].url, fileThumbnailImage, fileThumbnailImage.drawable, null)
                }
            }

            if (listener != null) {
                itemView.setOnClickListener { listener.onFileMessageItemClick(message) }
            }
        }
    }

    private inner class OtherVideoFileMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var timeText: TextView
        internal var nicknameText: TextView
        internal var readReceiptText: TextView
        internal var dateText: TextView
        internal var profileImage: ImageView
        internal var fileThumbnailImage: ImageView

        init {

            timeText = itemView.findViewById<View>(R.id.text_group_chat_time) as TextView
            nicknameText = itemView.findViewById<View>(R.id.text_group_chat_nickname) as TextView
            fileThumbnailImage = itemView.findViewById<View>(R.id.image_group_chat_file_thumbnail) as ImageView
            profileImage = itemView.findViewById<View>(R.id.image_group_chat_profile) as ImageView
            readReceiptText = itemView.findViewById<View>(R.id.text_group_chat_read_receipt) as TextView
            dateText = itemView.findViewById<View>(R.id.text_group_chat_date) as TextView
        }

        internal fun bind(context: Context?, message: FileMessage, channel: GroupChannel?, isNewDay: Boolean, isContinuous: Boolean, listener: OnItemClickListener?) {
            timeText.setText(DateUtils.formatTime(message.createdAt))

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                val readReceipt = channel.getReadReceipt(message)
                if (readReceipt > 0) {
                    readReceiptText.visibility = View.VISIBLE
                    readReceiptText.text = readReceipt.toString()
                } else {
                    readReceiptText.visibility = View.INVISIBLE
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.setText(DateUtils.formatDate(message.createdAt))
            } else {
                dateText.visibility = View.GONE
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.visibility = View.INVISIBLE
                nicknameText.visibility = View.GONE
            } else {
                profileImage.visibility = View.VISIBLE
                ImageUtils.displayRoundImageFromUrl(context!!, message.sender.profileUrl, profileImage)

                nicknameText.visibility = View.VISIBLE
                nicknameText.text = message.sender.nickname
            }

            // Get thumbnails from FileMessage
            val thumbnails = message.thumbnails as ArrayList<FileMessage.Thumbnail>

            // If thumbnails exist, get smallest (first) thumbnail and display it in the message
            if (thumbnails.size > 0) {
                ImageUtils.displayImageFromUrl(context!!, thumbnails[0].url, fileThumbnailImage, fileThumbnailImage.drawable, null)
            }

            if (listener != null) {
                itemView.setOnClickListener { listener.onFileMessageItemClick(message) }
            }
        }
    }

    companion object {
        val URL_PREVIEW_CUSTOM_TYPE = "url_preview"

        private val VIEW_TYPE_USER_MESSAGE_ME = 10
        private val VIEW_TYPE_USER_MESSAGE_OTHER = 11
        private val VIEW_TYPE_FILE_MESSAGE_ME = 20
        private val VIEW_TYPE_FILE_MESSAGE_OTHER = 21
        private val VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22
        private val VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23
        private val VIEW_TYPE_FILE_MESSAGE_VIDEO_ME = 24
        private val VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER = 25
        private val VIEW_TYPE_ADMIN_MESSAGE = 30
    }
}