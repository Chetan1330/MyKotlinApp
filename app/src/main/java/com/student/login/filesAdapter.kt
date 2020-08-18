package com.student.login

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.firebase.storage.StorageReference


class filesAdapter(val mCtx : Context, val layoutId:Int, val studentList:List<Datalist>)
    :ArrayAdapter<Datalist>(mCtx,layoutId,studentList){

    private val mContext: Context? = null
    lateinit var mStorage : StorageReference
    var exoPlayer: SimpleExoPlayer? = null
    //var playerView: PlayerView? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view:View = layoutInflater.inflate(layoutId,null)

        val filename = view.findViewById<TextView>(R.id.filename)

        val downBtn = view.findViewById<Button>(R.id.down)

        val playerView = view.findViewById<PlayerView>(R.id.ep_video_view);
        playerView.visibility = View.INVISIBLE

        val image1 = view.findViewById<ImageView>(R.id.image1)

        val employee = studentList[position]

        filename.text = employee.name
        val uri = Uri.parse(employee.fileUrl)
        Glide.with(context).load(uri).into(image1)

        val handler = Handler()
        val handler1 = Handler()
        handler.postDelayed({
            if (image1.drawable != null){
                playerView.visibility = View.INVISIBLE
            }
            if (playerView.player.bufferedPercentage <=1 ){
                playerView.visibility = View.INVISIBLE
            }

        }, 1000)

        handler1.postDelayed({
            if (playerView.player.isCurrentWindowSeekable){
                playerView.visibility = View.VISIBLE
            }

        }, 5000)

        try {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelector: TrackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context,trackSelector) as SimpleExoPlayer
            val video = Uri.parse(employee.fileUrl)
            val dataSourceFactory = DefaultHttpDataSourceFactory("video")
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
            val mediaSource: MediaSource = ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null)
            playerView.setPlayer(exoPlayer)
            exoPlayer!!.prepare(mediaSource)
            exoPlayer!!.playWhenReady = false
        } catch (e: Exception) {
            playerView.visibility = View.INVISIBLE
            Log.e("ViewHolder2", "exoplayer error$e")
        }

        //if (playerView!!.tag == null && image1.tag == null ){
            //playerView!!.visibility = View.INVISIBLE
           // image1.visibility = View.INVISIBLE
       // }


        downBtn.setOnClickListener {
            val url = employee.fileUrl

            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Download")
            request.setDescription("File is Downloading...")
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"${System.currentTimeMillis()}")

            val manager = layoutInflater.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }

        return view
    }

}