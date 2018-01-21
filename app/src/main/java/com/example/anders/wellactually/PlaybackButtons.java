package com.example.anders.wellactually;

import android.widget.Button;

/**
 * Created by Anders on 21/01/2018.
 */

class PlaybackButtons {
    private Button play;
    private Button record;

    public PlaybackButtons (Button play, Button record) {
        this.play = play;
        this.record = record;
    }
    public void updateState(int soundMixerState) {
        switch (soundMixerState) {
            case (SoundMixer.STATE_IDLE):
                play.setText("Play");
                play.setEnabled(true);
                record.setText("Record");
                break;
            case (SoundMixer.STATE_NO_PLAYBACK_FILE):
                play.setEnabled(false);
                play.setText("Play");
                record.setText("Record");
                break;
            case (SoundMixer.STATE_PLAYING):
                play.setEnabled(true);
                play.setText("Stop");
                record.setText("Record");
                break;
            case (SoundMixer.STATE_RECORDING):
                play.setEnabled(false);
                record.setText("Stop");
                break;
            default:
                play.setEnabled(false);
                play.setText("Play");
                record.setText("Record");
                break;
        }

    }
}
