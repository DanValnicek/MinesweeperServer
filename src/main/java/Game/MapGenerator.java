package Game;

import java.util.Arrays;
import java.util.Random;

public class MapGenerator {

	public static int[] randMinesGen(int cells, int numOfMines, int forbidden, int rowWidth) {
		Random random = new Random();
		int[] mines = new int[numOfMines];
		loop:
		for (int i = 0; i < numOfMines; i++) {
			mines[i] = random.nextInt(cells);
			for (int y = -1; y < 2; y++) {//Position in column
				if (forbidden + rowWidth * y < 0) continue;
				for (int x = -1; x < 2; x++) {//Position in row
					if ((forbidden + rowWidth * y + x) == mines[i]) {
						i--;
						continue loop;
					}
				}
			}
			for (int j = 0; j < i; j++) {
				if (mines[i] == mines[j]) {
					i--;
					break;
				}
			}
		}
		Arrays.sort(mines);
		System.out.println(Arrays.toString(mines));
		return mines;
	}

	public static int[] randMinesGen(int cells, int numOfMines) {
		Random random = new Random();
		int[] mines = new int[numOfMines];
		for (int i = 0; i < numOfMines; i++) {
			mines[i] = random.nextInt(cells);
			for (int j = 0; j < i; j++) {
				if (mines[i] == mines[j]) {
					i--;
					break;
				}
			}
		}
		Arrays.sort(mines);
		System.out.println(Arrays.toString(mines));
		return mines;
	}

}