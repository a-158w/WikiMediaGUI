import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ViewPlayController implements Initializable{
	
	
	@FXML
	private TextArea ListTextArea;
	@FXML
	private TextField PlaySelectTextField;
	@FXML
	private Button PlayButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {//Automatically populates list.
		
		ProcessBuilder LSProcessBuilder = new ProcessBuilder("/bin/bash", "-c", "ls creations | sort | cut -f 1 -d '.' | nl");//Builds bash process builder
		try {
			Process LSProcess = LSProcessBuilder.start();//Process starts bash process builder.
			InputStream stdout = LSProcess.getInputStream();
			BufferedReader bufferStdout = new BufferedReader(new InputStreamReader(stdout));
			LSProcess.waitFor();//Waits for process (ls command etc.) to finish.
			File file = new File("creations");//Checks if creations directory is empty.
			if (file.list().length!=0) {
				String line = null;//Set up temp line variable.
				while((line = bufferStdout.readLine()) !=null) {//Block just keeps appending to the txtOutput (of type text area).
					ListTextArea.appendText("\n"+line);
				}
				ListTextArea.appendText("\n\n");
			}else {//Creates error dialogue if directory is empty.
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error encountered");
				alert.setHeaderText("Directory empty");
				alert.setContentText("No creations have been made yet. \nGo back to main menu and press create.");

				alert.showAndWait();
				PlayButton.setDisable(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void handlePlayButton() {
		int numberOfFiles = new File("creations").listFiles().length;
		if (Integer.parseInt(PlaySelectTextField.getText()) > 0 && Integer.parseInt(PlaySelectTextField.getText()) <= numberOfFiles) {
			Thread thread = new Thread(new backgroundPlay());
			thread.start();
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error encountered");
			alert.setHeaderText("Not a valid choice");
			alert.setContentText("Please choose a valid number.");

			alert.showAndWait();
		}
	}
	
	public class backgroundPlay extends Task<Void>{

		@Override
		protected Void call() throws Exception {
		
			File tempScript = tempPlayScript(PlaySelectTextField.getText());

		    try {
		        ProcessBuilder createProcessBuilder = new ProcessBuilder("bash", tempScript.toString());
		        createProcessBuilder.inheritIO();
		        Process wikitProcess = createProcessBuilder.start();
		        wikitProcess.waitFor();
		    } finally {
		    	PlaySelectTextField.clear();
		        tempScript.delete();
		    }
			return null;
		}
	}
	
	public File tempPlayScript(String creation_number) throws IOException {//Creates a temporary bash script that plays selected video.
	    File Script = File.createTempFile("WikitScript", null);

	    Writer streamWriter = new OutputStreamWriter(new FileOutputStream(Script));
	    PrintWriter printWriter = new PrintWriter(streamWriter);

	    printWriter.println("#!/bin/bash");
	    printWriter.println("creation_to_play=\"\"");
	    printWriter.println("creation_to_play=`ls creations | sort | head -n "+creation_number+" | tail -n -1`");
	    printWriter.println("ffplay -autoexit creations/${creation_to_play} &> /dev/null");

	    printWriter.close();

	    return Script;
	}
	
	@FXML
	private void handleMainMenuButton(ActionEvent event) throws IOException{
		
		FXMLLoader MenuLoader = new FXMLLoader();
		MenuLoader.setLocation(this.getClass().getResource("Menu.fxml"));
		Parent List_Parent = MenuLoader.load();
		Scene scene = new Scene(List_Parent);
		Stage WikiMediaApp_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		WikiMediaApp_Stage.setScene(scene);
		WikiMediaApp_Stage.show();
	}
}