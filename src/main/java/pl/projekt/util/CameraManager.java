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
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @brief Class responsible for camera management: opening/closing a camera, image conversion.
 */

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

    /**
     * @brief Constructor initializing CameraManager variables.
     */
    public CameraManager() {
        this.capture = new VideoCapture();
        this.faceDetector = new FaceDetector();
        this.cameraActive = false;
    }

    /**
     * Function counts available cameras.
     *
     * @return Number of available cameras.
     */
    public int countCameras(){
        int counter = 0;

        for(; counter<5; counter++){
            if (!capture.open(counter, org.bytedeco.opencv.global.opencv_videoio.CAP_DSHOW)) {
                break;
            }
            capture.release();
        }

        return counter;
    }

    /**
     * @brief Opens camera in capped resolution at about 30 FPS. Every frame is being sent to Consumer class.
     *
     * @param onFrameCaptured Consumers that performs further actions on taken Mat frame.
     * @return True if camera opened false otherwise.
     */
    public boolean openCamera(int idx, Consumer<Mat> onFrameCaptured) {
        if(!cameraActive){
            capture.open(idx , org.bytedeco.opencv.global.opencv_videoio.CAP_DSHOW);
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

    /**
     * @brief Helper function converting OpenCV Mat class to JavaFX Image class
     *
     * @param frame Frame captured.
     * @return Converted Image class.
     */
    public Image convertMatToImage(Mat frame) {
        BytePointer buffer = new BytePointer();
        imencode(".bmp", frame, buffer);
            
        byte[] bytes = new byte[(int)buffer.limit()];
        buffer.get(bytes);
        buffer.close();

        return new Image(new ByteArrayInputStream(bytes));
    }

    /**
     * @brief Returns whether camera is active.
     *
     * @return True if camera is opened, false otherwise.
     */
    public boolean isCameraActive() {
        return cameraActive;
    }

    /**
     * @brief Function responsible for closing camera safely.
     */
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