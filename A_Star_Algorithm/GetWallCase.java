package A_Star_simulation;

import java.util.Random;

public class GetWallCase {

    public int[][] getBoundary(int boundX, int boundY) {
        int[][] array = new int[boundY][boundX];

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (i == 0 || j == 0 || i == boundY - 1 || j == boundX - 1) {
                    array[i][j] = 1;
                }
            }
        }

        return array;
    }

    public int[][] getRandomMap(int boundX, int boundY, int percent) {
        int[][] array = new int[boundY][boundX];

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                Random rand = new Random();

                if (i == 0 || j == 0 || i == boundY - 1 || j == boundX - 1) {
                    array[i][j] = 1;
                } else {
                    if (rand.nextInt(100) + 1 <= percent) {
                        array[i][j] = 1;
                    } else {
                        array[i][j] = 0;
                    }
                }
            }
        }

        return array;
    }
}
