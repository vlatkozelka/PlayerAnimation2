package com.example.playeranimation2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.PublishSubject
import org.notests.sharedsequence.Driver


data class AppState(
  val songs: List<Song> = Song.getRandomSongs(),
  val currentSong: Int = 0,
  val expandedPercent: Float = 0f
)

class MainActivity : AppCompatActivity() {

    companion object {
        var appState = AppState()
        val appStateObservable = PublishSubject.create<AppState>()
        val appStateDriver = Driver(appStateObservable.startWith(appState))
    }

    lateinit var mainLayout: MotionLayout
    lateinit var songsRecycler: RecyclerView
    lateinit var playerLayout: ViewGroup
    lateinit var adapter: SongsAdapter
    lateinit var snapHelper: PagerSnapHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.layout_main)
        songsRecycler = findViewById(R.id.recycler_songs)
        playerLayout = findViewById(R.id.layout_player)

        songsRecycler.layoutManager =
            LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }

        adapter = SongsAdapter()

        songsRecycler.adapter = adapter

        adapter.refreshData(appState.songs)

        snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(songsRecycler)


        songsRecycler.setOnTouchListener { view, motionEvent ->
            if (mainLayout.onTouchEvent(motionEvent).not()) {
                songsRecycler.onTouchEvent(motionEvent)
            } else {
                songsRecycler.onTouchEvent(motionEvent)
            }
        }


        mainLayout.setTransitionListener(object : MotionLayout.TransitionListener {
          override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

          }

          override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

          }

          override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            if (p1 == R.id.expanded) {
              appState = appState.copy(expandedPercent = 1f - p3)
            } else {
              appState = appState.copy(expandedPercent = p3)
            }

            emitNewAppState()
            adapter.expandedPercent = appState.expandedPercent

            updateAllRecyclerChildren()
          }

          override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

          }

        })

        songsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            updateAllRecyclerChildren()
          }
        })
    }

    fun updateAllRecyclerChildren() {
        for (i in appState.songs.indices) {
            val childView = songsRecycler.getChildAt(i)
            if (childView != null) {
                val songViewHolder =
                    songsRecycler.getChildViewHolder(childView) as? SongsAdapter.SongViewHolder
                songViewHolder?.setExpandPercent(appState.expandedPercent)
            }
        }
    }

    fun emitNewAppState() {
        appStateObservable.onNext(appState)
    }

    class SongsAdapter : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

        val data = arrayListOf<Song>()
        var expandedPercent: Float = 0f

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
            return SongViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            holder.bind(data[position], expandedPercent)
        }

        fun refreshData(data: List<Song>) {
            this.data.clear()
            this.data.addAll(data)
        }


        class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var songImageView: ImageView? = itemView.findViewById(R.id.iv_cover_art)
            var songTitleView: TextView? = itemView.findViewById(R.id.tv_song_title)
            var rootView: MotionLayout? = itemView.findViewById(R.id.root_view)

            fun bind(song: Song, expandedPercent: Float) {
                songImageView?.setImageResource(song.imageRes)
                songTitleView?.text = song.title
                setExpandPercent(expandedPercent)
            }

            fun setExpandPercent(percent: Float) {
                rootView?.setInterpolatedProgress(percent)
            }

        }


    }

}
