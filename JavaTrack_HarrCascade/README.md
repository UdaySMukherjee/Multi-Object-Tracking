# Face Detection in Video using OpenCV

This Java program uses the OpenCV library to perform real-time face detection in a video stream. It reads a video file, applies a Haar Cascade classifier for face detection, and draws rectangles around detected faces in each frame.

## Prerequisites

To run this program, you need to have the following prerequisites installed:

1. Java Development Kit (JDK)
2. OpenCV library for Java

You can download OpenCV for Java from the official OpenCV website: [OpenCV for Java](https://opencv.org/releases.html).

## Usage

1. Clone or download this repository to your local machine.

2. Make sure to configure your Java project to include the OpenCV library. This typically involves adding the OpenCV `.jar` files to your project's classpath. You can refer to the OpenCV documentation for detailed installation instructions.

3. Update the paths to the video file and Haar Cascade classifier in the `main` method of the `ImageRecognition` class:

   ```java
   VideoCapture capture = new VideoCapture("C:\\path\\to\\your\\video.mp4");
   CascadeClassifier faceCascade = new CascadeClassifier("C:\\path\\to\\haarcascade_frontalface_alt.xml");
   ```
4. Build and run the program. It will open a window displaying the video stream with detected faces highlighted by green rectangles.

5. To exit the program, close the video window or press any key within the window.
