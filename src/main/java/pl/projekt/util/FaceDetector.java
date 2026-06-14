package pl.projekt.util;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.io.*;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FaceDetector{

    private CascadeClassifier cascade;
    private Mat grayFrame;

    /**
     * Constructor loading CascadeClassifier, creates temporary
     * file to store frontal face data from .jar file.
     */
    public FaceDetector(){
        cascade = new CascadeClassifier();
        grayFrame = new Mat();


        try (InputStream is = getClass().getResourceAsStream("/haarcascade_frontalface_default.xml")){
            if (is == null){
                System.err.println("Error: Haarcascade file not exists");
                return;
            }

            File tempCascadeFile = File.createTempFile("haarcascade", ".xml");
            tempCascadeFile.deleteOnExit();

            Files.copy(is, tempCascadeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            cascade.load(tempCascadeFile.getAbsolutePath());
            if (cascade.empty()) {
                System.out.println("Error: Cascade not loaded");
            }

        } catch (IOException e){
            System.err.println("Error: Could not create temp file for cascade, " + e.getMessage());
        }
    }

    /**
     * Function that draws rectangles on frame
     *
     * @param frame Captured frame
     * @param faces Positions of the faces
     */
    public void drawFaces(Mat frame, Rect[] faces){
        for (var face : faces){
            rectangle(frame, face, new Scalar(102, 255, 178, 0), 3, LINE_8, 0);
        }
    }

    /**
     * Function that draws rectangles on frame with corresponding labels.
     *
     * @param frame Captured frame
     * @param faces Positions of the faces
     * @param labels Faces ids
     */
    public void drawFaces(Mat frame, Rect[] faces, String[] labels){
        for (int i=0; i<faces.length; i++){
            Rect face = faces[i];
            String label = labels[i];
            rectangle(frame, face, new Scalar(102, 255, 178, 0), 3, LINE_8, 0);
            putText(frame, label, new Point(face.x(), Math.max(face.y() - 10, 0)), 1, 1.0, new Scalar(102, 255, 178, 0));
        }
    }


    /**
     * Detecting faces from provided frame.
     * Function converts frame to grayscale and invokes
     * CascadeClassifier's class detectMultiScale method to get positions
     * of faces (x, y, width and length).
     *
     * @param frame Captured frame
     * @return Rect array representing positions of detected faces
     */
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

    /**
     * Clean-up function
     */
    public void clean() {
        if (grayFrame != null) grayFrame.close();
        if (cascade != null) cascade.close();
    }
}