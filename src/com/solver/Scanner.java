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
import java.util.Arrays;
import java.util.List;

import static org.opencv.core.CvType.*;

public class Scanner {
    // Array with saved numbers used for recognition.
    private static TNumber[] numbers;

    // Loading of CV2 library
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
//        TNumber[] template_numbers = getNumbers();

        int[][] img = scan("Resources/test_images/test_2.jpg");
        System.out.println(Arrays.deepToString(img));
//        String result = scanImage("Resources/test_images/test_1.jpg");
    }

    /** Puts all reference images needed for later recognition in an array
     *  Every element consists of an image of the number, corresponding number and amount of pixels
     *
     * @return Array of Elements(image as Mat, number, amount of pixels)
     */
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
                    tNumber[i] = new TNumber(img, name, s);
                    System.out.println("Path: " + p + "  Name: " + name + "  sum: " + s);
                }
            }
        }
        return tNumber;
    }


    /** Prepares the image for recognition by converting to gray scaling and rescaling (speed up)
     *
     * @param img Matrix of image to be resized
     */
    public static void prepare(Mat img) {
        int rows = img.rows();
        int columns = img.cols();
        int width = 1080;
        int height = (int) (rows / ((double)columns/ width));
        System.out.println("height: " + height + "  width" + width);
        Size newsize = new Size(width, height);
        Imgproc.resize(img, img, newsize, 0, 0, Imgproc.INTER_AREA);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
    }


    /** This functions extracts the sudoku from a taken picture
     *  Based on size and ratio of width and height
     *
     * @param gray Image to be analyzed in gray
     * @return Image of sudoku with tight borders or null if no/multiple sudoku's are found
     */
    public static Mat isolateSudoku(Mat gray) {
        Size s = new Size(8, 8);
        Mat blur = new Mat();
        Imgproc.blur(gray, blur, s);
        Imgproc.GaussianBlur(blur, blur, new Size(5,5),0);
        Mat edged = new Mat();

        Imgproc.Canny(blur, edged, 0, 50);
        Imgproc.dilate(edged, edged, Mat.ones(3, 3, CV_32F));
//        showImage(edged);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edged, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        int save = 0;
        int index = 0;
        int nr_of_sudokus = 0;
        // The first objective is to find the sudoku
        // We do this by finding the contour that satisfies a certain size and is also square
        for (MatOfPoint p : contours) {
            MatOfPoint2f temp = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(p.toArray()), temp, 3, true);
            Rect temprec = Imgproc.boundingRect(new MatOfPoint(temp.toArray()));
            double ratio = (double) temprec.width / temprec.height;
            // Add contour if certain height and square
            if (temprec.height > 0.4 * gray.size().height && (int) Math.floor(ratio) == 1) {
                save = index;
                nr_of_sudokus++;
            }
            index++;
        }

        if (nr_of_sudokus == 0) {
            System.out.println("Not enough or too much sudokus!");
            return null;
        }
        System.out.println("Finished with one sudoku");
        MatOfPoint sud = contours.get(save);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(sud.toArray()));
        return gray.submat(rect);
    }

    /** This function finds all the numbers present in the sudoku based
     *  dimension of the input sudoku.
     *  Note: the input matrix must be a tight sudoku, so no empty space around border of sudoku
     *
     * @param image_roi Matrix of sudoku
     * @return Array of region of interests of the number and the contours
     */
    public static List<Isolated_Number> isolatenumbers(Mat image_roi) {
        Mat thresh = new Mat();
        Imgproc.threshold(image_roi, thresh, 140, 255, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours1 = new ArrayList<>();
        Mat hierarchy1 = new Mat();
        List<Isolated_Number> numbers = new ArrayList<>();
        int size = 0;
        Imgproc.findContours(thresh, contours1, hierarchy1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // For each contour we have found in the image check its dimensions
        for (int i = 0; i < contours1.size(); i++) {
            MatOfPoint p = contours1.get(i);
            Rect temprec = Imgproc.boundingRect(new MatOfPoint(p.toArray()));
            double parent = hierarchy1.get(0, i)[3];
            if (parent > 2 && temprec.width > image_roi.size().width / 40 && temprec.height > image_roi.size().height / 17) {
                Mat numb_temp = image_roi.submat(temprec);

                int posx = (int) ((temprec.x + 0.7*temprec.width) / (image_roi.size().width / 9));
                int posy = (int) ((temprec.y + 0.7*temprec.height) / (image_roi.size().height / 9));

                Isolated_Number temp = new Isolated_Number(posx, posy, numb_temp);
                numbers.add(temp);
                size++;
            }
        }
        return numbers;
    }


    /** This function extracts all numbers given as Isolated_Number and puts them in the correct spot.
     *
     * @param Isolated_Numbers List of Isolated_Numbers,
     *                         Each Isolated_Number consists of its position in the sudoku and the ROI.
     * @return Recognized Sudoku as double int array.
     */
    public static int[][] recognize_set(List<Isolated_Number> Isolated_Numbers) {
        int[][] sudoku = new int[9][9];
        // Prepare the array by setting all values to zero.
        for (int[] ints : sudoku) {
            Arrays.fill(ints, 0);
        }
        for (Isolated_Number numb: Isolated_Numbers) {
            int label = recognize(numb.roi);
            sudoku[numb.y][numb.x] = label;
        }
        return sudoku;
    }


    /** Recognizes a number given as Mat. Done by the absolute difference.
     *
     * @param num image of the separate number as Mat.
     * @return calculate value of the num as int, zero if Mat was not a valid size.
     */
    public static int recognize(Mat num) {
        if (num == null) {
            return 0;
        }
        Mat thresh = new Mat(num.size(), CV_8U);
        Imgproc.threshold(num, thresh, 140, 255, Imgproc.THRESH_BINARY_INV);
        int width = (int) (((double) 70 / num.size().height) * num.size().width);
        if (width > 100) return 0;
        Imgproc.resize(thresh, thresh, new Size(60, 70));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_OPEN, Mat.ones(3,3, CV_8U));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_ERODE, Mat.ones(3,3, CV_8U));

        int distance = Integer.MAX_VALUE;
        int label = 0;
        for (TNumber n : numbers) {
            int number = n.number;
            Mat res = new Mat();
            Core.absdiff(n.img, thresh, res);
            Scalar sum = Core.sumElems(res);
            int s = (int) sum.val[0];
            if (s < distance) {
                label = number;
                distance = s;
            }
        }
        if (distance > 300000) {
            return 0;
        }
        return label;
    }

    /** Combines all previously defined functions. Reads the image and returns sudoku.
     *
     * @param path Path to the to be recognized sudoku (doesn't have to be isolated sudoku);
     * @return Sudoku as double int array.
     */
    public static int[][] scan(String path) {
        Mat img = Imgcodecs.imread(path);
        numbers = getNumbers();
        prepare(img);
        // Extract the numbers from the taken picture
        Mat sudoku = isolateSudoku(img);
        List<Isolated_Number> Isolated_Numbers = isolatenumbers(sudoku);

        return recognize_set(Isolated_Numbers);
    }


    /** Debug function
     *
     * @param img Mat to be displayed.
     */
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
