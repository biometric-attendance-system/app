package pl.projekt.util;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;  
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

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
            rectangle(frame, face, new Scalar(102, 255, 178, 0), 3, LINE_8, 0);
        }
    }


    public Rect[] getRectFaces(Mat frame){    
        cvtColor(frame, grayFrame, COLOR_BGR2GRAY);
        equalizeHist(grayFrame, grayFrame);

        int minSize = Math.round(frame.rows() * 0.1f); 
        RectVector faces = new RectVector();

        cascade.detectMultiScale(grayFrame, faces, 1.2, 3, 0, 
            new Size(minSize, minSize), new Size());
        
        Rect[] rectFaces = new Rect[(int)faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            rectFaces[i] = faces.get(i);
        }
        
        faces.close();  
        return rectFaces;
    }
    
    public void clean() {
        if (grayFrame != null) grayFrame.close();
        if (cascade != null) cascade.close();
    }
}