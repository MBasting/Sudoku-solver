import numpy as np
blocks = np.zeros((3, 3, 9), dtype=int)
for i in range(0, 3):
    for j in range(0, 3):
        blocks[i, j] = np.arange(1, 10)

blocks = blocks.tolist()

block = [0, 0]
cur = blocks[block[0]][block[1]]
blocks[block[0]][block[1]] = [a for a in cur if a != 3]
print(blocks)


# rows = np.zeros((9, 9), dtype=int)
# for i in range(0, 9):
#     rows[i] = np.arange(1, 10)
# rows = rows.tolist()
# rows[1] = [a for a in rows[i] if a != 5]
# print(rows)
#
# columns = rows.copy()
# print(columns)
