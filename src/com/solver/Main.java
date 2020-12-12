package com.solver;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        String result = scanImage("C:\\Users\\MarkBasting\\Documents\\Programming\\sudoku_solver_java\\test_images\\test_1.jpg");
    }

    public String solveSudoku(String sudoku) {
        return "";
    }

    public static String scanImage(String file) {
        try {
            Mat img = Imgcodecs.imread(file);
            int rows = img.rows();
            int columns = img.cols();
            int width = 1080;
            int height = columns / (rows/ width);
            Size newsize = new Size(1080, height);
            Mat resizeimage = new Mat();
            Imgproc.resize(img, resizeimage, newsize, 0, 0, Imgproc.INTER_AREA);

            MatOfByte mat = new MatOfByte();
            Imgcodecs.imencode(".jpg", resizeimage, mat);
            byte[] byteArray = mat.toArray();
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage buf = ImageIO.read(in);
            JFrame fr = new JFrame();
            fr.getContentPane().add(new JLabel(new ImageIcon(buf)));
            fr.pack();
            fr.setVisible(true);

            System.out.println("Image Loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
