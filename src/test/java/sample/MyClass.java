package sample;

/**
 * Created by nicolas on 25/02/2015.
 */
@MyAnnotation
public class MyClass<T> extends Object implements Runnable, AnInterface<T> {

	public MyClass(String args) {
		System.out.println(args);
	}

	@Override
	public T testSomething(Object value) {
		return null;
	}

	@Override
	public void run() {
		System.out.println("running");
	}
}
