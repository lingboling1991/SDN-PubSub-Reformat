package test;

/**
 * Created by lenovo on 2016-6-23.
 */
public class X extends Base {
	private static X INSTANCE;
	public int s = 0;
	private Thread y;

	private X() {
		System.out.println("constructor working, X: " + s + ", info: " + info);
//		y = new Thread(new Y(this));
//		y.start();
		info = "def";
		System.out.println("constructor working, X: " + s + ", info: " + info);
	}

	public static X getInstance() {
		if (INSTANCE == null)
			INSTANCE = new X();
		return INSTANCE;
	}
}
