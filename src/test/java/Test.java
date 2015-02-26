import java.util.ArrayList;

/**
 * Created by nicolas on 24/02/2015.
 */
public class Test {

	public void test() {
		class MyCollection<T> extends ArrayList<T> {
		}

		MyCollection<String> collection;
	}

}
