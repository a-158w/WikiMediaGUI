import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class CreationsController implements Initializable{
	
	@FXML
	private TextField WikitSearchField;
	@FXML
	private TextArea WikitTextArea;
	@FXML
	private TextField NoOfLinesTextField;
	@FXML
	private TextField CreationNameTextField;
	@FXML
	private Button MainMenuButton;
	@FXML
	private Button CreateButton;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {//Greys out create button until search is done.
		
		CreateButton.setDisable(true);
		
	}
	
	@FXML
	public void SearchButtonHandler() throws InterruptedException, IOException {//Starts thread when search button is clicked to run wikit search and display results.
	
		Thread thread = new Thread(new backgroundWikit());
		thread.start();


	}
	
	private class backgroundWikit extends Task<Void>{//Thread class for running wikit search.

		@Override
		protected Void call() throws Exception {
			String searchTerm = WikitSearchField.getText();
			
			File tempScript = tempWikitScript(searchTerm);

		    try {
		        ProcessBuilder wikitProcessBuilder = new ProcessBuilder("bash", tempScript.toString());
		        wikitProcessBuilder.inheritIO();
		        Process wikitProcess = wikitProcessBuilder.start();
		        wikitProcess.waitFor();
		    } finally {
		        tempScript.delete();
		    }
			return null;
		}
		
		@Override
		protected void done() {//Updates Text Area with wikit results
			Platform.runLater(()->{
				FileReader reader = null;
				try {
					reader = new FileReader(".wikitResultFileNumbered.txt");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				BufferedReader bufferstdout = new BufferedReader(reader);
				String line = null;
				try {
					while((line = bufferstdout.readLine()) != null) {
						WikitTextArea.appendText("\n"+line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				CreateButton.setDisable(false);
			});
		}
	}
	
	public File tempWikitScript(String searchTerm) throws IOException {//Creates a temporary bash script that returns results of wikit split line-by-line
	    File Script = File.createTempFile("WikitScript", null);

	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(Script));
	    PrintWriter printWriter = new PrintWriter(streamWriter);

	    printWriter.println("#!/bin/bash");
	    printWriter.println("wikit " +searchTerm+ " > .wikitResultFile.txt");
	    printWriter.println("cat .wikitResultFile.txt | sed 's/[.!?]  */&\\n/g' | tee .wikitResultFile.txt &> /dev/null");
	    printWriter.println("cat -n .wikitResultFile.txt | tee .wikitResultFileNumbered.txt &> /dev/null");

	    printWriter.close();

	    return Script;
	}
	
	
	@FXML
	private void handleCreateButton(ActionEvent event) {
		File temporaryDirectory = new File("creations/"+CreationNameTextField.getText()+".mkv");
		boolean exists = temporaryDirectory.exists();
		
		if(! exists) {
			boolean invalidChar = invalidText(CreationNameTextField.getText());
			if(! invalidChar) {
				Thread thread = new Thread(new backgroundCreate());
				thread.start();
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error encountered");
				alert.setHeaderText("You entered some invalid characters");
				alert.setContentText("Please use only letters and hyphens.");
				alert.showAndWait();
				MainMenuButton.fire();
			}
		}else {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to overwrite "+CreationNameTextField.getText()+" ?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();//Gives user option to overwrite file.

			if (alert.getResult() == ButtonType.YES) {
				Thread thread = new Thread(new backgroundCreate());
				thread.start();
			}else {
				MainMenuButton.fire();
			}
		}
		
	}
	
	public boolean invalidText(String title) {
		String[] chars = {"$", "*", " ", "(", ")", "|", "\\", "<", "?", ">", "/", ":", "\"", "`", "[", "]"};
		
		for (String character: chars) {
			if(title.contains(character)) {
				return true;
			}
		}
		return false;
	}

	
	public class backgroundCreate extends Task<Void>{

		@Override
		protected Void call() throws Exception {
		
			File tempScript = tempCreateScript();

		    try {
		        ProcessBuilder createProcessBuilder = new ProcessBuilder("bash", tempScript.toString());
		        createProcessBuilder.inheritIO();
		        Process wikitProcess = createProcessBuilder.start();
		        wikitProcess.waitFor();
		    } finally {
		        tempScript.delete();
		    }
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(()->{
				MainMenuButton.fire();
			});
		}
	}
	
	public File tempCreateScript() throws IOException {//Creates a temporary bash script that returns results of wikit split line-by-line
	    File Script = File.createTempFile("WikitScript", null);

	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(Script));
	    PrintWriter printWriter = new PrintWriter(streamWriter);

	    printWriter.println("#!/bin/bash");
	    printWriter.println("if [ -f \"creations/"+CreationNameTextField.getText()+".mkv\" ]");
	    printWriter.println("then");
	    printWriter.println("rm creations/"+CreationNameTextField.getText()+".mkv");
	    printWriter.println("fi");
	    
	    printWriter.println("cat .wikitResultFile.txt | sed -n \"1,"+NoOfLinesTextField.getText()+"p\" | tee .wikitGenerationFile.txt &> /dev/null");
	    printWriter.println("text2wave .wikitGenerationFile.txt -o .wikitAudio.wav");
	    printWriter.println("duration=`ffprobe -i .wikitAudio.wav -show_entries format=duration -v quiet -of csv=\"p=0\"`");
	    printWriter.println("ffmpeg -f lavfi -i color=c=blue:s=640x480:d=$duration -vf \"drawtext=fontfile=Lato-Black.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+WikitSearchField.getText()+"'\" .noAudioVideo.mp4 &> /dev/null");
	    printWriter.println("ffmpeg -i .noAudioVideo.mp4 -i .wikitAudio.wav -c copy creations/"+CreationNameTextField.getText()+".mkv &> /dev/null");

	    printWriter.close();

	    return Script;
	}
	
	public File tempCleanScript() throws IOException {//Creates a temporary bash script that deletes temporary files used to create creations.
	    File Script = File.createTempFile("WikitScript", null);

	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(Script));
	    PrintWriter printWriter = new PrintWriter(streamWriter);

	    
	    printWriter.println("if [ -f \".wikitResultFileNumbered.txt\" ]");
	    printWriter.println("then");
	    printWriter.println("rm .wikitResultFileNumbered.txt");
	    printWriter.println("fi");
	    
	    printWriter.println("if [ -f \".wikitGenerationFile.txt\" ]");
	    printWriter.println("then");
	    printWriter.println("rm .wikitGenerationFile.txt");
	    printWriter.println("fi");
	    
	    printWriter.println("if [ -f \".noAudioVideo.mp4\" ]");
	    printWriter.println("then");
	    printWriter.println("rm .noAudioVideo.mp4");
	    printWriter.println("fi");
	    
	    printWriter.println("if [ -f \".wikitAudio.wav\" ]");
	    printWriter.println("then");
	    printWriter.println("rm .wikitAudio.wav");
	    printWriter.println("fi");
	    
	    printWriter.println("if [ -f \".wikitResultFile.txt\" ]");
	    printWriter.println("then");
	    printWriter.println("rm .wikitResultFile.txt");
	    printWriter.println("fi");
	    
	    printWriter.close();

	    return Script;
	}
	
	public class backgroundClean extends Task<Void>{

		@Override
		protected Void call() throws Exception {
			File tempScript = tempCleanScript();
		    
		    try {
		    	ProcessBuilder cleanProcessBuilder = new ProcessBuilder("bash", tempScript.toString());
		    	cleanProcessBuilder.inheritIO();
		        Process cleanProcess = cleanProcessBuilder.start();
		        cleanProcess.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
		        tempScript.delete();
		    }
			return null;
		}
		
	}
	
	@FXML
	private void handleMainMenuButton(ActionEvent event) throws IOException{//Loads main menu scene.
		
		Thread cleanThread = new Thread(new backgroundClean());
		cleanThread.start();
		
		FXMLLoader MenuLoader = new FXMLLoader();
		MenuLoader.setLocation(this.getClass().getResource("Menu.fxml"));
		Parent List_Parent = MenuLoader.load();
		Scene scene = new Scene(List_Parent);
		Stage WikiMediaApp_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		WikiMediaApp_Stage.setScene(scene);
		WikiMediaApp_Stage.show();
	}
}
