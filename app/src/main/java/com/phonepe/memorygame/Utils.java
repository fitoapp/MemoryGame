package com.phonepe.memorygame;

import android.content.Context;
import android.text.TextUtils;

public class Utils {
    public static String getCurrentGridStructure(Context context) {
        if (context == null) {
            return "";
        }
        String currentLevel = PersistanceUtil.getCurrentLevel(context);
        if (TextUtils.isEmpty(currentLevel)) {
            return "";
        }
        switch (currentLevel) {
            case "1":
                return "3x2";
            default:
                return "3x4";
        }
    }

    public static int[] createPictureGrid(int row, int column, int pictureCount) {
        int[] pictureGrid = new int[row * column];
        int[] useCount = new int[pictureCount];
        for (int i = 0; i < pictureCount; ++i) {
            useCount[i] = 0;
        }
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < column; ++j) {
                while (true) {
                    int index = (int) (pictureCount * Math.random());
                    if (useCount[index] < 2) {
                        pictureGrid[i * column + j] = getPictureAtIndex(index);
                        useCount[index]++;
                        break;
                    }
                }
            }
        }
        return pictureGrid;
    }

    private static int getPictureAtIndex(int index) {
        switch (index) {
            case 0:
                return R.drawable.picture1;
            case 1:
                return R.drawable.picture2;
            case 2:
                return R.drawable.picture3;
            case 3:
                return R.drawable.picture4;
            case 4:
                return R.drawable.picture5;
            case 5:
                return R.drawable.picture6;
            default:
                return -1;
        }
    }
}
