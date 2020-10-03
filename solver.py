import numpy as np
import copy
import random
import time


def solveSudoku(sudokuOriginal):
    blocks, rows, columns = fillPossibilities(sudokuOriginal)
    counter = 0
    start = time.time()
    res, sudoku1 = trySudoku(copy.deepcopy(sudokuOriginal), copy.deepcopy(blocks),
                                copy.deepcopy(rows), copy.deepcopy(columns))
    counter += 1
    if res:
        print(sudoku1)
        print("Number of attempts: ", counter)
        end = time.time()
        print("Duration: ", end - start)
        return sudoku1


def trySudoku(sudoku, blocks, rows, columns):
    sudoku1 = []
    for i in range(len(sudoku)):
        for j in range(len(sudoku[i])):
            if sudoku[i][j] == 0:
                res, sudokus, mblocks, mrows, mcolumns = solveEntry(sudoku, blocks, rows, columns, [i, j])
                if not res:
                    return False, sudoku1
                # Try all possible valid numbers, and continue with those numbers.
                for sud in range(len(sudokus)):
                    curS = sudokus[sud]
                    curB = mblocks[sud]
                    curRow = mrows[sud]
                    curCol = mcolumns[sud]
                    res, temp = trySudoku(curS, curB, curRow, curCol)
                    if not res:
                        continue
    return True, sudoku1


def solveEntry(sudoku, blocks, rows, columns, pos):
    posb, block = getPossibilitiesSquare(blocks, rows, columns, pos)
    if len(posb) == 0:
        return False, sudoku, blocks, rows, columns
    sudokus = []
    mblocks = []
    mrows = []
    mcols = []
    for number in posb:
        sudoku1 = copy.deepcopy(sudoku)
        sudoku1[pos[0]][pos[1]] = number
        sudokus.append(sudoku1)
        mblocks.append(updateBlock(copy.deepcopy(blocks), block, number))
        mrows.append(updateRowColumn(copy.deepcopy(rows), pos[0], number))
        mcols.append(updateRowColumn(copy.deepcopy(columns), pos[1], number))
    return True, sudokus, mblocks, mrows, mcols


def getPossibilitiesSquare(blocks, rows, columns, pos):
    block = (int(np.floor(pos[0] / 3)), int(np.floor(pos[1] / 3)))
    curblock = blocks[block[0]][block[1]]
    currow = rows[pos[0]]
    curcol = columns[pos[1]]
    possibilities = list(set(curcol).intersection(set(curblock).intersection(currow)))
    return possibilities, block


def updateBlock(blocks, block, entry):
    cur = blocks[block[0]][block[1]]
    blocks[block[0]][block[1]] = [a for a in cur if a != entry]
    return blocks


def updateRowColumn(rows, i, entry):
    rows[i] = [a for a in rows[i] if a != entry]
    return rows


def fillPossibilities(sudoku):
    blocks = np.zeros((3, 3, 9), dtype=int)
    for i in range(0, 3):
        for j in range(0, 3):
            blocks[i, j] = np.arange(1, 10)
    blocks = blocks.tolist()
    rows = np.zeros((9, 9), dtype=int)
    for i in range(0, 9):
        rows[i] = np.arange(1, 10)
    rows = rows.tolist()
    columns = rows.copy()
    for i in range(len(sudoku)):
        for j in range(len(sudoku[i])):
            if sudoku[i][j] != 0:
                entry = sudoku[i][j]
                block = (int(np.floor(i / 3)), int(np.floor(j / 3)))
                updateBlock(blocks, block, entry)
                updateRowColumn(rows, i, entry)
                updateRowColumn(columns, j, entry)
    return blocks, rows, columns


testsudoku = [[0, 0, 0, 4, 0, 0, 0, 3, 0],
              [7, 0, 4, 8, 0, 0, 1, 0, 2],
              [0, 0, 0, 2, 3, 0, 4, 0, 9],
              [0, 4, 0, 5, 0, 9, 0, 8, 0],
              [5, 0, 0, 0, 0, 0, 9, 1, 3],
              [1, 0, 0, 0, 8, 0, 2, 0, 4],
              [0, 0, 0, 0, 0, 0, 3, 4, 5],
              [0, 5, 1, 9, 4, 0, 7, 2, 0],
              [4, 7, 3, 0, 5, 0, 0, 9, 1]]


difficult =   [[0, 0, 0, 0, 0, 0, 3, 0, 7],
              [9, 0, 0, 0, 0, 0, 5, 1, 4],
              [3, 0, 4, 0, 1, 6, 0, 2, 0],
              [0, 0, 6, 0, 0, 0, 0, 5, 0],
              [2, 0, 0, 0, 0, 4, 0, 0, 0],
              [0, 0, 0, 0, 0, 9, 4, 0, 0],
              [0, 0, 1, 9, 0, 0, 0, 7, 6],
              [0, 0, 0, 0, 0, 0, 0, 3, 0],
              [0, 0, 7, 6, 0, 0, 0, 0, 5]]
solveSudoku(testsudoku)

