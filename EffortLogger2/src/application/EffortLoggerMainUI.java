package application;

import java.io.IOException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class EffortLoggerMainUI{

	private Scene scene;
	private Stage stage;
	private Parent root;



	private String connectionString;
	private MongoDatabase db;
	private MongoCollection<Document> col;
	private MongoCollection<Document> userCol;
	private MongoClient mongoClient;
	private Login loginSystem;

	private Button startPP;



	//This method is called by the effortLoggerMainUIController in LoginController.java
	//Allows us to send variables we want to send to other controller files
	public void recieveTransferedItems(String connectionString, MongoDatabase db, MongoCollection<Document> col, MongoCollection<Document> userCol, MongoClient mongoClient, Login loginSystem) {
		this.connectionString = connectionString;
		this.db = db;
		this.col = col;
		this.userCol = userCol;
		this.mongoClient = mongoClient;
		this.loginSystem = loginSystem;
		//This System.out.println prints out a piece of the transfered data to make sure the transfer worked
		System.out.println(connectionString);
		System.out.println("We transfered over this login information: " + Login.getUsername());
	}

	public void startPoker(ActionEvent event) throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlanningPokerMainMenu.fxml"));

		root = fxmlLoader.load();
		PlanningPokerMainMenuController planningPokerMainMenuController = fxmlLoader.getController();
		planningPokerMainMenuController.recieveTransferedItems(connectionString, db, col, userCol, mongoClient, loginSystem);

		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setTitle("Planning Poker Set Up");
		stage.setScene(scene);
		stage.show();

	}


}