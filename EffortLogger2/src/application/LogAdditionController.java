package application;

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

import static com.mongodb.client.model.Filters.eq;

import java.security.Timestamp;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Sorts.descending;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Date;

public class LogAdditionController {
	private String connectionString;
	private MongoDatabase db;
	private MongoCollection<Document> col;
	private MongoCollection<Document> userCol;
	private MongoClient mongoClient;
	private Login loginSystem;
	
	private Parent root;
	private Scene scene;
	private Stage stage;
	
	private Date startTime;
	
	private int userId;
	private int projectId;
	
	@FXML
	private TextField description;
	
	@FXML
	private ChoiceBox<String> storyChoice;
	
	@FXML
	private Label clockStatus;
	
	public void recieveTransferedItems(String connectionString, MongoDatabase db, MongoCollection<Document> col, MongoCollection<Document> userCol, MongoClient mongoClient, Login loginSystem)
	{
		this.connectionString = connectionString;
		this.db = db;
		this.col = col;
		this.userCol = userCol;
		this.mongoClient = mongoClient;
		this.loginSystem = loginSystem;
		projectId = 1;
		
		FindIterable<Document> filterUsers = userCol.find(eq("username", loginSystem.getUsername()));
		Document targetObject = filterUsers.first();
		userId = Integer.parseInt(targetObject.get("userID").toString());
		
		MongoCollection<Document> stories = db.getCollection("stories");
		FindIterable<Document> iterable = stories.find();
    	MongoCursor<Document> results = iterable.iterator();
		while (results.hasNext())
    	{
    		storyChoice.getItems().add(results.next().get("title").toString());
    	}
	}
	
	public void storeTime()
	{
		startTime = new Date();
		System.out.println(startTime);
		clockStatus.setText("Clock is Running");
		clockStatus.setTextFill(Color.GREEN);
	}
	
	public void storeLog()
	{
		Main object = new Main();
		String desc = description.getText();
		String story = storyChoice.getValue();
		
		FindIterable<Document> found = object.phraseSearch("stories", story, db);
		MongoCursor<Document> result = found.iterator();
		int storyId = Integer.parseInt(result.next().get("story-id").toString());
		
		Main.insertLog(userId, projectId, storyId, desc, startTime, db);
		description.clear();
		storyChoice.valueProperty().set(null);
		startTime = null;
		clockStatus.setText("Clock is Stopped");
		clockStatus.setTextFill(Color.RED);
	}
	
	public void returnHome(ActionEvent event) throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EffortLoggerMainUI.fxml"));
		
		System.out.println("Switching to the Main Page");
		root = fxmlLoader.load();
		EffortLoggerMainUI effortLoggerMainUI = fxmlLoader.getController();
		effortLoggerMainUI.recieveTransferedItems(connectionString, db, col, userCol, mongoClient, loginSystem);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setTitle("Add Log");
		stage.setScene(scene);
		stage.show();
	}
}