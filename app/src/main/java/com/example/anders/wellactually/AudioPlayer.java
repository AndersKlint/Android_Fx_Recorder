package com.example.anders.wellactually;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
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
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Anders on 21/01/2018.
 */

public class AudioPlayer {

    private Context context;
    private SimpleExoPlayer player;
    private boolean isInitialized;
    private Uri filePath;
    private float bpmFactor;

    public AudioPlayer(Context context) {
        this.context = context;
        isInitialized = false;

        player =
                ExoPlayerFactory.newSimpleInstance(
                        context, new DefaultTrackSelector());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Well Actually"), null);
    }

    public void init(Uri filePath) {
        this.filePath = filePath;
        bpmFactor = 1;
        player =
                ExoPlayerFactory.newSimpleInstance(
                        context, new DefaultTrackSelector());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Well Actually"), null);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(filePath);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare(mediaSource);
        isInitialized = true;
        setPlaybackParams(1, 1);
    }

    public void initWithSample(Uri filePath, float oldBpm, float newBpm) {
        init(filePath); // order important, init first
        bpmFactor = BpmConverter.getNewSpeedPercentage(oldBpm, newBpm);
        setPlaybackParams(1, 1); //only update them with new bpmfactor here
    }

    public void release() {
        player.release();
        isInitialized = false;
    }

    public void setPlaybackParams(float pitch, float speed) {
        player.setPlaybackParameters(new PlaybackParameters(speed*bpmFactor, pitch));
    }


    public void setPitch(float pitch) {
        float oldSpeed = player.getPlaybackParameters().speed;
        player.setPlaybackParameters(new PlaybackParameters(oldSpeed, pitch));
    }

    public void setSpeed(float speed) {
        float oldPitch = player.getPlaybackParameters().pitch;
        player.setPlaybackParameters(new PlaybackParameters(speed*bpmFactor, oldPitch));
    }

    public boolean togglePlay() {
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

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public Uri getUri() {
        return filePath;
    }

    public void setVolume(float volume) {
        player.setVolume(volume);
    }
}
