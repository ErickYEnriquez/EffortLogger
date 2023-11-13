package application;
import java.io.IOException;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

// controls the actual planning poker session and handles transition to end phase of planning poker
public class PlanningPokerController {

	private Scene scene;
	private Stage stage;
	private Parent root;

	// shared data throughout the application
	private String sessionName;
	private int playerNum;
	private String connectionString;
	private MongoDatabase db;
	private MongoCollection<Document> col;
	private MongoCollection<Document> userCol;
	private MongoClient mongoClient;
	private Login loginSystem;

	// UI elements
	@FXML
	private Label projectLabel, issueLabel, statusLabel, statsLabel;
	@FXML
	private Button submit, quickLook;
	@FXML
	private ChoiceBox<String> cardBox;
	@FXML
	private Rectangle rectangle1, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6, rectangle7, rectangle8, rectangle9, rectangle10;
	@FXML
	private Label  name1, name2, name3, name4, name5, name6, name7, name8, name9, name10;
	@FXML
	private Label vote1, vote2, vote3, vote4, vote5, vote6, vote7, vote8, vote9;
	@FXML
	private Label vote10;
	@FXML
	private TextArea dbInfo;

	ObservableList<String> cardList = FXCollections.observableArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Pass");

	// non-UI elements
	private String[] issueList;
	private Rectangle[] rectangleList;
	private int average, low, high;
	private String[] nameList;
	private Label[] labelList;
	private Label[] voteList;
	private int[] numList;
	private int currIssue;
	private int currPlayer = 0, prevPlayer;


	// poker setup
	public void setScene(String projectName, String[] issuelist, int playerNum)
	{
		projectLabel.setText(projectName);
		issueLabel.setText("Current Issue: " + issueList[0]);

		labelList = new Label[] {name1, name2, name3, name4, name5, name6, name7, name8, name9, name10};
		nameList = new String[] {Login.getUsername(), "Walter", "Gojo", "Power", "Colt", "Gibraltar", "Jack Cooper", "Shinji", "Kobeni", "Jesse"};
		rectangleList = new Rectangle[] {rectangle1, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6, rectangle7, rectangle8, rectangle9, rectangle10};
		numList = new int[playerNum];
		voteList = new Label[] {vote1, vote2, vote3, vote4, vote5, vote6, vote7, vote8, vote9, vote10};
		for (int i = 0; i < playerNum; i++)
		{
			rectangleList[i].setOpacity(1);
			labelList[i].setText(nameList[i]);
		}
		rectangleList[0].setFill(Color.GREEN);
		intialize();
		prevPlayer = currPlayer;
		currIssue = 0;
	}

	// NEEDS SOME TOUCHUP WORK???????????
	// card submission plus main planning poker logic
	public void submitCard(ActionEvent event) throws IOException
	{
		prevPlayer = currPlayer;
		currPlayer++;
		// handles what happens if we have reached issueList max
		if (issueList.length == currIssue)
		{
			//
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EffortLoggerMainUI.fxml"));
			root = fxmlLoader.load();
			EffortLoggerMainUI effortLoggerMainUIController = fxmlLoader.getController();
			effortLoggerMainUIController.recieveTransferedItems(connectionString, db, col, userCol, mongoClient, loginSystem);

			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setTitle("EFfortLogger Main Menu");
			stage.setScene(scene);
			stage.show();

			System.out.println("Time to mvoe on!");

			// implement code to move back to main menu
			//
		}
		// handles normal planning poker play before players are done voting
		if (currPlayer <= playerNum)
		{
			submit.setText("Submit Vote");
			statsLabel.setText("");
			issueLabel.setText("Current Issue: " + issueList[currIssue]);
			// increment player to keep track of whose turn it is
			if (currPlayer == 0)
				statusLabel.setText("It's Your Turn to Vote!");
			else
				statusLabel.setText("Waiting for other players to vote");

			rectangleList[prevPlayer].setFill(Color.RED);
			numList[currPlayer-1] = Integer.parseInt(cardBox.getValue());
		}
		// if no more players need to vote, change issue, reset player count, and display statistics for the issue
		if (currPlayer == playerNum)
		{
			statusLabel.setText("Finished Voting!");
			submit.setText("Continue");
			low = findMin(numList);
			high = findMax(numList);
			average = findAverage(numList);
			statsLabel.setText("Low: " + low + "  High: " + high + "  Average: " + average);
			showVotes(voteList);
			resetRectangle(rectangleList);
			currIssue++;
			// if we have run out of current issues (everyone is done voting)
		}
		if (currPlayer == playerNum+1)
		{
			statsLabel.setText("");
			currPlayer = 0;
			resetRectangle(rectangleList);
			clearVotes(voteList);
			submit.setText("Submit Vote");
			System.out.println(currIssue);
			issueLabel.setText("Current Issue: " + issueList[currIssue]);
		}

		// update list
		rectangleList[currPlayer].setFill(Color.GREEN);
	}

	// quick-look for accessing relevant log data and such
	public void quickLook(ActionEvent event) throws IOException
	{
		String gatheredInfo = "LOG DATA: \n";
		// grab either session name or current issue and use that to search through database for required logs
		// only print the necessary and relevant logs to the user. Only print part of log that would be helpful?
		// on button press, should clear the entire text area and update with new search inputs
		MongoCollection<Document> logCol = db.getCollection("logs");
		MongoCollection<Document> storyCol = db.getCollection("stories");

		Document currentDoc = new Document();

		Bson sessionLogFilter = Filters.text(sessionName);
		Bson issueLogFilter = Filters.text(issueList[currIssue]);
		Bson sessionStoryFilter = Filters.text(sessionName);
		Bson issueStoryFilter = Filters.text(issueList[currIssue]);

		FindIterable<Document> sessionLogDocs = logCol.find(sessionLogFilter);
		FindIterable<Document> issueLogDocs = logCol.find(issueLogFilter);
		FindIterable<Document> sessionStoryDocs = storyCol.find(sessionStoryFilter);
		FindIterable<Document> issueStoryDocs = storyCol.find(issueStoryFilter);

		MongoCursor<Document> results = sessionLogDocs.iterator();		// add data
		while(results.hasNext())
		{
			currentDoc = results.next();
			gatheredInfo += currentDoc.get("details").toString() + "  " + currentDoc.get("TimeStamp").toString() + "\n";
		}
		System.out.println(gatheredInfo);
		results = issueLogDocs.iterator();
		while(results.hasNext())
		{
			currentDoc = results.next();
			gatheredInfo += currentDoc.get("details").toString() + "   " + currentDoc.get("TimeStamp").toString() + "\n";
		}
		System.out.println(gatheredInfo);
		gatheredInfo += "STORY DATA: \n";
		results = sessionStoryDocs.iterator();
		while(results.hasNext())
		{
			currentDoc = results.next();
			gatheredInfo += currentDoc.get("title").toString() + "    " + currentDoc.get("details").toString() + "   " + currentDoc.get("TimeStamp").toString() + "\n";
		}
		System.out.println(gatheredInfo);
		results = issueStoryDocs.iterator();
		while(results.hasNext())
		{
			currentDoc = results.next();
			gatheredInfo += currentDoc.get("title").toString() + "    " + currentDoc.get("details").toString() + "    " + currentDoc.get("TimeStamp").toString() + "\n";
		}

		dbInfo.setText(gatheredInfo);
	}

	// something for a choicebox idk but basically grabs the user card that they want to submit
	public void intialize ()
	{
		cardBox.setValue("0");
		cardBox.setItems(cardList);
	}

	// finds smallest value in given array
	public int findMin(int[] arr)
	{
		int min = arr[0];
		for (int element : arr) {
			if (element < min)
				min = element;
		}
		return min;
	}

	// finds largest value in given array
	public int findMax(int[] arr)
	{
		int max = arr[0];
		for (int element : arr) {
			if (element > max)
				max = element;
		}
		return max;
	}

	// finds average value in array (rounded)
	public int findAverage(int[] arr)
	{
		int sum = 0;
		for (int element : arr) {
			sum +=element;
		}
		return sum/arr.length;
	}

	// resets rectangle colors
	public void resetRectangle (Rectangle[] arr)
	{
		for (Rectangle element : arr) {
			element.setFill(Color.BEIGE);
		}
	}

	// displays votes of all players
	public void showVotes (Label[] arr)
	{
		for (int i = 0; i < playerNum; i++)
		{
			arr[i].setText(numList[i] + "");
		}
	}

	// clear votes on player cards
	public void clearVotes (Label[] arr)
	{
		for (int i = 0; i < playerNum; i++)
		{
			arr[i].setText("");
		}
	}

	// receives data from other controller files
	public void recieveTransferedItems(String connectionString, MongoDatabase db, MongoCollection<Document> col, MongoCollection<Document> userCol, MongoClient mongoClient, Login loginSystem, String sessionName, int playerNum, String[] issueList) {
		this.sessionName = sessionName;
		this.playerNum = playerNum;
		this.connectionString = connectionString;
		this.db = db;
		this.col = col;
		this.userCol = userCol;
		this.mongoClient = mongoClient;
		this.loginSystem = loginSystem;
		this.issueList = issueList;
	}
}
