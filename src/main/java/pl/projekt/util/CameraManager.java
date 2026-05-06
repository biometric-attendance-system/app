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
    private boolean cameraActive;

    static {
        try {
            nu.pattern.OpenCV.loadShared();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Błąd OpenCV: " + e.getMessage());
        }
    }

    public CameraManager() {
        this.capture = new VideoCapture();
        this.cameraActive = false;
    }

    public boolean openCamera(Consumer<Image> onFrameCaptured) {
        if(!cameraActive){
            capture.open(0);
            if(capture.isOpened()){
                cameraActive = true;

                Runnable frameGrabber = () -> {
                    Mat frame = new Mat();
                    if(capture.read(frame)){
                        //algorytmy rozpoznawania twarzy
                        Image imageToShow = matToImage(frame);
                        onFrameCaptured.accept(imageToShow);
                    }
                };

                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0 , 33, TimeUnit.MILLISECONDS);
                return true;
            }
        }
        return false;
    }

    public boolean isCameraActive() {
        return cameraActive;
    }

    public void closeCamera() {
        if(cameraActive){
            if(timer != null && !timer.isShutdown()){
                try{
                    timer.shutdown();
                    timer.awaitTermination(33, TimeUnit.MILLISECONDS);
                } catch(InterruptedException e){
                    System.err.println("Błąd zatrzymania wątku: " + e.getMessage());
                }
            }
            if (capture.isOpened())
                capture.release();
            cameraActive = false;
        }
    }

    public Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}