import com.solver.Solver;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    @org.junit.jupiter.api.Test
    void solve() {
        int[][] sudoku = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudoku[i][j]=0;
            }
        }
        sudoku[0][2] = 4;
        sudoku[0][7] = 5;
        sudoku[0][8] = 2;


        sudoku[1][2] = 7;
        sudoku[1][5] = 6;
        sudoku[1][7] = 9;
        sudoku[1][8] = 8;

        sudoku[2][0] = 8;
        sudoku[2][3] = 1;
        sudoku[2][4] = 3;
        sudoku[2][6] = 4;
        sudoku[2][8] = 6;
        sudoku[3][0] = 7;
        sudoku[3][3] = 5;
        sudoku[3][5] = 1;
        sudoku[4][1] = 6;
        sudoku[4][8] = 4;
        sudoku[5][0] = 9;
        sudoku[5][5] = 3;
        sudoku[5][6] = 2;
        sudoku[5][7] = 8;
        sudoku[6][6] = 5;
        sudoku[6][8] = 9;
        sudoku[7][4] = 5;
        sudoku[8][0] = 5;
        sudoku[8][1] = 8;
        sudoku[8][4] = 9;
        int[][] sud = Solver.solve(sudoku);
        String sol = "[[6, 3, 4, 8, 7, 9, 1, 5, 2], [1, 5, 7, 4, 2, 6, 3, 9, 8], [8, 9, 2, 1, 3, 5, 4, 7, 6], [7, 2, 8, 5, 4, 1, 9, 6, 3], [3, 6, 5, 9, 8, 2, 7, 1, 4], [9, 4, 1, 7, 6, 3, 2, 8, 5], [4, 7, 6, 3, 1, 8, 5, 2, 9], [2, 1, 9, 6, 5, 4, 8, 3, 7], [5, 8, 3, 2, 9, 7, 6, 4, 1]]";
        assertEquals(sol, Arrays.deepToString(sud));
    }

    @Test
    void solv1() throws FileNotFoundException {
        File file = new File("Resources\\suduoku.txt");
        sudoku_pair sd = getSudokufromTXT(file);
        int[][] sud = Solver.solve(sd.tobe_solved);
        System.out.println(Arrays.deepToString(sud));
        System.out.println(Arrays.deepToString(sd.solved));
        assertTrue(Arrays.deepEquals(sud, sd.solved));
    }

    sudoku_pair getSudokufromTXT(File file) throws FileNotFoundException {
        int[][] sudoku = new int[9][9];
        Scanner sc = new Scanner(file);
        sc.next();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudoku[i][j] = Integer.parseInt(sc.next());
                sc.next();
            }
            sc.next();
        }
        int[][] solved = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                solved[i][j] = Integer.parseInt(sc.next());
                sc.next();
            }
            if (i != 8) {
                sc.next();
            }
        }
        System.out.println(solved.toString());
        sudoku_pair sd = new sudoku_pair(sudoku, solved);
        return sd;
    }
}