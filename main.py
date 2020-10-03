import time

from function import _, loop_basic_rule, loop_algorithm, check_box_eliminate_others
import copy


def solve(sudoku):
    start = time.time()
    # Input excel file path and sheet name
    # Copy existing information from question
    solution = copy.deepcopy(sudoku)
    possible_value = {}

    # Cell notation [i, j] i , j: from 1 to 9
    # First is to include all possible numbers for each cell
    for i in range(1, 10):
        for j in range(1, 10):
            possible_value[i, j] = list(range(1, 10))

    # If the cell is already filled, remove all possible numbers for this cell
    for i in range(1, 10):
        for j in range(1, 10):
            if solution[i - 1][j - 1] != 0:
                possible_value[i, j] = []
    # Run basic rules and Crook's algorithm
    while True:
        solution_old = copy.deepcopy(solution)
        loop_basic_rule(possible_value, solution)
        loop_algorithm(possible_value, solution)
        check_box_eliminate_others(possible_value)
        if solution == solution_old:
            break
    end = time.time()
    print("Duration: ", end - start)
    # Check if the final result is solution
    if (
            min([sum(x) for x in zip(*solution)]) == max([sum(x) for x in zip(*solution)])
            and min([sum(x) for x in solution]) == max([sum(x) for x in solution])
            and min([sum(x) for x in zip(*solution)]) == 45
            and min([sum(x) for x in solution]) == 45
    ):
        return solution
    else:
        print("Solution not yet found")
        return False
