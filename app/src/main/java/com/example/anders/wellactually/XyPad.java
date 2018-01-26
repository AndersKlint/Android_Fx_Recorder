package com.example.anders.wellactually;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class XyPad {
    private ImageView xySeeker;
    private SoundMixer soundMixer;


    public XyPad(ImageView xySeeker, View trackPad, SoundMixer soundMixer) {
        this.soundMixer = soundMixer;
        this.xySeeker = xySeeker;
        onTouched(trackPad);

    }

    private void onTouched(View trackPad) {
        trackPad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                float seekerHalfWidth = xySeeker.getWidth() / 2;
                float seekerHalfHeight = xySeeker.getHeight() / 2;
                if (x <= 0)  // handles out of bounds
                    x = seekerHalfWidth;
                else if (x > v.getWidth() - seekerHalfWidth)
                    x = v.getWidth() - seekerHalfWidth;
                if (y <= 0)
                    y = seekerHalfHeight;
                else if (y > v.getHeight() - seekerHalfHeight)
                    y = v.getHeight() - seekerHalfHeight;
                float xScale = x / ((float) v.getWidth());
                float yScale = y / ((float) v.getHeight());
                xySeeker.setX(x - seekerHalfWidth);  // to fix origin to center
                xySeeker.setY(y - seekerHalfHeight);
                soundMixer.setCurrentPlaybackParams((1 - yScale) * 2,xScale * 2);
                return true;
            }
        });
    }


    public void resetXyPad() {
        xySeeker.setX(((View) xySeeker.getParent()).getWidth() / 2 - xySeeker.getWidth() / 2);
        xySeeker.setY(((View) xySeeker.getParent()).getHeight() / 2 - xySeeker.getHeight() / 2);
        soundMixer.setCurrentPlaybackParams(1,1);
    }
}