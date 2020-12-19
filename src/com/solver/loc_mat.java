package com.solver;

import org.opencv.core.Mat;

public class loc_mat {
    int x;
    int y;
    Mat roi;

    public loc_mat(int x, int y, Mat roi) {
        this.x = x;
        this.y = y;
        this.roi = roi;
    }
}
