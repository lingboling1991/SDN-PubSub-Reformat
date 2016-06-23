package test;

/**
 * Created by lenovo on 2016-6-23.
 */
public class Y implements Runnable {
	private X x;

	public Y(X x) {
		this.x = x;
	}

	@Override
	public void run() {
		try {
			System.out.println("Y: " + x.s);
			Thread.sleep(5000);
			System.out.println("Y: " + x.s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
