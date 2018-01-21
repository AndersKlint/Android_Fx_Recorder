package com.example.anders.wellactually;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Anders on 21/01/2018.
 */

public class AudioRecorder {
    MediaRecorder recorder;
    private boolean isRecording;

    public AudioRecorder( ) {
        isRecording = false;
        recorder = new MediaRecorder();
    }

    public boolean toggleRecord(int duration, String recordingPath) {
        if (!isRecording) {
            startRecording(duration, recordingPath);
            return true;
        }
        else {
            stopRecording();
            return false;
        }
    }

    private void startRecording(int duration, String recordingPath) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordingPath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            if (duration > 0) {
                recorder.setMaxDuration(duration);
                recorder.setOnInfoListener(recordingStoppedListener);
            }
            recorder.prepare();
            isRecording = true;
            recorder.start();
        } catch (IOException e) {
            Log.e("Audio recording:", "prepare() failed");
        }
    }

    private void stopRecording() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private MediaRecorder.OnInfoListener recordingStoppedListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                SoundMixer.toggleRecord(); // is this bad? Probably
        }
    };

}

