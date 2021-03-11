package com.app.video;

import android.view.Surface;

import com.app.tools.H264VideoDecoder;

/**
 * Created by han.chen.
 * Date on 2021/2/24.
 **/
public class VideoPlayThread extends Thread {
    public VideoPlayThread(Surface surface) {
        H264VideoDecoder.getInstance().initDecoder(surface);
    }

    private boolean running = true;
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (running) {
            try {
                VideoNalBuffer.NalBuffer nalBuffer = RTPVideoReceiveImp.mVideoNalBuffer.pollData();
                if (nalBuffer != null && nalBuffer.nal != null && nalBuffer.nal.length > 0) {
                    H264VideoDecoder.getInstance().onFrame(nalBuffer.nal, 0, nalBuffer.nal.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startThread() {
        running = true;
        super.start();
    }

    public void stopThread() {
        running = false;
        H264VideoDecoder.getInstance().stopCodec();
    }

}
