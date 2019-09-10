


import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MenuController {


	@FXML
	private void handleCreateButton(ActionEvent event) throws IOException {
		FXMLLoader CreateLoader = new FXMLLoader();
		CreateLoader.setLocation(this.getClass().getResource("Creations.fxml"));
		Parent List_Parent = CreateLoader.load();
		Scene scene = new Scene(List_Parent);
		Stage WikiMediaApp_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		WikiMediaApp_Stage.setScene(scene);
		WikiMediaApp_Stage.show();
	}
	

	@FXML
	private void handleListButton(ActionEvent event) throws IOException{
		
		FXMLLoader ListLoader = new FXMLLoader();
		ListLoader.setLocation(this.getClass().getResource("List.fxml"));
		Parent List_Parent = ListLoader.load();
		Scene scene = new Scene(List_Parent);
		Stage WikiMediaApp_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		WikiMediaApp_Stage.setScene(scene);
		WikiMediaApp_Stage.show();
	}
	
	@FXML
	private void handleViewPlayButton(ActionEvent event) throws IOException{
		
		FXMLLoader ListLoader = new FXMLLoader();
		ListLoader.setLocation(this.getClass().getResource("ViewPlay.fxml"));
		Parent List_Parent = ListLoader.load();
		Scene scene = new Scene(List_Parent);
		Stage WikiMediaApp_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		WikiMediaApp_Stage.setScene(scene);
		WikiMediaApp_Stage.show();
	}
	
	@FXML
	private void handleDeleteButton(ActionEvent event) throws IOException{
		
		FXMLLoader ListLoader = new FXMLLoader();
		ListLoader.setLocation(this.getClass().getResource("Delete.fxml"));
		Parent List_Parent = ListLoader.load();
		Scene scene = new Scene(List_Parent);
		Stage WikiMediaApp_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		WikiMediaApp_Stage.setScene(scene);
		WikiMediaApp_Stage.show();
	}
	
	@FXML
	private void handleQuitButton(){
		
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			Platform.exit();
			System.exit(0);
		}		
	}
}
