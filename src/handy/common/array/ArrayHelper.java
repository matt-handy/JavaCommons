package handy.common.array;

public class ArrayHelper {
	public static int getIndexOfHighestAboveZero(int[] counts) {
		int greatestCount = Integer.MIN_VALUE;
		int greatestCountIdx = -1;
		for (int jdx = 0; jdx < counts.length; jdx++) {
			int i = counts[jdx];
			if (i > greatestCount && i > 0) {
				greatestCount = i;
				greatestCountIdx = jdx;
			}
		}
		return greatestCountIdx;
	}
}
