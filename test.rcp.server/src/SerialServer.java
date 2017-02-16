

public class SerialServer {

	private static final int CARRIAGE_RETURN_CODE = 13;

	public static void main(String[] args) {
		Serial serial = new Serial("COM50", 57600);

		while (serial.available() <= 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String intTime = serial.readStringUntil(CARRIAGE_RETURN_CODE);
		System.out.println("Integration time: " + intTime);

		for (int i = 0; i < 3700; i++) {
			int j = (int) (Math.random() * 400000) + 10000;
			serial.write(j + "");
			serial.write(CARRIAGE_RETURN_CODE);
		}

		serial.dispose();
	}
}
