package hk.gogotech.ribs_poc.utils

import com.sendbird.android.GroupChannel
import com.sendbird.android.SendBird
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object TextUtils {
    fun getGroupChannelTitle(channel: GroupChannel): String {
        val members = channel.members

        if (members.size < 2 || SendBird.getCurrentUser() == null) {
            return "No Members"
        } else if (members.size == 2) {
            val names = StringBuffer()
            for (member in members) {
                if (member.userId == SendBird.getCurrentUser().userId) {
                    continue
                }

                names.append(", " + member.nickname)
            }
            return names.delete(0, 2).toString()
        } else {
            var count = 0
            val names = StringBuffer()
            for (member in members) {
                if (member.userId == SendBird.getCurrentUser().userId) {
                    continue
                }

                count++
                names.append(", " + member.nickname)

                if (count >= 10) {
                    break
                }
            }
            return names.delete(0, 2).toString()
        }
    }

    /**
     * Calculate MD5
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    fun generateMD5(data: String): String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(data.toByteArray())
        val messageDigest = digest.digest()

        val hexString = StringBuffer()
        for (i in messageDigest.indices)
            hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))

        return hexString.toString()
    }
}
