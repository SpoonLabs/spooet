package sample;

/**
 * Created by nicolas on 25/02/2015.
 */
public enum AnEnum {
	a, b, c, d, e, f, g;

	private String name;

	AnEnum() {
		name = "name";
	}

	@Override
	public String toString() {
		return name;
	}
}
