
import ui.WavGraphing;

class Application {

	private String audioFileName = "";

	private WavGraphing graphFrame = new WavGraphing();
	public static void main(String []args) {

		Application app = new Application(args);
		app.run();

	}

	public Application(String[] args) {
		processArgs(args);	
	}

	public void run() {
		graphFrame.display();
		graphFrame.exec();
	}

	

	private void processArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == "-f" && i + 1 < args.length) {
				System.out.println(args[i]);
				audioFileName = args[i];
			}
			
		}
	}
};

