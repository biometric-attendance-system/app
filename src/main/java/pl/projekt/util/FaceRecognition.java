    package pl.projekt.util;

    import java.io.File;
    import java.nio.file.Path;
    import java.nio.file.Files;
    import java.nio.IntBuffer;
    import java.util.Arrays;

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

    /**
     * Class responsible for self-training and recognition of faces.
     */
    public class FaceRecognition{
        private final String APP_DIR_PATH = System.getProperty("user.home") + File.separator + ".FaceRecognitionApp";
        private final String MODEL_FILE_PATH = APP_DIR_PATH + File.separator + "trained_model.yml";


        private FaceRecognizer faceRecognizer;
        private Path tempDir;
        private String albumNumber;
        private int count;
        private final double CONFIDENCE = 35.0;

        /**
         * Function creating train File.
         *
         * @return Train file.
         */
        private File getTrainFile(){
            File trainDir = new File(APP_DIR_PATH);
            if (!trainDir.exists()){
                if (!trainDir.mkdirs()) return null;
            }
            return new File(MODEL_FILE_PATH);
        }

        /**
         * Function checks whether face position is outside frame bounds.
         *
         * @param frame Captured frame.
         * @param rect Position of a face.
         * @return Checked Rect.
         */
        private Rect getSafeRect(Mat frame, Rect rect){
            int x = Math.max(0, rect.x());
            int y = Math.max(0, rect.y());
            int width = Math.min(frame.cols() - x, rect.width());
            int height = Math.min(frame.rows() - y, rect.height());

            if (width <= 0 || height <= 0) return null;

            return new Rect(x,y,width,height);
        }

        /**
         * Function loads LBPH recognizer and uses it to read existing train file.
         *
         * @return True if train file exists false otherwise.
         */
        public boolean loadRecognizer(){
            if (faceRecognizer == null) {
                faceRecognizer = LBPHFaceRecognizer.create();
            }
            File trainFile = getTrainFile();

            if (trainFile != null && trainFile.exists()) {
                faceRecognizer.read(trainFile.getAbsolutePath());
            } else {
                return false;
            }
            return true;
        }

        /**
         * Function closing faceRecognizer.
         */
        public void closeRecognizer(){
            faceRecognizer.close();
        }

        /**
         * Function iterates through face positions and predicts every one.
         * If prediction's confidence is too low or face position is out of bounds
         * it is labeled as "unknown". When predicted with satisfying confidence it saves
         * predicted label (album number or Lecturer ID number) of a face.
         *
         * @param faces Frame captured
         * @param pos Array of found faces
         * @return Array of labels
         */
        public String[] recognize(Mat faces, Rect[] pos) {
            String[] temp = new String[pos.length];

            if (faceRecognizer == null) {
                boolean isLoaded = loadRecognizer();
                if (!isLoaded) {
                    Arrays.fill(temp, "Unknown");
                    return temp;
                }
            }

            try (IntPointer label = new IntPointer(1);
                 DoublePointer confidence = new DoublePointer(1)) {

                for (int i = 0; i < pos.length; i++) {
                    Rect safeRect = getSafeRect(faces, pos[i]);

                    if (safeRect == null) {
                        temp[i] = "Unknown";
                        continue;
                    }

                    try (safeRect;
                         Mat cropped = new Mat(faces, safeRect);
                         Mat grayCropped = new Mat()) {

                        cvtColor(cropped, grayCropped, COLOR_BGR2GRAY);
                        equalizeHist(grayCropped, grayCropped);

                        faceRecognizer.predict(grayCropped, label, confidence);

                        if (confidence.get(0) <= CONFIDENCE) {
                            temp[i] = String.valueOf(label.get(0));
                        } else {
                            temp[i] = "Unknown";
                        }
                    }
                }
            }
            return temp;
        }

        /**
         * Function that creates temporary directory for a given album or ID number.
         *
         * @param albumNumber Album or ID number.
         * @return True if directory created, false otherwise.
         */
        public boolean createDir(String albumNumber){
            try{
                tempDir = Files.createTempDirectory("FaceRecognitionApp-" + albumNumber);
                count = 0;
                this.albumNumber = albumNumber;
                return true;
            } catch (Exception e){
                System.err.println("Error: Could not open a temp folder");
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Function that deletes temporary directory and files within it.
         *
         * @return True if directory deleted, false otherwise.
         */
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

        /**
         * Photo count getter.
         *
         * @return Number of photos saved.
         */
        public int getPhotoCount(){
            return count;
        }

        /**
         * Function that saves cropped face from a frame.
         * If more than one face is detected it does not save a frame.
         *
         * @param frame Frame captured.
         * @param faces Positions of faces.
         */
        public void saveFace(Mat frame, Rect[] faces) {
            if (faces.length != 1 || tempDir == null) return;

            Rect safeRect = getSafeRect(frame, faces[0]);

            if (safeRect == null) return;

            try (safeRect;
                 Mat cropped = new Mat(frame, safeRect);
                 Mat grayCropped = new Mat()) {

                cvtColor(cropped, grayCropped, COLOR_BGR2GRAY);
                equalizeHist(grayCropped, grayCropped);

                String filePath = tempDir.toAbsolutePath().toString() +
                        File.separator + albumNumber + "-" + count + ".png";

                imwrite(filePath, grayCropped);

                count++;
            }
        }

        /**
         * Function that accumulates saved face frames into a MatVector and
         * trains faceRecognizer a new face with their album or ID number
         * saved with it as a label.
         *
         * @return True if training was successful, false otherwise.
         */
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

                if (trainFile == null) return false;

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