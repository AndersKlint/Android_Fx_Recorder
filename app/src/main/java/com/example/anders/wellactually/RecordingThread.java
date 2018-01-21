package com.example.anders.wellactually;


/**
 * Created by Anders on 21/01/2018.
 */

public class RecordingThread implements Runnable {
    AudioRecorder recorder;
    public RecordingThread(AudioRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

        }
    }
}
