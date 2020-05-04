package handy.common.string;

public class StringHelper {
	public static boolean isNullOrEmpty(String str) {
		if(str == null || str.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}
}
