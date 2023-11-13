package application;
import java.io.IOException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlanningPokerMainMenuController {
		private Scene scene;
		private Stage stage;
		private Parent root;

		@FXML
		private TextField sessionField;
		@FXML
		private TextField playerField;
		@FXML
		private Button startPp;
		@FXML
		private Label errorLabel;
		@FXML
		private TextField issues;




		private String connectionString;
		private MongoDatabase db;
		private MongoCollection<Document> col;
		private MongoCollection<Document> userCol;
		private MongoClient mongoClient;
		private Login loginSystem;
		private boolean valid = false;
		private String issuesGotten;
		private String[] issueList;

		public void startPoker(ActionEvent event) throws IOException{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlanningPokerUI.fxml"));

			if (sessionField == null)
			{
				errorLabel.setText("Title of Session cannot be blank");
				valid = false;
			}

			String sessionName = sessionField.getText();

			if (playerField == null)
			{
				errorLabel.setText("Number of players cannot be blank");
				valid = false;
			}

			int playerNum = Integer.parseInt(playerField.getText());

			if (playerNum <= 1 || playerNum > 10)
			{
				errorLabel.setText("PlayerNum not valid, please re-enter");
				playerField.clear();
				valid = false;
			}
			if (sessionName.length() > 30)
			{
				errorLabel.setText("Session name is too long. Please keep it to underneath 30 characters");
				sessionField.clear();
				valid = false;
			}
			if (issues == null)
			{
				errorLabel.setText("Issues cannot be blank. Please enter at least one issue");
				valid = false;
			}

			issuesGotten = issues.getText();
			issueList = issuesGotten.split(",");

			for (String element : issueList)
				System.out.println(element);

			valid = true;
			if (valid)
			{

				root = fxmlLoader.load();

				PlanningPokerController planningPokerController = fxmlLoader.getController();
				planningPokerController.recieveTransferedItems(connectionString, db, col, userCol, mongoClient, loginSystem, sessionName, playerNum, issueList);
				planningPokerController.setScene(sessionName,  issueList, playerNum);


				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setTitle(sessionName);
				stage.setScene(scene);
				stage.show();
			}
		}

		public void recieveTransferedItems(String connectionString, MongoDatabase db, MongoCollection<Document> col, MongoCollection<Document> userCol, MongoClient mongoClient, Login loginSystem) {
			this.connectionString = connectionString;
			this.db = db;
			this.col = col;
			this.userCol = userCol;
			this.mongoClient = mongoClient;
			this.loginSystem = loginSystem;
		}


}
