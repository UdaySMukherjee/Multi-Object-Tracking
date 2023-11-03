# Object Tracking with YOLOv3 in Java

This Java program demonstrates object tracking using the YOLOv3 model and OpenCV. It processes a video file and detects objects in real-time. The detected objects are highlighted with bounding boxes, and the processed video is displayed in a graphical user interface (GUI).

## Prerequisites

Before running the program, ensure you have the following dependencies:

1. OpenCV 4.0: You should have OpenCV installed, and the OpenCV Java library loaded correctly. The OpenCV library must be compatible with your Java version.

2. YOLOv3 Model: You need the YOLOv3 pre-trained weights and configuration file. Make sure to download the `yolov3.weights` and `yolov3.cfg` files and provide the correct file paths in the code.

3. Java Environment: You should have a Java environment set up on your system.

## Usage

1. Clone this repository or download the source code.

2. Open the `yolo.java` file in your preferred Java development environment.

3. Make sure to set the correct file paths for `modelWeights`, `modelConfiguration`, and `filePath` in the code. These paths should point to the YOLOv3 model files and the video file you want to process.

4. Compile and run the Java program.

## Output

The program will process the specified video file frame by frame. Detected objects will be highlighted with bounding boxes, and the processed video will be displayed in a GUI window.

## Example

Here is an example of how to set the file paths in the Java code:

```java
String modelWeights = "path_to_yolov3.weights"; // Downloaded YOLOv3 weights
String modelConfiguration = "path_to_yolov3.cfg"; // YOLOv3 model configuration
String filePath = "path_to_video_file.avi"; // Path to the video file you want to process
```

Make sure to replace "path_to_yolov3.weights", "path_to_yolov3.cfg", and "path_to_video_file.avi" with the actual file paths on your system.
