import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;


public class MulObj {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture capture = new VideoCapture(0); // 0 represents the default camera (usually the built-in webcam)

        if (!capture.isOpened()) {
            System.out.println("Error: Could not open webcam.");
            return;
        }

        JFrame frame = new JFrame("Webcam Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        frame.setSize(640, 480);
        frame.setVisible(true);

        Mat mat = new Mat();
        BufferedImage img;
        JLabel label = new JLabel();

        while (true) {
            capture.read(mat);
            if (!mat.empty()) {
                Imgcodecs.imencode(".jpg", mat, new MatOfByte());
                img = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
                mat.get(0, 0, ((DataBufferByte) img.getRaster().getDataBuffer()).getData());
                label.setIcon(new ImageIcon(img));
                panel.removeAll();
                panel.add(label);
                frame.revalidate();
            }
        }
    }
}
