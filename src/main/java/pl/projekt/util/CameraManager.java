package pl.projekt.util;



import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;


import java.io.ByteArrayInputStream;
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
            nu.pattern.OpenCV.loadShared();
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
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".bmp", frame, buffer);
        Image img = new Image(new ByteArrayInputStream(buffer.toArray()));
        buffer.release();
        return img;
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
                    if (!timer.awaitTermination(200, TimeUnit.MILLISECONDS)){
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