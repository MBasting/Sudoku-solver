package com.solver;

import java.util.HashSet;
import java.util.Set;

public class Solver {

    public int[][] solve(int[][] sudoku) {
        return null;

    }

    public Set<Integer> possibilities_row(int[][] sudoku, int row) {
        Set<Integer> pos = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            pos.add(i+1);
        }
        for (int i = 0; i < 9; i++) {
            if(sudoku[row][i] != 0) pos.remove(sudoku[row][i]);
        }
        return pos;
    }

    public Set<Integer> possibilities_col(int[][] sudoku, int column) {
        Set<Integer> pos = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            pos.add(i+1);
        }
        for (int i = 0; i < 9; i++) {
            if(sudoku[i][column] != 0 ) pos.remove(sudoku[i][column]);
        }
        return pos;
    }


}
