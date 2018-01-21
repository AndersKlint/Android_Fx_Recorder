package com.example.anders.wellactually;

import android.os.Handler;
import android.widget.ProgressBar;



/**
 * Created by Anders on 21/01/2018.
 */

public class AudioProgressBar {
    private static ProgressBar BAR;
    private static Handler HANDLER;
    private static AudioPlayer PLAYER;

    public static void init(ProgressBar bar) {
        BAR = bar;
        HANDLER = new Handler();
    }
    public static void setPlayer(AudioPlayer player) {
        PLAYER = player;
    }
    public static void setEnable(boolean on){
        if (on)
            HANDLER.postDelayed(updateProgressBar,40);
        else
            HANDLER.removeCallbacks(updateProgressBar);

    }

    private static Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
                BAR.setProgress((int) (100*((float) PLAYER.getCurrentPosition() / (float) PLAYER.getDuration())));
            if (PLAYER.getDuration() < 5000)
                HANDLER.postDelayed(this, 40);  // 40 ms = 60+ fps
            else
                HANDLER.postDelayed(this, 1000);
        }
    };

    public static void resetPosition() {
        BAR.setProgress(0);
    }
}
