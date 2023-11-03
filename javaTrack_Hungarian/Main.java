// Import necessary OpenCV and other Java libraries
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

// Define the main class for the application
public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load OpenCV native library
    }
    static Mat imag = null;
    static Mat orgin = null;
    static Mat kalman = null;
    public static Tracker tracker;
    static Scalar Colors[] = { new Scalar(255,0,0),new Scalar(0,255,0),
            new Scalar(0,0,255),new Scalar(255,255,0),
            new Scalar(0,255,255), new Scalar(255,0,255),
            new Scalar(255,127,255), new Scalar(127,0,255),
            new Scalar(127,0,127)};

    // Define constants for frame dimensions and minimum blob area
    final static int FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width/2;
    final static int FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height/2;
    final static double MIN_BLOB_AREA = 500;

    // Set the default filename for the video
    static String filename = "atrium.avi";

    // Main method
    public static void main(String[] args) throws InterruptedException {
        // Check if a command-line argument is provided to change the video filename
        if (args.length > 0) {
            CONFIG.filename = args[0];
        }

        // Create and configure the first JFrame for displaying video
        JFrame jFrame = new JFrame("MULTIPLE-TARGET TRACKING");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jFrame.setContentPane(vidpanel);
        jFrame.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame.setLocation((3 / 4)
                * Toolkit.getDefaultToolkit().getScreenSize().width, (3 / 4)
                * Toolkit.getDefaultToolkit().getScreenSize().height);
        jFrame.setVisible(true);

        // Create and configure the second JFrame for background subtraction
        JFrame jFrame2 = new JFrame("BACKGROUND SUBSTRACTION");
        jFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel2 = new JLabel();
        jFrame2.setContentPane(vidpanel2);
        jFrame2.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame2.setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2, (3 / 4)
                        * Toolkit.getDefaultToolkit().getScreenSize().height);
        jFrame2.setVisible(false);

        // Initialize OpenCV Mats and other variables
        Mat frame = new Mat();
        Mat outbox = new Mat();
        Mat diffFrame = null;
        Vector<Rect> array = new Vector<Rect>();
        BackgroundSubtractorMOG2 mBGSub = Video
                .createBackgroundSubtractorMOG2();

        tracker = new Tracker((float) CONFIG._dt,
                (float) CONFIG._Accel_noise_mag, CONFIG._dist_thres,
                CONFIG._maximum_allowed_skipped_frames,
                CONFIG._max_trace_length);

        // Open the video capture
        VideoCapture camera = new VideoCapture();
        camera.open(CONFIG.filename);

        int i = 0;

        if (!camera.isOpened()) {
            System.out.print("Can not open Camera, try it later.");
            return;
        }

        // Main loop to process frames from the video
        while (true) {
            if (!camera.read(frame))
                break;
            Imgproc.resize(frame, frame, new Size(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT),
                    0., 0., Imgproc.INTER_LINEAR);
            imag = frame.clone();

            if (i == 0) {
                diffFrame = new Mat(outbox.size(), CvType.CV_8UC1);
                diffFrame = outbox.clone();
            }

            if (i == 1) {
                diffFrame = new Mat(frame.size(), CvType.CV_8UC1);
                processFrame(camera, frame, diffFrame, mBGSub);
                frame = diffFrame.clone();

                array = detectionContours(diffFrame);

                Vector<Point> detections = new Vector<>();

                Iterator<Rect> it = array.iterator();
                while (it.hasNext()) {
                    Rect obj = it.next();

                    int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
                    int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

                    Point pt = new Point(ObjectCenterX, ObjectCenterY);
                    detections.add(pt);
                }

                if (array.size() > 0) {
                    tracker.update(array, detections, imag);
                    Iterator<Rect> it3 = array.iterator();
                    while (it3.hasNext()) {
                        Rect obj = it3.next();

                        int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
                        int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

                        Point pt = new Point(ObjectCenterX, ObjectCenterY);

                        Imgproc.rectangle(imag, obj.br(), obj.tl(), new Scalar(
                                0, 255, 0), 2);
                    }
                } else if (array.size() == 0) {
                }
                for (int k = 0; k < tracker.tracks.size(); k++) {
                    int traceNum = tracker.tracks.get(k).trace.size();
                    if (traceNum > 1) {
                        for (int jt = 1; jt < tracker.tracks.get(k).trace
                                .size(); jt++) {
                            Imgproc.line(imag,
                                    tracker.tracks.get(k).trace.get(jt - 1),
                                    tracker.tracks.get(k).trace.get(jt),
                                    CONFIG.Colors[tracker.tracks.get(k).track_id % 9],
                                    2, 4, 0);
                        }
                    }
                }
            }
            i = 1;
            ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
            vidpanel.setIcon(image);
            vidpanel.repaint();

            ImageIcon image2 = new ImageIcon(Mat2bufferedImage(frame));
            vidpanel2.setIcon(image2);
            vidpanel2.repaint();
        }
    }

    protected static void processFrame(VideoCapture capture, Mat mRgba,
                                       Mat mFGMask, BackgroundSubtractorMOG2 mBGSub) {
        // GREY_FRAME also works and exhibits better performance
        mBGSub.apply(mRgba, mFGMask, CONFIG.learningRate);
        Imgproc.cvtColor(mFGMask, mRgba, Imgproc.COLOR_GRAY2BGRA, 0);
        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
                8, 8));
        Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(8, 8));

        Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(3, 3), new Point(1, 1));
        Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(7, 7), new Point(3, 3));

        Imgproc.threshold(mFGMask, mFGMask, 127, 255, Imgproc.THRESH_BINARY);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, erode);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, dilate);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, openElem);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE, closeElem);
    }

    private static BufferedImage Mat2bufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return img;
    }

    public static Vector<Rect> detectionContours(Mat outmat) {
        Mat v = new Mat();
        Mat vv = outmat.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE);

        int maxAreaIdx = -1;
        Rect r = null;
        Vector<Rect> rect_array = new Vector<Rect>();

        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > CONFIG.MIN_BLOB_AREA && contourarea < CONFIG.MAX_BLOB_AREA) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                rect_array.add(r);
            }
        }
        v.release();
        return rect_array;
    }
}
