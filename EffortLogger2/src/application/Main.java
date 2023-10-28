package application;
	
import java.util.Scanner;
import java.util.InputMismatchException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

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


public class Main extends Application {
	
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage primaryStage) {
    	//connection string replace username with your username and password with your password
    	String connectionString = "mongodb+srv://ndlovelace13:7Cpa4yubfjj7aPql@effortlogger.zfgzhfr.mongodb.net/?retryWrites=true&w=majority";
    	MongoDatabase db;
    	MongoCollection<Document> col;
    	
    	//basic user stuff that James/Cole can link to login credentials later on
    	String currentUser = "Hugh Mungus";
    	int userId = 420;
    	int userSecurity = 1;
    	
    	int project = 1;
    	
    	
    	
    	//stuff needed for the big while loop
    	String continueChoice = "y";
    	int action = 0;
    	Scanner in = new Scanner(System.in);
    	//trying to connect the driver 
    	try (MongoClient mongoClient = MongoClients.create(connectionString)) {
           //this second try is for anything mongo related
    		try {
    			//finding the database and collection
                db = mongoClient.getDatabase("Effortlogs");
                col = db.getCollection("logs");
                
                //initializes the indexes necessary for phrase searching
            	searchInit(db);
                
                System.out.println("Welcome to the EffortLogger V2 Prototype!\n");
                //Main EffortLogger Loop (FOR NOW)
                while (continueChoice.equals("y"))
                {
                	System.out.println("Please select an action from the following list:\n");
                	System.out.println("1 = Add a Story or Log");
                	System.out.println("2 = Edit an Existing Story or Log");
                	System.out.println("3 = Delete an Existing Story or Log");
                	System.out.println("4 = Search for a Story or Log");
                	System.out.println("5 = Print all Stories or Logs\n");
                	
                	
                	Boolean confirm = false;
                	
                	while (!confirm)
                	{
                		try
                		{
                			action = in.nextInt();
                			if (action > 0 && action < 6)
                			{
                				confirm = true;
                			}
                			else
                			{
                				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
                			}
                			
                		}
                		catch (InputMismatchException e)
                		{
                			System.out.println("The input must be an integer, please enter a number from the above list:\n");
                			in.nextLine();
                		}
                	}
                	//vars necessary for decision-making
                	String slChoice;
                	String title;
                	String details;
                	
                	switch (action)
                	{
                		case 1: //INSERT
                			System.out.println("adding a story or log!");
                			
                			System.out.println("Would you like to add a story or log? Enter (S/L)\n");
                			slChoice = in.next().toLowerCase();
                			while (!slChoice.equals("s") && !slChoice.equals("l"))
                        	{
                        		System.out.println("Your input is invalid, please enter S/L");
                        		slChoice = in.next().toLowerCase();
                        	}
                			if (slChoice.equals("s"))
                			{
                				//slChoice = "stories";
                				in.nextLine();
                				System.out.println("What title would you like this user story to have?\n");
                				title = in.nextLine();
                				System.out.println("What description would you like to add to the story?\n");
                				details = in.nextLine();
                				insertStory(userId, project, title, details, db);
                			}
                			else //if (slChoice.equals("l"))
                			{
                				//slChoice = "logs";
                				in.nextLine();
                				System.out.println("What details would you like to add to the log?\n");
                				details = in.nextLine();
                				insertLog(userId, project, 1, details, db);
                			}

                			
                			break;
                		case 2: //EDIT
                			System.out.println("editing a story or log!");
                			System.out.println("Would you like to edit a story or log? Enter (S/L)\n");
                			slChoice = in.next().toLowerCase();
                			while (!slChoice.equals("s") && !slChoice.equals("l"))
                        	{
                        		System.out.println("Your input is invalid, please enter S/L");
                        		slChoice = in.next().toLowerCase();
                        	}
                			
                			String param = "";
                			if (slChoice.equals("s"))
                			{
                				slChoice = "stories";
                				param = "story-id";
                			}
                			else if (slChoice.equals("l"))
                			{
                				slChoice = "logs";
                				param = "log-id";
                			}
                			System.out.println("Please enter the id number of the entry you would like to edit:");
                			int idNum = 0;
                			confirm = false;
                        	while (!confirm)
                        	{
                        		try
                        		{
                        			idNum = in.nextInt();
                        			if (idNum > 0)
                        			{
                        				confirm = true;
                        			}
                        			else
                        			{
                        				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
                        			}
                        			
                        		}
                        		catch (InputMismatchException e)
                        		{
                        			System.out.println("The input must be an integer, please enter a number from the above list:\n");
                        			in.nextLine();
                        		}
                        	}
                        	System.out.println("Now printing the current requested entry\n");
                        	FindIterable<Document> iterable = numSearch(slChoice, param, idNum, db);
                        	if (iterable.first() != null)
                        	{
                        		print(iterable);
                        		
                        		if (slChoice.equals("logs"))
                        		{
                        			System.out.println("Starting editing process for logs");
                        			System.out.println("What key would you like to change?");
                        			System.out.println("For project-id, enter P");
                        			System.out.println("For story-id, enter S");
                        			System.out.println("For details, enter D");
                					confirm = false;
                					String editChoice = in.next().toLowerCase();
                					while (!confirm)
                					{
                						switch (editChoice)
                						{
                    						case "p":
                    							editChoice = "project-id";
                    							confirm = true;
                    							break;
                    							
                    						case "s":
                    							editChoice = "story-id";
                    							confirm = true;
                    							break;
                    							
                    						case "d":
                    							editChoice = "details";
                    							confirm = true;
                    							break;
                    					
                							
                							default:
                								System.out.println("Your input is invalid, please enter P/S/D");
                                        		editChoice = in.next().toLowerCase();
                								break;
                						}
                					}
                					int updatedNum = 0;
                					String updatedPhrase = null;
                					if (editChoice.equals("details"))
                					{
                						in.nextLine();
                						System.out.println("Please enter the new details you would like this log to have");
                						updatedPhrase = in.nextLine();
                					}
                					else
                					{
                						System.out.println("Please enter the new " + editChoice + " you would like this log to have");
                            			confirm = false;
                                    	while (!confirm)
                                    	{
                                    		try
                                    		{
                                    			updatedNum = in.nextInt();
                                    			if (updatedNum > 0)
                                    			{
                                    				confirm = true;
                                    			}
                                    			else
                                    			{
                                    				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
                                    			}
                                    			
                                    		}
                                    		catch (InputMismatchException e)
                                    		{
                                    			System.out.println("The input must be an integer, please enter a number from the above list:\n");
                                    			in.nextLine();
                                    		}
                                    	}
                					}
                					update(slChoice, editChoice, param, idNum, updatedPhrase, updatedNum, db);
                					System.out.println("Now printing your updated entry");
                					print(numSearch(slChoice, param, idNum, db));
                					
                        		}
                        		else
                        		{
                        			System.out.println("Starting editing process for stories");
                        			System.out.println("What key would you like to change?");
                        			System.out.println("For project-id, enter P");
                        			System.out.println("For title, enter T");
                        			System.out.println("For details, enter D");
                        			
                					confirm = false;
                					String editChoice = in.next().toLowerCase();
                					while (!confirm)
                					{
                						switch (editChoice)
                						{
                    						case "p":
                    							editChoice = "project-id";
                    							confirm = true;
                    							break;
                    							
                    						case "t":
                    							editChoice = "title";
                    							confirm = true;
                    							break;
                    							
                    						case "d":
                    							editChoice = "details";
                    							confirm = true;
                    							break;
                    					
                							
                							default:
                								System.out.println("Your input is invalid, please enter P/T/D");
                                        		editChoice = in.next().toLowerCase();
                								break;
                						}
                					}
                					int updatedNum = 0;
                					String updatedPhrase = null;
                					if (editChoice.equals("details") || editChoice.equals("title"))
                					{
                						in.nextLine();
                						System.out.println("Please enter the new " + editChoice + " you would like this log to have");
                						updatedPhrase = in.nextLine();
                					}
                					else
                					{
                						System.out.println("Please enter the new " + editChoice + " you would like this log to have");
                            			confirm = false;
                                    	while (!confirm)
                                    	{
                                    		try
                                    		{
                                    			updatedNum = in.nextInt();
                                    			if (updatedNum > 0)
                                    			{
                                    				confirm = true;
                                    			}
                                    			else
                                    			{
                                    				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
                                    			}
                                    			
                                    		}
                                    		catch (InputMismatchException e)
                                    		{
                                    			System.out.println("The input must be an integer, please enter a number from the above list:\n");
                                    			in.nextLine();
                                    		}
                                    	}
                					}
                					update(slChoice, editChoice, param, idNum, updatedPhrase, updatedNum, db);
                					System.out.println("Now printing your updated entry");
                					print(numSearch(slChoice, param, idNum, db));
                        		}
                        	}
                			break;
                		case 3: //DELETE
                			System.out.println("deleting a story or log!");
                			System.out.println("Would you like to delete a story or log? Enter (S/L)\n");
                			slChoice = in.next().toLowerCase();
                			while (!slChoice.equals("s") && !slChoice.equals("l"))
                        	{
                        		System.out.println("Your input is invalid, please enter S/L");
                        		slChoice = in.next().toLowerCase();
                        	}
                			
                			param = "";
                			if (slChoice.equals("s"))
                			{
                				slChoice = "stories";
                				param = "story-id";
                			}
                			else if (slChoice.equals("l"))
                			{
                				slChoice = "logs";
                				param = "log-id";
                			}
                			
                			System.out.println("Please enter the id number of the entry you would like to remove:");
                			idNum = 0;
                			confirm = false;
                        	while (!confirm)
                        	{
                        		try
                        		{
                        			idNum = in.nextInt();
                        			if (idNum > 0)
                        			{
                        				confirm = true;
                        			}
                        			else
                        			{
                        				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
                        			}
                        			
                        		}
                        		catch (InputMismatchException e)
                        		{
                        			System.out.println("The input must be an integer, please enter a number from the above list:\n");
                        			in.nextLine();
                        		}
                        	}
                        	deleteEntry(idNum, slChoice, param, db);
                			break;
                		case 4: //SEARCH
                			System.out.println("searching for a story or log!");
                			System.out.println("Would you like to search for a story or log? Enter (S/L)\n");
                			slChoice = in.next().toLowerCase();
                			while (!slChoice.equals("s") && !slChoice.equals("l"))
                        	{
                        		System.out.println("Your input is invalid, please enter S/L");
                        		slChoice = in.next().toLowerCase();
                        	}
                			
                			if (slChoice.equals("s"))
                				slChoice = "stories";
                			else if (slChoice.equals("l"))
                				slChoice = "logs";
                			
                			System.out.println("Would you like to search by a number or a phrase? Enter (N/P)");
                			String npChoice = in.next().toLowerCase();
                			while (!npChoice.equals("n") && !npChoice.equals("p"))
                			{
                				System.out.println("Your input is invalid, please enter N/P");
                        		npChoice = in.next().toLowerCase();
                			}
                			if (npChoice.equals("n"))
                			{
            					System.out.println("Would you like to search by user, project, story, or log id? Enter (U/P/S/L)");
            					System.out.println("Disclaimer: you may not search by log-id if you are searching the story collection");
            					String idChoice = in.next().toLowerCase();
            					/*while (!idChoice.equals("u") && !idChoice.equals("p") && !idChoice.equals("s"))
                    			{
                    				System.out.println("Your input is invalid, please enter N/P");
                            		idChoice = in.next().toLowerCase();
                    			}*/
            					param = "";
            					confirm = false;
            					while (!confirm)
            					{
            						switch (idChoice)
            						{
                						case "u":
                							param = "user-id";
                							confirm = true;
                							break;
                						
                						case "p":
                							param = "project-id";
                							confirm = true;
                							break;
                							
                						case "s":
                							param = "story-id";
                							confirm = true;
                							break;
                						
                						case "l":
                							if (slChoice.equals("stories"))
                							{
                								System.out.println("The log-id parameter does not exist in the story collection. Please select another");
                								idChoice = in.next().toLowerCase();
                							}
                							else
                							{
                								param = "log-id";
                								confirm = true;
                							}
                							break;
            							
            							default:
            								System.out.println("Your input is invalid, please enter N/P");
                                    		idChoice = in.next().toLowerCase();
            								break;
            						}
            					}
            					
            					System.out.println("Please enter the id number of the entry you would like to search for:");
                    			idNum = 0;
                    			confirm = false;
                            	while (!confirm)
                            	{
                            		try
                            		{
                            			idNum = in.nextInt();
                            			if (idNum > 0)
                            			{
                            				confirm = true;
                            			}
                            			else
                            			{
                            				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
                            			}
                            			
                            		}
                            		catch (InputMismatchException e)
                            		{
                            			System.out.println("The input must be an integer, please enter a number from the above list:\n");
                            			in.nextLine();
                            		}
                            	}
            					print(numSearch(slChoice, param, idNum, db));
                			}
                			else
                			{
                				System.out.println("Please enter the phrases you would like to search for");
                				in.nextLine();
                				String phrase = in.nextLine();
                				print(phraseSearch(slChoice, phrase, db));
                			}
                			break;
                		case 5:
                			System.out.println("printing all story or log!");
                			
                			System.out.println("Would you like to print all stories or logs? Enter (S/L)\n");
                			slChoice = in.next().toLowerCase();
                			while (!slChoice.equals("s") && !slChoice.equals("l"))
                        	{
                        		System.out.println("Your input is invalid, please enter S/L");
                        		slChoice = in.next().toLowerCase();
                        	}
                			
                			if (slChoice.equals("s"))
                				slChoice = "stories";
                			else if (slChoice.equals("l"))
                				slChoice = "logs";

                			System.out.println("Now printing all entries from " + slChoice + " collection:\n");
                			printCol(slChoice, db);
                			
                			break;
                	}
                	
                	System.out.println("Would you like to continue using EffortLogger V2? (Y/N)");
                	continueChoice = in.next().toLowerCase();
                	while (!continueChoice.equals("y") && !continueChoice.equals("n"))
                	{
                		System.out.println("Your input is invalid, please enter Y/N");
                		continueChoice = in.next().toLowerCase();
                	}
                }
                in.close();
                
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    	
    }
    
    //necessary function for making certain tags searchable by phrase
    //if you want to add more tags that store strings, add them to this so they can be properly searched
    public void searchInit(MongoDatabase db)
    {
    	MongoCollection<Document> logs = db.getCollection("logs");
    	MongoCollection<Document> stories = db.getCollection("stories");
    	Document test = new Document().append("details", 1);
    	logs.createIndex(test);
    	//stories.createIndex(Indexes.text("title"));
    	//stories.dropIndex("details_1_title_1");
    	//test = new Document().append("details", 1);
    	/*MongoCursor<Document> results = stories.listIndexes().iterator();
    	while (results.hasNext())
    	{
    		System.out.println(results.next());
    	}*/
    }
    
    //insert into logs collection in database we have to manually pass the db feel free to add more tags
    //to get the amounts of logs use col.count() and add 1 
    // example use: insertLog("Cole", 1, "description of user story", col.countDocuments() + 1, db)
    public void insertLog(int userId, int projectId, int storyId, String details, MongoDatabase db) 
    {
    	
    	MongoCollection<Document> col = db.getCollection("logs");
    	//return the most recent item in the collection
    	int logId = newId("logs", "log-id", db);
        Document test = new Document("user-id", userId)
        		.append("project-id", projectId)
        		.append("story-id", storyId)
        		.append("log-id", logId)
        		.append("details", details)
        		.append("TimeStamp", new java.util.Date());
        col.insertOne(test);
        System.out.println("Log successfully added");
    }
    
    public void insertStory(int userId, int projectId, String title, String description, MongoDatabase db) 
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
    //If you are looking for a user who's name is AJ then filter variable would = user 
    //data would = AJ
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
    public FindIterable<Document> findLast(String colName, String sortParam, MongoDatabase db)
    {
    	MongoCollection<Document> col = db.getCollection(colName);
    	FindIterable<Document> iterable;
    	iterable = col.find().limit(1).sort(descending(sortParam));
    	return iterable;
    }
    
    //method that will return a new id number based off the last id of a certain collection
    public int newId(String colName, String sortParam, MongoDatabase db) 
    {
    	FindIterable<Document> bruh = findLast(colName, sortParam, db);
		MongoCursor<Document> results = bruh.iterator();
		int newId = Integer.parseInt(results.next().get(sortParam).toString());
		newId++;
		return newId;
    }
    
    
    //print all logs that where found and stored in an iterable
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
    public void update(String colName, String param, String idParam, int idNum, String updatedPhrase, int updatedNum, MongoDatabase db) {
    	MongoCollection<Document> col = db.getCollection(colName);
    	if(updatedPhrase == null)
    	{
    		col.findOneAndUpdate(eq(idParam, idNum), set(param, updatedNum));
    	}
    	else {
    		col.findOneAndUpdate(eq(idParam, idNum), set(param, updatedPhrase));
    	}
    	System.out.println("We do a little updating");
    }
    
}
