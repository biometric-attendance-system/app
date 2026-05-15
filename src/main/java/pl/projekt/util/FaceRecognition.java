package pl.projekt.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.IntBuffer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.DoublePointer;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector; 
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_face.*;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;

public class FaceRecognition{
    private final String APP_DIR_PATH = System.getProperty("user.home") + File.separator + ".FaceRecognitionApp";
    private final String MODEL_FILE_PATH = APP_DIR_PATH + File.separator + "trained_model.yml";


    private FaceRecognizer faceRecognizer;
    private Path tempDir;
    private String albumNumber;
    private int count;
    private double CONFIDENCE = 40.0;

    private File getTrainFile(){
        File trainDir = new File(APP_DIR_PATH);
        if (!trainDir.exists()){
            trainDir.mkdirs();
        } 
        return new File(MODEL_FILE_PATH);
    }

    public boolean loadRecognizer(){
        faceRecognizer = LBPHFaceRecognizer.create();
        File trainFile = getTrainFile();
    
        if (trainFile.exists()) {
            faceRecognizer.read(trainFile.getAbsolutePath());
        } else {
            return false;
        }
        return true;
    }

    public void closeRecognizer(){
        faceRecognizer.close();
    }

    public String[] recognize(Mat faces, Rect[] pos){
        String[] temp = new String[pos.length];

        try(IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            ){
            int i = 0;
            for (Rect curr : pos){

                int x = Math.max(0, curr.x());
                int y = Math.max(0, curr.y());
                int width = Math.min(faces.cols() - x, curr.width());
                int height = Math.min(faces.rows() - y, curr.height());

                if (width <= 0 || height <= 0) {
                    temp[i++] = "Unknown"; 
                    continue;
                }

                Rect safeRect = new Rect(x, y, width, height);

                Mat cropped = new Mat(faces,safeRect);
                Mat grayCropped = new Mat();
                cvtColor(cropped, grayCropped, COLOR_BGR2GRAY);
                equalizeHist(grayCropped, grayCropped);
                
                faceRecognizer.predict(grayCropped, label, confidence);

                if (confidence.get(0) <= CONFIDENCE){
                    temp[i] = String.valueOf(label.get(0));
                } else {
                    temp[i] = "Unknown";
                }

                grayCropped.close();
                cropped.close();
                i++;
            }
        }
        return temp;
    }

    public boolean createDir(String albumNumber){
        try{
            tempDir = Files.createTempDirectory("FaceRecognitionApp-" + albumNumber);
            count = 0;
            this.albumNumber = albumNumber;
            return true;
        } catch (Exception e){
            System.err.println("Error: could not open a temp folder");
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteDir(){
        if (tempDir==null) return false;
        File dir = tempDir.toFile();
        if (dir.exists()){
            File[] files = dir.listFiles();

            if (files != null){
                for (File file : files){
                    file.delete();
                }
            }
            dir.delete();
        }
        return true;
    }

    public int getPhotoCount(){
        return count;
    }

    public void saveFace(Mat frame, Rect[] faces){
        if (faces.length != 1 || tempDir == null) return;

        Mat cropped = new Mat(frame, faces[0]);
        Mat grayCropped = new Mat();

        cvtColor(cropped, grayCropped, COLOR_BGR2GRAY);
        equalizeHist(grayCropped, grayCropped);
        
        String filePath = tempDir.toAbsolutePath().toString() + 
                        File.separator + albumNumber + "-" + count + ".png";

        imwrite(filePath, grayCropped); 

        count++;
        cropped.close();      
        grayCropped.close();
    } 

    public boolean trainFace(){
        if (count < 60) return false;
        if (tempDir == null) return false;

        File[] files = tempDir.toFile().listFiles();
        if (files == null) return false;

        MatVector images = new MatVector(files.length);
        
        Mat labelsMat = new Mat(files.length, 1, CV_32SC1);
        IntBuffer labelsBuffer = labelsMat.createBuffer();

        int idx = 0;
        for (File file : files){
            Mat img = imread(file.getAbsolutePath(), IMREAD_GRAYSCALE);
            images.put(idx, img); 
            labelsBuffer.put(idx, Integer.parseInt(albumNumber));
            idx++;
        }

        loadRecognizer();

        try{
            File trainFile = getTrainFile();
            if (trainFile.exists()){
                faceRecognizer.update(images, labelsMat);  
            } else {
                faceRecognizer.train(images, labelsMat);
            }
            faceRecognizer.write(trainFile.getAbsolutePath());
            
            return true;

        } catch (Exception e){
            System.err.println("Error during training: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            if (labelsMat != null) labelsMat.close();
            if (faceRecognizer != null) closeRecognizer();
            
            this.deleteDir();

            for (int i = 0; i < images.size(); i++){
                Mat img = images.get(i);
                if (img != null && !img.isNull()) {
                    img.close();
                }
            }
            images.close();
        }
    }
}