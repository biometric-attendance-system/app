package pl.projekt.util;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.lang.Math;


public class FaceDetector{

    private CascadeClassifier cascade;
    private Mat grayFrame;

    public FaceDetector(){
        cascade = new CascadeClassifier();
        cascade.load("src/main/resources/haarcascade_frontalface_default.xml");
        if (cascade.empty()) {
            System.out.println("Error: cascade not loaded");
        }
        grayFrame = new Mat();
    }

    public void addAttendanceFromFrame(){
        
    }

    public void drawFaces(Mat frame, Rect[] faces){
        for (var face : faces){
            Imgproc.rectangle(frame, face, new Scalar(102,255,178), 3);
        }
    }

    public Rect[] getRectFaces(Mat frame){    
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        int minSize = Math.round(frame.rows() * 0.1f); 
        MatOfRect faces = new MatOfRect();

        cascade.detectMultiScale(grayFrame, faces, 1.2, 3, 0, new Size(minSize, minSize), new Size() );
        
        Rect[] rectFaces = faces.toArray();
        faces.release();

        return rectFaces;
    }
    
    public void clean() {
        if (grayFrame != null) grayFrame.release();
    }
}