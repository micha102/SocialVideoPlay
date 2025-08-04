package com.small.socialvideoplay

import android.content.Context
import org.json.JSONArray
import androidx.core.content.edit

object PrefsHelper {
    private const val PREF_NAME = "videos"
    private const val KEY_LIST = "video_list"

    fun saveList(context: Context, list: List<Video>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonList = JSONArray()
        for (video in list) {
            jsonList.put(video.toJson())
        }
        prefs.edit { putString(KEY_LIST, jsonList.toString()) }
    }

    fun loadList(context: Context): MutableList<Video> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = JSONArray(prefs.getString(KEY_LIST, "[]") ?: "[]")
        val videoList = mutableListOf<Video>()
        for (i in 0 until json.length()) {
            val videoJson = json.getJSONObject(i)
            val video = Video.jsonToVideo(videoJson)
            videoList.add(video)
        }
        return videoList
    }
}
