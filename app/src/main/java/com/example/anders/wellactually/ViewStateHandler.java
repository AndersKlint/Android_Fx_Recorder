package com.example.anders.wellactually;

import android.support.design.widget.TabLayout;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Anders on 21/01/2018.
 */

class ViewStateHandler implements OnStateChangedListener {
    private Button play;
    private Button record;
    private TabLayout tabLayout;


    public ViewStateHandler(Button play, Button record, TabLayout tabLayout) {
        this.play = play;
        this.record = record;
        this.tabLayout = tabLayout;
    }


    private void updateState(int soundMixerState) {
        switch (soundMixerState) {
            case (SoundMixer.STATE_READY_TO_PLAY):
                setEnableTrackTabs(true);
                play.setText("Play");
                play.setEnabled(true);
                record.setText("Record");
                break;
            case (SoundMixer.STATE_NO_PLAYBACK_FILE):
                setEnableTrackTabs(true);
                play.setEnabled(false);
                play.setText("Play");
                record.setText("Record");
                break;
            case (SoundMixer.STATE_PLAYING):
                setEnableTrackTabs(true);
                play.setEnabled(true);
                play.setText("Stop");
                record.setText("Record");
                break;
            case (SoundMixer.STATE_RECORDING):
                setEnableTrackTabs(false);
                record.setEnabled(true);
                play.setEnabled(false);
                record.setText("Stop");
                break;
            case (SoundMixer.STATE_METRONOME_PLAYING):
                setEnableTrackTabs(false);
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

    private void setEnableTrackTabs(boolean b) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        if (b) {
            tabStrip.setEnabled(true);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(true);
            }
        } else {
            tabStrip.setEnabled(false);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(false);
            }
        }
    }

    @Override
    public void stateChanged(int state) {
        updateState(state);
    }
}
