package com.example.anders.wellactually;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by Anders on 21/01/2018.
 */

class PlaybackButtons implements OnStateChangedListener {
    private Button play;
    private Button record;


    public PlaybackButtons(Button play, Button record) {
        this.play = play;
        this.record = record;
    }


    private void updateState(int soundMixerState) {
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
                record.setEnabled(true);
                play.setEnabled(false);
                record.setText("Stop");
                break;
            case (SoundMixer.STATE_METRONOME_PLAYING):
                record.setEnabled(false);
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

    @Override
    public void stateChanged(int state) {
        updateState(state);

    }
}
