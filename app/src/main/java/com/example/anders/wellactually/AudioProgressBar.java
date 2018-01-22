package com.example.anders.wellactually;

import android.os.Handler;
import android.widget.ProgressBar;



/**
 * Created by Anders on 21/01/2018.
 */

public class AudioProgressBar {
    private  ProgressBar bar;
    private  Handler handler;
    private  AudioPlayer player;

    public AudioProgressBar(ProgressBar bar) {
        this.bar = bar;
        handler = new Handler();
    }
    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }
    public void setEnable(boolean on){
        if (on)
            handler.postDelayed(updateProgressBar,40);
        else
            handler.removeCallbacks(updateProgressBar);

    }

    private Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
                bar.setProgress((int) (100*((float) player.getCurrentPosition() / (float) player.getDuration())));
            if (player.getDuration() < 5000)
                handler.postDelayed(this, 40);  // 40 ms = 60+ fps
            else
                handler.postDelayed(this, 1000);
        }
    };

    public void resetPosition() {
        bar.setProgress(0);
    }
}
