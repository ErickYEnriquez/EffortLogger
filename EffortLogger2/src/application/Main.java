package application;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.set;

import java.io.IOException;
import java.util.Date;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/*
 * EffortLogger V2 Risk Reduction Prototype
 * Written by: Noah Lovelace
 * Focus: Input Validation
 */


public class Main extends Application {

	private boolean authorize = false;


    public static void main(String[] args) {
        launch(args);
    }
    @Override
	public void start(Stage stage) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
    	LoginController controller = fxmlLoader.getController();
    	Parent fxml = fxmlLoader.load();
    	Scene scene = new Scene(fxml,900,600);
    	stage.setTitle("Login");
    	stage.setScene(scene);
    	stage.show();
    }
    

    //necessary function for making certain tags searchable by phrase
    //if you want to add more tags that store strings, add them to this so they can be properly searched
    public void searchInit(MongoDatabase db)
    {
    	MongoCollection<Document> logs = db.getCollection("logs");
    	MongoCollection<Document> stories = db.getCollection("stories");
    	Document test = new Document().append("details", 1);
//    	logs.createIndex(test);
//    	stories.createIndex(Indexes.text("title"));
//    	stories.dropIndex("details_1_title_1");
//    	test = new Document().append("details", 1);
    	MongoCursor<Document> results = stories.listIndexes().iterator();
    	while (results.hasNext())
    	{
    		System.out.println(results.next());
    	}
    }

    //insert into logs collection in database we have to manually pass the db feel free to add more tags
    //to get the amounts of logs use col.count() and add 1
    // example use: insertLog("Cole", 1, "description of user story", col.countDocuments() + 1, db)
    public static void insertLog(long userId, int projectId, int storyId, String details, Date startTime, MongoDatabase db)
    {

    	MongoCollection<Document> col = db.getCollection("logs");
    	//return the most recent item in the collection
    	int logId = newId("logs", "log-id", db);
        Document test = new Document("user-id", userId)
        		.append("project-id", projectId)
        		.append("story-id", storyId)
        		.append("log-id", logId)
        		.append("details", details)
        		.append("start-time", startTime)
        		.append("end-time", new Date());
        col.insertOne(test);
        System.out.println("Log successfully added");
    }
    //functions identically to the insertLog function, just services the story collection instead
    //as the story class has different values to be stored
    public static void insertStory(long userId, int projectId, String title, String description, MongoDatabase db)
    {

    	MongoCollection<Document> col = db.getCollection("stories");
    	//return a newId based on the last id in the collection
    	int storyId = newId("stories", "story-id", db);
        Document test = new Document("user-id",userId)
        		.append("project-id", projectId)
        		.append("story-id", storyId)
        		.append("title", title)
        		.append("details", description)
        		.append("TimeStamp", new java.util.Date());
        col.insertOne(test);
        System.out.println("Story successfully added");
    }

    //helps find every document in the database that matches a filter and data
    //this method is for searching numerical values, the data variable must be an integer
    //pass the database from the start method
    //returns an iterable that has all the documents found
    public FindIterable<Document> numSearch(String colName, String filter, int data, MongoDatabase db)
    {
    	MongoCollection<Document> col = db.getCollection(colName);
    	FindIterable<Document> iterable;
    	iterable = col.find(eq(filter, data));
    	if (iterable.first() == null)
    	{
    		System.out.println("The log(s) you are searching for doesn't exist");
    	}
    	return iterable;
    }

    //helps find every document in the database that matches a filter and data
    //If you are looking for a user who's name is AJ then filter variable would = user
    //data would = AJ
    //pass the database from the start method
    //returns an iterable that has all the documents found
    public FindIterable<Document> phraseSearch(String colName, String phrase, MongoDatabase db)
    {
    	MongoCollection<Document> col = db.getCollection(colName);
    	FindIterable<Document> iterable;
    	Bson filter = Filters.text(phrase);
    	iterable = col.find(filter);
    	if (iterable.first() == null)
    	{
    		System.out.println("The log(s) you are searching for doesn't exist");
    	}
    	return iterable;
    }

    //prints all entries from the specified collection in json format
    public void printCol(String colName, MongoDatabase db)
    {
    	MongoCollection<Document> col = db.getCollection(colName);
    	FindIterable<Document> iterable = col.find();
    	MongoCursor<Document> results = iterable.iterator();
    	while(results.hasNext())
        {
        	System.out.println(results.next().toJson());

        }
    	return;
    }

    //method finding the last of a certain collection
    public static FindIterable<Document> findLast(String colName, String sortParam, MongoDatabase db)
    {
    	MongoCollection<Document> col = db.getCollection(colName);
    	FindIterable<Document> iterable;
    	iterable = col.find().limit(1).sort(descending(sortParam));
    	return iterable;
    }

    //method that will return a new id number based off the last id of a certain collection
    public static int newId(String colName, String sortParam, MongoDatabase db)
    {
    	FindIterable<Document> bruh = findLast(colName, sortParam, db);
		MongoCursor<Document> results = bruh.iterator();
		int newId = Integer.parseInt(results.next().get(sortParam).toString());
		newId++;
		return newId;
    }


    //print all logs that were found and stored in an iterable, pairs well with the search and find functions
    public void print(FindIterable<Document> iterable) {
    	MongoCursor<Document> results = iterable.iterator();
        while(results.hasNext())
        {
        	System.out.println(results.next().toJson());
        }
    }

    //finds the log we want to delete by log number then deletes it
    public void deleteEntry(int num, String colName, String param, MongoDatabase db){
    	MongoCollection<Document> col = db.getCollection(colName);
    	Document deleted = col.findOneAndDelete(eq(param, num));
    	if (deleted == null)
    	{
    		System.out.println("There was no entry found matching that id - nothing deleted");
    	}
    	else
    	{
    		System.out.println("The following Document was deleted from " + colName);
    		System.out.println(deleted.toJson());
    	}
    }

    //finds log by log number then sets attribute you decided in update string with data
    public void update(String colName, String param, String idParam, int idNum, String updatedPhrase, int updatedNum, Date updatedTime, MongoDatabase db) {
    	MongoCollection<Document> col = db.getCollection(colName);
    	if(updatedPhrase == null)
    	{
    		col.findOneAndUpdate(eq(idParam, idNum), set(param, updatedNum));
    		col.findOneAndUpdate(eq(idParam, idNum), set("TimeStamp", updatedTime));
    	}
    	else {
    		col.findOneAndUpdate(eq(idParam, idNum), set(param, updatedPhrase));
    		col.findOneAndUpdate(eq(idParam, idNum), set("TimeStamp", updatedTime));
    	}
    	System.out.println("We do a little updating");
    }
}
