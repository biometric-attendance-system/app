package pl.projekt.util;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_videoio.*;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CameraManager {

    private VideoCapture capture;
    private ScheduledExecutorService timer;
    private volatile boolean cameraActive;
    private FaceDetector faceDetector;

    static {
        try {
            Loader.load(org.bytedeco.opencv.opencv_java.class);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Error loading OpenCV: " + e.getMessage());
        }
    }

    public CameraManager() {
        this.capture = new VideoCapture();
        this.faceDetector = new FaceDetector();
        this.cameraActive = false;
    }

    public boolean openCamera(Consumer<Mat> onFrameCaptured) {
        if(!cameraActive){
            capture.open(0);
            capture.set(org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH, 640);
            capture.set(org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT, 480);
            
            if(capture.isOpened()){
                cameraActive = true;

                final Mat frame = new Mat();

                Runnable frameGrabber = () -> {
                    if (!cameraActive || !capture.isOpened()) return;

                    if(capture.read(frame) && !frame.empty()){
                        onFrameCaptured.accept(frame);
                    }
                };

                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0 , 33L, TimeUnit.MILLISECONDS);
                return true;
            }
        }
        return false;
    }

    public Image convertMatToImage(Mat frame) {
        BytePointer buffer = new BytePointer();
        imencode(".bmp", frame, buffer);
            
        byte[] bytes = new byte[(int)buffer.limit()];
        buffer.get(bytes);
        buffer.close();
            
        return new Image(new ByteArrayInputStream(bytes));
    }

    public boolean isCameraActive() {
        return cameraActive;
    }

    public void closeCamera() {
        if(cameraActive){
            cameraActive = false;

            if(timer != null && !timer.isShutdown()){
                try{
                    timer.shutdown();
                    if (!timer.awaitTermination(300, TimeUnit.MILLISECONDS)){
                        timer.shutdownNow();
                    }

                } catch(InterruptedException e){
                    System.err.println("Thread error: " + e.getMessage());
                }
            }
            if (capture.isOpened()) capture.release();
            faceDetector.clean();
        }
    }

}