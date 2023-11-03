import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;
import org.opencv.highgui.HighGui;


public class ImageRecognition {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Open a video file
        VideoCapture capture = new VideoCapture("C:\\Users\\pc\\Desktop\\javadocs\\atrium.avi");

        // Check if the video file is opened successfully
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video file.");
            return;
        }

        CascadeClassifier faceCascade = new CascadeClassifier("C:\\Users\\pc\\Desktop\\JavaTrack\\haarcascade_fullbody.xml");

        Mat frame = new Mat();
        Mat grayFrame = new Mat();

        while (true) {
            // Read a frame from the video
            if (capture.read(frame)) {
                // Convert the frame to grayscale
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces in the frame
                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(grayFrame, faces);

                // Draw rectangles around the detected faces
                List<Rect> faceList = new ArrayList<>(faces.toList());
                for (Rect face : faceList) {
                    Imgproc.rectangle(frame, face, new Scalar(0, 255, 0), 3);
                }

                // Display the frame with detected faces
                HighGui.imshow("Tracking Video", frame);
                if (HighGui.waitKey(30) >= 0) {
                    break;
                }
            } else {
                System.out.println("End of video.");
                break;
            }
        }
        // Release the video capture and close the display window
        capture.release();
        HighGui.destroyAllWindows();
    }
}
