package handy.common.string;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringHelperTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		assertTrue(StringHelper.isNullOrEmpty(null));
		assertTrue(StringHelper.isNullOrEmpty(""));
		assertFalse(StringHelper.isNullOrEmpty("Dave"));
	}

}
