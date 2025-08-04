package com.small.socialvideoplay

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.net.toUri

class VideoListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private lateinit var emptyView: TextView
    private var videoList: MutableList<Video> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)
        emptyView = findViewById(R.id.emptyView)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        videoList = PrefsHelper.loadList(this)

        adapter = VideoAdapter(videoList,
            onClick = { item ->
                val intent = Intent(Intent.ACTION_VIEW, (item as Video).originalUrl?.toUri())
                startActivity(intent)
            },
            onLongClick = { view, item ->
                showContextMenu(view, item)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        updateEmptyView()
    }
    override fun onResume() {
        super.onResume()
        videoList.clear()
        videoList.addAll(PrefsHelper.loadList(this))
        adapter.notifyDataSetChanged()
        updateEmptyView()
    }
    private fun showContextMenu(view: View, item: Video) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.video_context_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_copy_link -> {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = ClipData.newPlainText("Video URL", item.originalUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Link copied", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_delete -> {
                    videoList.remove(item)
                    PrefsHelper.saveList(this, videoList)
                    adapter.notifyDataSetChanged()
                    updateEmptyView()
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun updateEmptyView() {
        emptyView.visibility = if (videoList.isEmpty()) View.VISIBLE else View.GONE
    }
}
