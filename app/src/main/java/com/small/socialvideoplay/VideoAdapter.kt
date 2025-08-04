package com.small.socialvideoplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(
    private val videos: List<Video>,
    private val onLongClick: (View, Video) -> Unit,
    private val onClick: (Video) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        val platform: TextView = view.findViewById(R.id.platform)
        val title: TextView = view.findViewById(R.id.title)
        val duration: TextView = view.findViewById(R.id.duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_card, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]

        holder.platform.text = getPlatformName(video.originalUrl)
        holder.title.text = video.title ?: "Untitled"
        holder.duration.text = formatDuration(video.duration)

        Glide.with(holder.itemView)
            .load(video.thumbnail)
            .placeholder(android.R.color.darker_gray)
            .into(holder.thumbnail)

        holder.itemView.setOnLongClickListener {
            onLongClick(it, video)
            true
        }
        holder.itemView.setOnClickListener {
            onClick(video)
            true
        }
    }
    fun getPlatformName(url: String?): String {
        return when {
            url == null -> "Unknown"
            "tiktok.com" in url -> "TikTok"
            "instagram.com" in url -> "Instagram"
            "facebook.com" in url -> "Facebook"
            "x.com" in url || "twitter.com" in url -> "X"
            else -> "Unknown"
        }
    }
    fun formatDuration(seconds: Long?): String {
        val safeSeconds = seconds ?: 0
        val minutes = safeSeconds / 60
        val remaining = safeSeconds % 60
        return String.format("%02d:%02d", minutes, remaining)
    }



    override fun getItemCount(): Int = videos.size
}
