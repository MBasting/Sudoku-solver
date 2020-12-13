package com.solver;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32F;

public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
//        TNumber[] template_numbers = getNumbers();
        isolatenumbers(prepare(Imgcodecs.imread("Resources/test_images/test_1.jpg")));
//        String result = scanImage("Resources/test_images/test_1.jpg");
    }

    public static TNumber[] getNumbers()  {
        File path = new File("Resources/numbers");
        TNumber[] tNumber = new TNumber[9];
        File [] files = path.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++){
                if (files[i].isFile()){ //this line weeds out other directories/folders
                    File temp = files[i];
                    String p = temp.getPath();
                    int name = Integer.parseInt(String.valueOf(temp.getName().charAt(0)));
                    Mat img = Imgcodecs.imread(p);
                    Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
                    // To calculate the number of pixels active we need to count the white pixels
                    // So the image needs to be inverted before calculating.
                    Core.bitwise_not(img, img);
                    Scalar sum = Core.sumElems(img);
                    int s = (int) sum.val[0];
                    tNumber[i] = new TNumber(p, name, s);
                    showImage(img);
                    System.out.println("Path: " + p + "  Name: " + name + "  sum: " + s);
                }
            }
        }
        return tNumber;
    }

    public static Mat prepare(Mat img) {
        int rows = img.rows();
        int columns = img.cols();
        int width = 1080;
        int height = columns / (rows/ width);
        Size newsize = new Size(1080, height);
        Mat resizeimage = new Mat();
        Imgproc.resize(img, resizeimage, newsize, 0, 0, Imgproc.INTER_AREA);
        Imgproc.cvtColor(resizeimage, resizeimage, Imgproc.COLOR_BGR2GRAY);
        return resizeimage;
    }

    public static int isolatenumbers(Mat mat) {
        Size s = new Size(8, 8);
        Imgproc.blur(mat, mat, s);
        Mat blur = new Mat();
        Imgproc.GaussianBlur(mat, blur, new Size(5,5),0);
        Mat edged = new Mat();

        Imgproc.Canny(blur, edged, 0, 50);
        Imgproc.dilate(edged, edged, Mat.ones(3, 3, CV_32F));
        showImage(edged);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edged, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        return 0;
    }

    public String solveSudoku(String sudoku) {
        return "";
    }

    public static String scanImage(String file) {

        Mat img = Imgcodecs.imread(file);
        showImage(prepare(img));
        System.out.println("Image Loaded");
        return "";
    }

    public static void showImage(Mat img) {
        try {
            MatOfByte mat = new MatOfByte();
            Imgcodecs.imencode(".jpg", img, mat);
            byte[] byteArray = mat.toArray();
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage buf = ImageIO.read(in);
            JFrame fr = new JFrame();
            fr.getContentPane().add(new JLabel(new ImageIcon(buf)));
            fr.pack();
            fr.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
