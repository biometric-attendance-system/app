package pl.projekt.performance;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FramePerformanceTest {

    static {
        try {
            Loader.load(org.bytedeco.opencv.opencv_java.class);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Error loading OpenCV: " + e.getMessage());
        }
    }

    private FaceDetector faceDetector;
    private FaceRecognition faceRecognition;
    private Mat[] testFrames;

    @BeforeEach
    public void setUp() {
        faceDetector = new FaceDetector();
        faceRecognition = new FaceRecognition();
        testFrames = new Mat[3];

        testFrames[0] = imread("src/test/resources/test_frame1face.jpg");
        testFrames[1] = imread("src/test/resources/test_frame3faces.jpg");
        testFrames[2] = imread("src/test/resources/test_frame6faces.jpg");

        Rect[] faces = faceDetector.getRectFaces(testFrames[0]);
        faceRecognition.recognize(testFrames[0], faces);
        System.out.println("FRAME PERFORMANCE TEST - number of detected faces: " + faces.length);
    }

    @AfterEach
    public void tearDown() {
        for (Mat testFrame : testFrames) {
            if (testFrame != null && !testFrame.isNull()) {
                testFrame.close();
            }
        }
        if (faceRecognition != null) {
            faceRecognition.closeRecognizer();
        }
    }

    @Test
    public void averageTimeOfDetectingFacesLessThan120ms_NumOfFaces1() {
        int framesToTest = 100;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < framesToTest; i++) {
            Rect[] faces = faceDetector.getRectFaces(testFrames[0]);
            if (faces.length > 0) {
                faceRecognition.recognize(testFrames[0], faces);
            }

            for (Rect face : faces) {
                if (face != null)
                    face.close();
            }

        }

        long endTime = System.currentTimeMillis();
        long averageTime = (endTime - startTime) / framesToTest;

        System.out.println("FRAME PERFORMANCE TEST - average time equals: " + averageTime);

        assertTrue(averageTime < 120);
    }

    @Test
    public void averageTimeOfDetectingFacesLessThan120ms_NumOfFaces3() {
        int framesToTest = 100;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < framesToTest; i++) {
            Rect[] faces = faceDetector.getRectFaces(testFrames[1]);
            if (faces.length > 0) {
                faceRecognition.recognize(testFrames[1], faces);
            }

            for (Rect face : faces) {
                if (face != null)
                    face.close();
            }

        }

        long endTime = System.currentTimeMillis();
        long averageTime = (endTime - startTime) / framesToTest;

        System.out.println("FRAME PERFORMANCE TEST - average time equals: " + averageTime);

        assertTrue(averageTime < 120);
    }

    @Test
    public void averageTimeOfDetectingFacesLessThan120ms_NumOfFaces6() {
        int framesToTest = 100;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < framesToTest; i++) {
            Rect[] faces = faceDetector.getRectFaces(testFrames[2]);
            if (faces.length > 0) {
                faceRecognition.recognize(testFrames[2], faces);
            }

            for (Rect face : faces) {
                if (face != null)
                    face.close();
            }

        }

        long endTime = System.currentTimeMillis();
        long averageTime = (endTime - startTime) / framesToTest;

        System.out.println("FRAME PERFORMANCE TEST - average time equals: " + averageTime);

        assertTrue(averageTime < 120);
    }
}