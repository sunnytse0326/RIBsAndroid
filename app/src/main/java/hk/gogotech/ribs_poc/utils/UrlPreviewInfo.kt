package hk.gogotech.ribs_poc.utils

import org.json.JSONException
import org.json.JSONObject

class UrlPreviewInfo {
    var url: String
    var siteName: String
    var title: String
    var description: String
    var imageUrl: String

    constructor(url: String, siteName: String, title: String, description: String, imageUrl: String) {
        this.url = url
        this.siteName = siteName
        this.title = title
        this.description = description
        this.imageUrl = imageUrl
    }

    @Throws(JSONException::class)
    constructor(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        url = jsonObject.getString("url")
        siteName = jsonObject.getString("site_name")
        title = jsonObject.getString("title")
        description = jsonObject.getString("description")
        imageUrl = jsonObject.getString("image")
    }

    @Throws(JSONException::class)
    fun toJsonString(): String {
        val jsonObject = JSONObject()
        jsonObject.put("url", url)
        jsonObject.put("site_name", siteName)
        jsonObject.put("title", title)
        jsonObject.put("description", description)
        jsonObject.put("image", imageUrl)

        return jsonObject.toString()
    }
}