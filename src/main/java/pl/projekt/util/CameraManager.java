package pl.projekt.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

public class CameraManager {
    private VideoCapture capture;

    public CameraManager() {
        this.capture = new VideoCapture();
    }

    public boolean open(int index) {
        return capture.open(index);
    }

    public Mat getFrame() {
        Mat frame = new Mat();
        capture.read(frame);
        return frame;
    }

    public void closeCamera() {
        if (capture.isOpened()) {
            capture.release();
        }
    }
    public Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}