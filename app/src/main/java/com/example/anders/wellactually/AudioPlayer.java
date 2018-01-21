package com.example.anders.wellactually;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Anders on 21/01/2018.
 */

public class AudioPlayer {

    private Context context;
    private String filePath;
    private SimpleExoPlayer player;
    private PlaybackParameters params;
    private boolean isInitialized;

    public AudioPlayer(Context context, String filePath ) {
        this.context = context;
        this.filePath = filePath;
        isInitialized = false;

        player =
                ExoPlayerFactory.newSimpleInstance(
                        context, new DefaultTrackSelector());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Well Actually"), null);
    }

    public void init(){
        player =
                ExoPlayerFactory.newSimpleInstance(
                        context, new DefaultTrackSelector());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Well Actually"), null);
        MediaSource  mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(filePath));
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare(mediaSource);
        params = player.getPlaybackParameters();
        isInitialized = true;
    }

    public void release(){
        player.release();
        isInitialized = false;
    }


    public void setPitch(float pitch) {
        float oldSpeed = params.speed;
        params = new PlaybackParameters(oldSpeed, pitch);
    }

    public void setSpeed(float speed) {
        float oldPitch = params.pitch;
        params = new PlaybackParameters(speed, oldPitch);
    }

    public boolean togglePlay() {
        if(player == null)
            init();
        if (!player.getPlayWhenReady()) {
            player.setPlayWhenReady(true);
            return true;
        }
        player.setPlayWhenReady(false);
        player.seekTo(0);
        return false;
    }

    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }

    public long getDuration() {
        return player.getDuration();
    }

    public long getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public boolean isInitialized(){
        return isInitialized;
    }

    public String getReadPath() {
        return filePath;
    }
}
