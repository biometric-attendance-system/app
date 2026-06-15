# Biometric Attendance System

A system created for lecturers, it registers students faces and recognizes them to mark student as present.
## Technologies
* **Java 17+**
* **JavaFX** (user interface)
* **OpenCV / JavaCV** (image processing and facial recognition)
* **SQLite** (local database)
* **Maven** (dependency management)

## Key Features
* **Biometric Authorization:** Uses facial recognition for logging in.
* **Database Management:** Lecturer and student registration and creating models of their face data.
* **Automatic Reporting:** Real-time attendance changes with statistics.
* **Data Security:** Local storage of biometric model in a user folder.
* **Intuitive Interface:** User-friendly JavaFX interface.

## Setup Instructions

### Requirements
* Java 17 or newer.
* Connected webcam.

### Installation and Running
1. Clone the repository:
   `git clone <git@github.com:biometric-attendance-system/app.git>`
2. Build the project:
   `mvn clean install`
3. Run the app:
   `mvn javafx:run`

## Data Structure
The application stores user's facial images in temp folder, updates biometric model file and deletes photos. Model data in the user's home directory (`user.home`):
* `~/.FaceRecognitionApp/` - folder containing:
    * Biometric model file (`.yml`).

## Workflow
1. **Initial Setup:** Upon the first launch, lecturer must register and record their face.
2. **Model Training:** The application automatically processes captured images to create a biometric model (`.yml`).
3. **Attendance Logging:** During a lecture, the application scans the camera feed, recognizes faces, and updates the attendance status in real-time. Students not registered are labeled as absent.
4. **Statistics:** Lecturers can view attendance and statistics of every student.

