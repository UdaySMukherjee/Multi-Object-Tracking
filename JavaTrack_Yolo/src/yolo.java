
import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.utils.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class yolo {

    public static List<String> getOutputNames(Net net) {
        List<String> names = new ArrayList<>();
        List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
        List<String> layersNames = net.getLayerNames();
        outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));//unfold and create R-CNN layers from the loaded YOLO model//
        return names;
    }
    public static void plotConfidenceScores(List<Float> confs) {
        XYSeries series = new XYSeries("Confidence Scores");

        for (int i = 0; i < confs.size(); i++) {
            series.add(i, confs.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Confidence Scores",
                "Object Index",
                "Confidence",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Confidence Scores");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.getContentPane().add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }
    public static void main(String[] args) throws InterruptedException {
        System.load("C:\\Users\\pc\\Downloads\\opencv\\build\\java\\x64\\opencv_java480.dll"); // Load the openCV 4.0 dll //
        String modelWeights = "C:\\Users\\pc\\Desktop\\JavaTrack_Yolo\\yolov3.weights"; //Download and load only wights for YOLO , this is obtained from official YOLO site//
        String modelConfiguration = "C:\\Users\\pc\\Desktop\\JavaTrack_Yolo\\yolov3.cfg.txt";//Download and load cfg file for YOLO , can be obtained from official site//
        String filePath = "C:\\Users\\pc\\Desktop\\javaTrack_Hungarian\\atrium.avi"; //My video  file to be analysed//
        VideoCapture cap = new VideoCapture(filePath);// Load video using the videocapture method//
        Mat frame = new Mat(); // define a matrix to extract and store pixel info from video//
        Mat dst = new Mat ();

        JFrame jframe = new JFrame("Video"); // the lines below create a frame to display the resultant video with object detection and localization//
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setSize(600, 600);
        jframe.setVisible(true);// we instantiate the frame here//

        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights); //OpenCV DNN supports models trained from various frameworks like Caffe and TensorFlow. It also supports various networks architectures based on YOLO//
        //Thread.sleep(5000);

        //Mat image = Imgcodecs.imread("D:\\yolo-object-detection\\yolo-object-detection\\images\\soccer.jpg");
        Size sz = new Size(288,288);

        List<Mat> result = new ArrayList<>();
        List<String> outBlobNames = getOutputNames(net);
        List<Float> allConfidences = new ArrayList<>();

        while (true) {

            if (cap.read(frame)) {
                Mat blob = Dnn.blobFromImage(frame, 0.00392, sz, new Scalar(0), true, false); // We feed one frame of video into the network at a time, we have to convert the image to a blob. A blob is a pre-processed image that serves as the input.//
                net.setInput(blob);
                net.forward(result, outBlobNames); //Feed forward the model to get output //

                float confThreshold = 0.6f; //Insert thresholding beyond which the model will detect objects//
                List<Integer> clsIds = new ArrayList<>();
                List<Float> confs = new ArrayList<>();
                List<Rect> rects = new ArrayList<>();
                for (int i = 0; i < result.size(); ++i)
                {
                    // each row is a candidate detection, the 1st 4 numbers are
                    // [center_x, center_y, width, height], followed by (N-4) class probabilities
                    Mat level = result.get(i);
                    for (int j = 0; j < level.rows(); ++j)
                    {
                        Mat row = level.row(j);
                        Mat scores = row.colRange(5, level.cols());
                        Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                        float confidence = (float)mm.maxVal;
                        Point classIdPoint = mm.maxLoc;
                        if (confidence > confThreshold)
                        {
                            int centerX = (int)(row.get(0,0)[0] * frame.cols()); //scaling for drawing the bounding boxes//
                            int centerY = (int)(row.get(0,1)[0] * frame.rows());
                            int width   = (int)(row.get(0,2)[0] * frame.cols());
                            int height  = (int)(row.get(0,3)[0] * frame.rows());
                            int left    = centerX - width  / 2;
                            int top     = centerY - height / 2;

                            clsIds.add((int)classIdPoint.x);
                            confs.add((float)confidence);
                            rects.add(new Rect(left, top, width, height));
                        }
                    }
                }
                float nmsThresh = 0.5f;

                MatOfFloat confidences = null; // Declare the variable outside of the if block

                if (!confs.isEmpty()) {
                    confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
                    Rect[] boxesArray = rects.toArray(new Rect[0]);
                    MatOfRect boxes = new MatOfRect(boxesArray);
                    MatOfInt indices = new MatOfInt();

                    Rect2d[] boxesArray2d = new Rect2d[boxesArray.length];
                    for (int i = 0; i < boxesArray.length; i++) {
                        Rect rect = boxesArray[i];
                        boxesArray2d[i] = new Rect2d(rect.tl(), rect.br());
                    }

                    MatOfRect2d boxes2d = new MatOfRect2d(boxesArray2d);
                    Dnn.NMSBoxes(boxes2d, confidences, confThreshold, nmsThresh, indices);

                    int [] ind = indices.toArray();
                    int j=0;
                    for (int i = 0; i < ind.length; ++i)
                    {
                        int idx = ind[i];
                        Rect box = boxesArray[idx];
                        Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(0,0,255), 2);

                        // Print confidence score
                        float confidence = confidences.toArray()[idx];
                        System.out.println("Confidence Score: " + confidence);
                        // Accumulate confidence score

                        allConfidences.add(confidence);
                        System.out.println(idx);
                    }


                    ImageIcon image = new ImageIcon(Mat2bufferedImage(frame)); //setting the results into a frame and initializing it //
                    vidpanel.setIcon(image);
                    vidpanel.repaint();

                } else {
                    System.out.println("No valid confidence scores found.");
                    plotConfidenceScores(allConfidences);
                }

            }

        }
    }

    //	}
    public static BufferedImage Mat2bufferedImage(Mat image) {   // The class described here  takes in matrix and renders the video to the frame  //
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }
}
