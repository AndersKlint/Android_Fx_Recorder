package com.example.anders.wellactually;

import android.media.PlaybackParams;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.PlaybackParameters;

public class XyPad {
    private ImageView xySeeker;
    private PlaybackParameters params;


    public XyPad(ImageView xySeeker ) {
        this.xySeeker = xySeeker;
        this.params = SoundMixer.getParams();
    }

    public void onClick(View view) {
        params = SoundMixer.getParams();
       view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float newSpeed = 1;
                float newPitch = 1;
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
                float xScale = x / v.getWidth();
                float yScale = y / v.getHeight();
                xySeeker.setX(x - seekerHalfWidth);  // to fix origin to center
                xySeeker.setY(y - seekerHalfHeight);
                boolean updatedParams = false;
                if (xScale > 0.05 && xScale < 0.95) { // safe param limits for rounding
                    newSpeed = xScale * 2;
                    updatedParams = true;
                }
                if (yScale > 0.05 && yScale < 0.95) {
                   newPitch = (1 - yScale) * 2;
                    updatedParams = true;
                }
                if(updatedParams)
                    SoundMixer.updateParams(new PlaybackParameters(newSpeed,newPitch));
                return true;
            }
        });
    }

    public void resetXyPad() {
        xySeeker.setX(((View) xySeeker.getParent()).getWidth()/2 - xySeeker.getWidth()/2);
        xySeeker.setY(((View) xySeeker.getParent()).getHeight()/2 - xySeeker.getHeight()/2);
        this.params = SoundMixer.getParams();
        SoundMixer.updateParams(new PlaybackParameters(1,1));
    }
}