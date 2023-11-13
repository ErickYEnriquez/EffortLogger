package application;

import java.io.IOException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class LoginController{
	private Scene scene;
	private Stage stage;
	private Parent root;

	@FXML
	private TextField username; //these variables have to have the same name as the ID in fxml file
	@FXML
	private PasswordField password;
	@FXML
	private Button submit;
	@FXML

	private Label failLabel;



	//built this method assuming we have getters and setters in login java
	public void submitLogin(ActionEvent event) throws IOException{
		//if you want to load another page do it here ex: FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(file name))
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EffortLoggerMainUI.fxml"));
		//feel free to change how I call this login you can probably make a new login object

		//Setting up Mongo data
		String connectionString = "mongodb+srv://ndlovelace13:7Cpa4yubfjj7aPql@effortlogger.zfgzhfr.mongodb.net/?retryWrites=true&w=majority";
    	MongoDatabase db;
    	MongoCollection<Document> col;
    	MongoCollection<Document> userCol;
		MongoClient mongoClient = MongoClients.create(connectionString);
		db = mongoClient.getDatabase("Effortlogs");
		col = db.getCollection("logs");
        userCol = db.getCollection("users");

        // Create a login system object to store login data and sent it to other javafx scenes
        Login loginSystem = new Login();

		String loginUsername = username.getText();
		String loginPassword = password.getText();
		// do security check
		System.out.println("This is the username: " + loginUsername);
		System.out.println("This is the password: " + loginPassword);
		Login.setPassword(loginPassword);
		Login.setUsername(loginUsername);

		// This if statement checks to see if the login information is correct or not
		// A username and password in the database currently you can test is
		// Username: jlkim1  Password: cats12
		if (loginSystem.findUser(loginUsername, loginPassword, db)) {
				// Jump over to the next scene, likely main effort logger UI
			System.out.println("Account found, moving to main menu.");
			root = fxmlLoader.load();

			//These two lines of code send data over to the next fxml controller file
			EffortLoggerMainUI effortLoggerMainUIController = fxmlLoader.getController();
			effortLoggerMainUIController.recieveTransferedItems(connectionString, db, col, userCol, mongoClient, loginSystem);

			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setTitle("EffortLogger Main Menu");
			stage.setScene(scene);
			stage.show();

		}
		else {
			System.out.println("Account not found");
			// .clear() removes all of the text that's currently present in the text field
			username.clear();
			password.clear();
			failLabel.setText("Password not found, please try again!");
		}
		//if we can go to new page use this code
		/* stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		 * scene = new Scene(fxmlLoader.load(),int, int) these ints are the x and y axis.
		 * stage.setTitle("") choose a title name
		 * <name of new controller class> control = fxmlLoader.getController
		 * can call controller functions from here
		 * ex:
		 * control.displayLogin() if there was a displayLogin() function in the other controller class
		 * stage.setScene(scene)
		 * stage.show()
		 */
	}

}