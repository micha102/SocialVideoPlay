package com.small.socialvideoplay

import org.json.JSONObject

data class Video (
    val status: String? = null,
    val originalUrl: String? = null,
    val title: String? = null,
    val url: String? = null,
    val duration: Long? = 0,
    val thumbnail: String? = null,
    val cookies: String? = null,
    val error: String? = null,
) {
    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("status", status)
        jsonObject.put("title", title)
        jsonObject.put("url", url)
        jsonObject.put("duration", duration)
        jsonObject.put("thumbnail", thumbnail)
        jsonObject.put("cookies", cookies)
        jsonObject.put("error", error)
        jsonObject.put("originalUrl", originalUrl)
        return jsonObject
    }
    companion object {

        fun jsonToVideo(jsonObject: JSONObject): Video {
            return Video(
                status = jsonObject.getString("status"),
                title = if (jsonObject.has("title")) jsonObject.getString("title") else null,
                url = if (jsonObject.has("url")) jsonObject.getString("url") else null,
                duration = if (jsonObject.has("duration")) jsonObject.getLong("duration") else -1,
                thumbnail = if (jsonObject.has("thumbnail")) jsonObject.getString("thumbnail") else null,
                cookies = if (jsonObject.has("cookies")) jsonObject.getString("cookies") else null,
                error = if (jsonObject.has("error")) jsonObject.getString("error") else null,
                originalUrl = jsonObject.getString("originalUrl"),
            )
        }
    }

}
