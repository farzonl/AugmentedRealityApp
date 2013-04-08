package Comparch.augmentedreality;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import Comparch.augmentedreality.androidSpec.FrameView;
import Comparch.augmentedreality.effects.Effect;
import android.util.Log;

public class ImageProcessorTask implements Runnable {
        private static final String TAG = "ImageProcessor";

        VideoCapture capture;
        FrameView view;
        Mat frame;
        Effect effect;
        boolean running;

        /* TODO We need some way to have a possibility of many frames with their own 
         * effects. This will do fine for signle efffects.*/
        public ImageProcessorTask(String pipeline, FrameView view) {
                this.view = view;
                frame = new Mat();
                effect = null;
                running = true;
        }

        public void setEffect(Effect effect) {
                synchronized (this) {
                        this.effect = effect;
                }
        }

        public void setRunning(boolean running) {
                this.running = running;
        }

        @Override
        public void run() {
                Log.i(TAG, "Image Processing Thread Started");
                openCamera();
                setupCamera(view.getWidth(), view.getHeight());

                while (running) {
                        if (capture == null) {
                                Log.e(TAG, "capture is not open");
                                break;
                        }

                        if (!capture.grab()) {
                                Log.e(TAG, "capture.grab() failed");
                                break;
                        }

                        capture.retrieve(frame, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);

                        synchronized (this) {
                                // TODO Pipelining stuff goes here. Just a single effect for now.
                                if (effect != null)
                                        frame = effect.applyTo(frame);
                        }

                        view.setFrame(frame);
                }
                running = false;

                releaseCamera();
                Log.i(TAG, "Image Processing Thread Finished");
        }

        public boolean openCamera() {
                Log.i(TAG, "openCamera");
                releaseCamera();
                capture = new VideoCapture(Highgui.CV_CAP_ANDROID);
                if (!capture.isOpened()) {
                        capture.release();
                        capture = null;
                        Log.e(TAG, "Failed to open native camera");
                        return false;
                }
                return true;
        }

        public void releaseCamera() {
                Log.i(TAG, "releaseCamera");
                if (capture != null) {
                        capture.release();
                        capture = null;
                }
        }

        public void setupCamera(int width, int height) {
                Log.i(TAG, "setupCamera(" + width + ", " + height + ")");
                if (capture != null && capture.isOpened()) {
                        List<Size> sizes = capture.getSupportedPreviewSizes();
                        int mFrameWidth = width;
                        int mFrameHeight = height;

                        double minDiff = Double.MAX_VALUE;
                        for (Size size : sizes) {
                                Log.i(TAG, "Supports " + size.width + " x " + size.height);
                                if (Math.abs(size.height - height) < minDiff) {
                                        mFrameWidth = (int) size.width;
                                        mFrameHeight = (int) size.height;
                                        minDiff = Math.abs(size.height - height);
                                }
                        }

                        Log.i(TAG, "Chose " + mFrameWidth + " x " + mFrameHeight);
                        capture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
                        capture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
                }
        }
}