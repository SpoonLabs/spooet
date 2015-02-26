package sample;

/**
 * Created by nicolas on 25/02/2015.
 */
@MyAnnotation
public class MyClass extends Object implements Runnable, AnInterface {

//	public MyClass(String args) {
//		System.out.println(args);
//	}

	@Override
	public Object testSomething(Object value) {
		return null;
	}

	@Override
	public void run() {
		System.out.println("running");
	}
}
