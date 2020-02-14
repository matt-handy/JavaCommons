package handy.common.array;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrayHelperTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		int[] counts = {0, 0, 0, 0, 0, 0, 0};
		int idx = ArrayHelper.getIndexOfHighestAboveZero(counts);
		assertEquals(idx, -1);
		
		int[] counts2 = {0, 1, 0, 1, 0, 0, 0};
		idx = ArrayHelper.getIndexOfHighestAboveZero(counts2);
		assertEquals(idx, 1);
	}

}
