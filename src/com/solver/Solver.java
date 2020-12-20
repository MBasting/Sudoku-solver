package com.solver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Solver {

    public static int[][] solve(int[][] sudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9;j++) {
                if (sudoku[i][j] == 0) {
                    Set<Integer> pos = posibilities(sudoku, i, j);
                    if (pos.size() == 0) {
                        return null;
                    }
                    for (int a : pos) {
                        int[][] temp_sud = new int[9][9];
                        for (int x = 0; x < sudoku.length; x++){
                            temp_sud[x] = Arrays.copyOf(sudoku[x], sudoku[x].length);
                        }
                        temp_sud[i][j] = a;
                        temp_sud = solve(temp_sud);
                        if (temp_sud != null) {
                            return temp_sud;
                        }
                    }
                    return null;
                }
            }
        }
        return sudoku;
    }

    public static Set<Integer> posibilities(int[][] sudoku, int row, int column) {

        Set<Integer> pos = possibilities_row(sudoku, row);
        Set<Integer> pos_col = possibilities_col(sudoku, column);
        Set<Integer> pos_block = possibilities_block(sudoku, row, column);
        pos.retainAll(pos_col);
        pos.retainAll(pos_block);
        return pos;
    }


    public static Set<Integer> possibilities_row(int[][] sudoku, int row) {
        Set<Integer> pos = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            pos.add(i+1);
        }
        for (int i = 0; i < 9; i++) {
            if(sudoku[row][i] != 0) pos.remove(sudoku[row][i]);
        }
        return pos;
    }

    public static Set<Integer> possibilities_col(int[][] sudoku, int column) {
        Set<Integer> pos = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            pos.add(i+1);
        }
        for (int i = 0; i < 9; i++) {
            if(sudoku[i][column] != 0 ) pos.remove(sudoku[i][column]);
        }
        return pos;
    }

    public static Set<Integer> possibilities_block(int[][] sudoku, int row, int column) {
        int block = (int) Math.floor(row / 3) * 3 + (int) Math.floor(column / 3);
        int start_row = (int) Math.floor(block / 3) * 3;
        int start_column = block % 3 * 3;
        Set<Integer> pos = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            pos.add(i+1);
        }
        for (int i = start_row; i < start_row + 3; i++) {
            for (int j = start_column; j < start_column + 3; j++) {
                if(sudoku[i][j] != 0 ) pos.remove(sudoku[i][j]);
            }
        }
        return pos;
    }
}
