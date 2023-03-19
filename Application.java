
import ui.WavGraphing;

class Application {

	private String audioFileName = "";

	private WavGraphing graphFrame = new WavGraphing();

	public Application(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == "-f" && i + 1 < args.length) {
				System.out.println(args[i]);
				audioFileName = args[i];
			}
			
		}
	}

	public void run() {
		graphFrame.display();
	}

	public static void main(String []args) {

		Application app = new Application(args);
		app.run();

	}
};

