package com.solver;

import org.opencv.core.Mat;

import java.io.File;

public class TNumber {
    Mat img;
    int number;
    int sum;

    public TNumber(Mat img, int n, int s) {
        this.img = img;
        this.number = n;
        this.sum = s;
    }
}
