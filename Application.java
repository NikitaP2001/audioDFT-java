
import ui.WavGraphing;

class Application {

	private String audioFileName = "sample.wav";

	private WavGraphing graphFrame;

	public static void main(String []args) {

		Application app = new Application(args);
		app.run();

	}

	public Application(String[] args) {
		processArgs(args);	
	}

	public void run() {
		graphFrame = new WavGraphing(audioFileName);
		try {
			graphFrame.display();
			graphFrame.exec();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void processArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f") && i + 1 < args.length)
				audioFileName = args[i + 1];
			
		}
	}
};

