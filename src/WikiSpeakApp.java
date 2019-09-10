


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;


public class WikiSpeakApp extends Application{
	@Override
	public void start(Stage primaryStage) throws IOException, InterruptedException{
		
		checkCreationFile();
		
		FXMLLoader menuLoader = new FXMLLoader();
		menuLoader.setLocation(this.getClass().getResource("Menu.fxml"));
		Parent layout = menuLoader.load();
		Scene scene = new Scene(layout);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	

	
	public void checkCreationFile() throws IOException, InterruptedException {//Checks if creations folder exists by running a bash script using tempBashScript method.

	    File tempScript = tempBashScript();

	    try {
	        ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
	        pb.inheritIO();
	        Process process = pb.start();
	        process.waitFor();
	    } finally {
	        tempScript.delete();
	    }
	}

	public File tempBashScript() throws IOException {//Creates a temporary bash script that checks creates creations directory if one does not exist.
	    File Script = File.createTempFile("checkCreationFolder", null);

	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(Script));
	    PrintWriter printWriter = new PrintWriter(streamWriter);

	    printWriter.println("#!/bin/bash");
	    printWriter.println("if [ ! -d \"creations\" ]");
	    printWriter.println("then");
	    printWriter.println("mkdir creations");
	    printWriter.println("fi");

	    printWriter.close();

	    return Script;
	}
}
