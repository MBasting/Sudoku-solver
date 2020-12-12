import os
import numpy as np
import cv2
import matplotlib.pyplot as plt
from python.main_solve import solve
import python.main_solve

template_numbers = []
draw = False


def load_numbers():
    for (dirpath, dirnames, filenames) in os.walk("../numbers"):
        for file in filenames:
            img = cv2.imread("../numbers/" + file)
            img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            img = cv2.bitwise_not(img)
            sum = np.sum(img)
            # file[:-4] returns the name of the file
            template_numbers.append((file[:-4], img, sum))


def prepare(image, width=1080):
    scale_percent = image.shape[1] / width
    height = int(image.shape[0] / scale_percent)
    dim = (width, height)
    # resize image
    new_image = cv2.resize(image, dim, interpolation=cv2.INTER_AREA)
    gray = cv2.cvtColor(new_image, cv2.COLOR_BGR2GRAY)
    return gray, new_image


def isolatenumbers(image, resized):
    kernel = np.ones((8, 8), np.float32) / 64
    dst = cv2.filter2D(image, -1, kernel)
    blurred_image = cv2.GaussianBlur(dst, (5, 5), 0)

    edged_image = cv2.Canny(blurred_image, 0, 50)
    kernel = np.ones((3, 3))
    dilation = cv2.dilate(edged_image, kernel, iterations=1)

    # plt.imshow(dilation, cmap='gray')
    # plt.show()
    ret, thresh = cv2.threshold(dilation, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
    # plt.imshow(thresh, cmap='gray')
    # plt.show()
    edged = edged_image.copy()

    contours, _ = cv2.findContours(edged, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    print("Number of Contours found = " + str(len(contours)))
    cont = []
    area = 0
    for i, cnt in enumerate(contours):
        # if the contour has no other contours inside of it
        x, y, w, h = cv2.boundingRect(cnt)
        ratio = w / h
        if cv2.contourArea(cnt) > (1 / 10 * image.shape[1] * image.shape[1]) and np.round(ratio) == 1:
            print(ratio)
            cont.append(cnt)

    if len(cont) != 1:
        print("No Sudoku found or multiple squares present that could be sudoku's")
        exit(1)

    x, y, w, h = cv2.boundingRect(cont[0])
    roi = resized[y:y + h, x:x + w]
    # plt.imshow(roi)
    # plt.show()
    roi_gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
    # blurred_image = cv2.GaussianBlur(roi_gray, (7, 7), 0)
    # closing = cv2.morphologyEx(blurred_image, cv2.MORPH_CLOSE, np.ones((5, 5)))
    ret, thresh1 = cv2.threshold(roi_gray, 140, 255, cv2.THRESH_BINARY)

    contours, hierarchy = cv2.findContours(thresh1, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
    cont = []
    numbers = []
    for i, cnt in enumerate(contours):
        # if the contour has no other contours inside of it
        x, y, w, h = cv2.boundingRect(cnt)
        # Only keep the contours that are in the third layer!
        if hierarchy[0, i, 3] > 2 and w > int(roi.shape[1] / 50) and h > int(roi.shape[0] / 17):
            cont.append(cnt)
            numbers.append([(x, y), roi_gray[y:y + h, x:x + w]])
    return numbers, roi_gray, cont


def recognize_set(pos_image):
    sudoku = np.zeros(shape=(9, 9), dtype=int)
    for (position, image) in pos_image:
        label = recognize(image)
        if label is not None:
            sudoku[position[0], position[1]] = int(label)
    return sudoku


def recognize(char):
    height, width = char.shape
    cutoff_h = int(height / 10)
    cutoff_w = int(width / 10)
    char = char[cutoff_h:height - cutoff_h, cutoff_w:width - cutoff_w].copy()

    scale = 70 / char.shape[0]
    width = int(scale * char.shape[1])
    char = cv2.resize(char, dsize=(width, 70), interpolation=cv2.INTER_LINEAR)
    char = cv2.blur(char, (5, 5))
    if char.shape[1] > 60:
        return None

    # Matching the size of the incoming letters to the example letters.
    c = np.zeros((70, 60))
    _, thres = cv2.threshold(char, 127, 255, cv2.THRESH_BINARY_INV)
    erode = cv2.morphologyEx(thres, cv2.MORPH_ERODE, np.ones((5, 5)))
    c[0:70, 0:char.shape[1]] = erode
    distance = 9999999
    label = ""
    for check in template_numbers:
        t_img = check[1]
        dist = np.sum(np.abs(c - t_img))
        if dist < distance:
            distance = dist
            label = check[0]  # Zero index of check contains the file name which in our case is the number.
    if distance > 300000:
        print(label, distance)
    return label

def scan(path):
    print("starting")
    load_numbers()
    img = cv2.imread(path)
    gray_image, resized = prepare(img)

    num, ROI, contours = isolatenumbers(gray_image, resized)
    if draw:
        cv2.drawContours(ROI, contours, -1, (0, 255, 0), 3)
        plt.imshow(ROI, cmap='gray')
        plt.show()
    numbers = []
    for cnt in contours:
        x, y, w, h = cv2.boundingRect(cnt)
        temp = ROI[y:y + h, x:x + w]
        pos = (np.round((y + 0.5 * h) / (ROI.shape[0] / 10)) - 1, np.round((x + 0.5 * w) / (ROI.shape[1] / 10)) - 1)
        pos = (int(pos[0]), int(pos[1]))
        numbers.append((pos, temp))
        if draw:
            plt.imshow(temp, cmap='gray')
            plt.show()
    return recognize_set(numbers)


path = "../test_images/test_1.jpg"
result = scan(path);
print(solve(result.tolist()))
