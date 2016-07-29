package sample;

/**
 * Created by nicolas on 25/02/2015.
 */
@MyAnnotation(12)
public interface AnInterface<T> {
	<R> T testSomething(R value);
}
