package application;
	
import java.util.Scanner;
import java.util.InputMismatchException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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

import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.security.Timestamp;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Sorts.descending;


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
    
    public void start(Stage stage) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
    	Parent fxml = fxmlLoader.load();
    	Scene scene = new Scene(fxml,900,600);
    	stage.setTitle("Login");
    	stage.setScene(scene);
    	stage.show();
    

    	String connectionString = "mongodb+srv://ndlovelace13:7Cpa4yubfjj7aPql@effortlogger.zfgzhfr.mongodb.net/?retryWrites=true&w=majority";
    	MongoDatabase db;
//    	
//    	MongoCollection<Document> col;
//    	MongoCollection<Document> userCol;
//    	
//    	// login system initialization
//    	Login loginSystems = new Login();
//    	
//    	//basic user stuff that James/Cole can link to login credentials later on
//    	String currentUser = "";
//    	long userId;
//    	int userSecurity;
//  
//    	int project = 1;
//    	MongoClient mongoClient = MongoClients.create(connectionString); 
//            // Send a ping to confirm a successful connection
//        	 db = mongoClient.getDatabase("Effortlogs");
//             col = db.getCollection("logs");
//             userCol = db.getCollection("users");
//   
//    	
//    	
//    	//stuff needed for the big while loop
//    	String continueChoice = "y";
//    	int action = 0;
//    	Scanner in = new Scanner(System.in);
//    	//trying to connect the driver 
    	
           //this second try is for anything mongo related
    			//finding the database and collection
                //initializes the indexes necessary for phrase searching
//            	searchInit(db);
//                
//                System.out.println("Welcome to the EffortLogger V2 Prototype!\n");
                
                // login system
//                while (!authorize)
//                {
//	                // login systems
//	                System.out.println("Please enter your user credentials:" + "\n" + "Username:");
//	                currentUser = in.nextLine();
//	                System.out.println("Password: ");
//	                String password = in.nextLine();
//	                
//	                if (loginSystems.findUser(currentUser, password, db))
//	                	authorize = true;
//	                else
//	                	System.out.println("Username or Password did not match. Try again");
//                }
//                
//                // retrieve all user info
//        		FindIterable<Document> filterUsers = userCol.find(eq("username", currentUser));
//        		MongoCursor<Document> userIterable = filterUsers.iterator();
//        		Document targetObject = userIterable.next();
//                userSecurity = Integer.parseInt(targetObject.get("securityLevel").toString());
//                userId = Long.parseLong(targetObject.get("userID").toString());
//
//
//                
//                //Main EffortLogger Loop (FOR NOW)
//                while (continueChoice.equals("y"))
//                {
//                	//output all options for database manipulation
//                	System.out.println("Please select an action from the following list:\n");
//                	System.out.println("1 = Add a Story or Log");
//                	System.out.println("2 = Edit an Existing Story or Log");
//                	System.out.println("3 = Delete an Existing Story or Log");
//                	System.out.println("4 = Search for a Story or Log");
//                	System.out.println("5 = Print all Stories or Logs");
//                	System.out.println("6 = Create a New User");
//                	System.out.println("7 = Delete a User");
//                	System.out.println("8 = Update an Existing User");
//                	System.out.println("9 = Print all Users");
//                	
//                	Boolean confirm = false;
//                	//verifying that an integer is entered that matches one of the 5 choices outlined above
//                	while (!confirm)
//                	{
//                		try
//                		{
//                			action = in.nextInt();
//                			if (action > 0 && action < 10)
//                			{
//                				confirm = true;
//                			}
//                			else
//                			{
//                				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
//                			}
//                			
//                		}
//                		catch (InputMismatchException e)
//                		{
//                			System.out.println("The input must be an integer, please enter a number from the above list:\n");
//                			in.nextLine();
//                		}
//                	}
//                	//vars necessary for decision-making
//                	String slChoice;
//                	String title;
//                	String details;
//                	
//                	//This switch statement handles the 5 different options for db manipulation
//                	//Each starts with a branching pathway to manipulate either the story or log collections
//                	//The following instructions vary between pathways
//                	switch (action)
//                	{
//                		case 1: //INSERT
//                			System.out.println("adding a story or log!");
//                			
//                			System.out.println("Would you like to add a story or log? Enter (S/L)\n");
//                			slChoice = in.next().toLowerCase();
//                			//input validation for s/l choice
//                			while (!slChoice.equals("s") && !slChoice.equals("l"))
//                        	{
//                        		System.out.println("Your input is invalid, please enter S/L");
//                        		slChoice = in.next().toLowerCase();
//                        	}
//                			if (slChoice.equals("s"))
//                			{
//                				//slChoice = "stories";
//                				in.nextLine();
//                				System.out.println("What title would you like this user story to have?\n");
//                				title = in.nextLine();
//                				System.out.println("What description would you like to add to the story?\n");
//                				details = in.nextLine();
//                				//calls to the insertStory function to actually add to the database
//                				insertStory(userId, project, title, details, db);
//                			}
//                			else //if (slChoice.equals("l"))
//                			{
//                				//slChoice = "logs";
//                				in.nextLine();
//                				System.out.println("What details would you like to add to the log?\n");
//                				details = in.nextLine();
//                				//calls to the insertLog function to add to the db
//                				insertLog(userId, project, 1, details, db);
//                			}
//
//                			
//                			break;
//                		case 2: //EDIT
//                			System.out.println("editing a story or log!");
//                			System.out.println("Would you like to edit a story or log? Enter (S/L)\n");
//                			slChoice = in.next().toLowerCase();
//                			//input validation for story/log case
//                			while (!slChoice.equals("s") && !slChoice.equals("l"))
//                        	{
//                        		System.out.println("Your input is invalid, please enter S/L");
//                        		slChoice = in.next().toLowerCase();
//                        	}
//                			
//                			String param = "";
//                			if (slChoice.equals("s"))
//                			{
//                				slChoice = "stories";
//                				param = "story-id";
//                			}
//                			else if (slChoice.equals("l"))
//                			{
//                				slChoice = "logs";
//                				param = "log-id";
//                			}
//                			//input validation for idNum entered, must be an integer and must be in bounds
//                			System.out.println("Please enter the id number of the entry you would like to edit:");
//                			int idNum = 0;
//                			confirm = false;
//                        	while (!confirm)
//                        	{
//                        		try
//                        		{
//                        			idNum = in.nextInt();
//                        			if (idNum > 0)
//                        			{
//                        				confirm = true;
//                        			}
//                        			else
//                        			{
//                        				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
//                        			}
//                        			
//                        		}
//                        		catch (InputMismatchException e)
//                        		{
//                        			System.out.println("The input must be an integer, please enter a number from the above list:\n");
//                        			in.nextLine();
//                        		}
//                        	}
//                        	//Searching for the requested entry
//                        	System.out.println("Now printing the current requested entry\n");
//                        	FindIterable<Document> iterable = numSearch(slChoice, param, idNum, db);
//                        	//Continues on if the entry is found, otherwise boots back to the main loop
//                        	if (iterable.first() != null)
//                        	{
//                        		print(iterable);
//                        		
//                        		if (slChoice.equals("logs"))
//                        		{
//                        			System.out.println("Starting editing process for logs");
//                        			System.out.println("What key would you like to change?");
//                        			System.out.println("For project-id, enter P");
//                        			System.out.println("For story-id, enter S");
//                        			System.out.println("For details, enter D");
//                					confirm = false;
//                					String editChoice = in.next().toLowerCase();
//                					//input validation for this choice
//                					while (!confirm)
//                					{
//                						switch (editChoice)
//                						{
//                    						case "p":
//                    							editChoice = "project-id";
//                    							confirm = true;
//                    							break;
//                    							
//                    						case "s":
//                    							editChoice = "story-id";
//                    							confirm = true;
//                    							break;
//                    							
//                    						case "d":
//                    							editChoice = "details";
//                    							confirm = true;
//                    							break;
//                    					
//                							
//                							default:
//                								System.out.println("Your input is invalid, please enter P/S/D");
//                                        		editChoice = in.next().toLowerCase();
//                								break;
//                						}
//                					}
//                					int updatedNum = 0;
//                					String updatedPhrase = null;
//                					//branching paths for numerical values vs. String
//                					if (editChoice.equals("details"))
//                					{
//                						in.nextLine();
//                						System.out.println("Please enter the new details you would like this log to have");
//                						updatedPhrase = in.nextLine();
//                					}
//                					else
//                					{
//                						System.out.println("Please enter the new " + editChoice + " you would like this log to have");
//                            			confirm = false;
//                            			//input validation for the updatedNumber
//                                    	while (!confirm)
//                                    	{
//                                    		try
//                                    		{
//                                    			updatedNum = in.nextInt();
//                                    			if (updatedNum > 0)
//                                    			{
//                                    				confirm = true;
//                                    			}
//                                    			else
//                                    			{
//                                    				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
//                                    			}
//                                    			
//                                    		}
//                                    		catch (InputMismatchException e)
//                                    		{
//                                    			System.out.println("The input must be an integer, please enter a number from the above list:\n");
//                                    			in.nextLine();
//                                    		}
//                                    	}
//                					}
//                					//Update function called once all inputs have been verified
//                					update(slChoice, editChoice, param, idNum, updatedPhrase, updatedNum, db);
//                					System.out.println("Now printing your updated entry");
//                					print(numSearch(slChoice, param, idNum, db));
//                					
//                        		}
//                        		else
//                        		{
//                        			//The story editing process is largely the same, with some varying choices based on the values present in each story object
//                        			System.out.println("Starting editing process for stories");
//                        			System.out.println("What key would you like to change?");
//                        			System.out.println("For project-id, enter P");
//                        			System.out.println("For title, enter T");
//                        			System.out.println("For details, enter D");
//                        			
//                					confirm = false;
//                					String editChoice = in.next().toLowerCase();
//                					while (!confirm)
//                					{
//                						switch (editChoice)
//                						{
//                    						case "p":
//                    							editChoice = "project-id";
//                    							confirm = true;
//                    							break;
//                    							
//                    						case "t":
//                    							editChoice = "title";
//                    							confirm = true;
//                    							break;
//                    							
//                    						case "d":
//                    							editChoice = "details";
//                    							confirm = true;
//                    							break;
//                    					
//                							
//                							default:
//                								System.out.println("Your input is invalid, please enter P/T/D");
//                                        		editChoice = in.next().toLowerCase();
//                								break;
//                						}
//                					}
//                					int updatedNum = 0;
//                					String updatedPhrase = null;
//                					if (editChoice.equals("details") || editChoice.equals("title"))
//                					{
//                						in.nextLine();
//                						System.out.println("Please enter the new " + editChoice + " you would like this log to have");
//                						updatedPhrase = in.nextLine();
//                					}
//                					else
//                					{
//                						System.out.println("Please enter the new " + editChoice + " you would like this log to have");
//                            			confirm = false;
//                                    	while (!confirm)
//                                    	{
//                                    		try
//                                    		{
//                                    			updatedNum = in.nextInt();
//                                    			if (updatedNum > 0)
//                                    			{
//                                    				confirm = true;
//                                    			}
//                                    			else
//                                    			{
//                                    				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
//                                    			}
//                                    			
//                                    		}
//                                    		catch (InputMismatchException e)
//                                    		{
//                                    			System.out.println("The input must be an integer, please enter a number from the above list:\n");
//                                    			in.nextLine();
//                                    		}
//                                    	}
//                					}
//                					//calls update on the story entry specified once all inputs have been validated
//                					update(slChoice, editChoice, param, idNum, updatedPhrase, updatedNum, db);
//                					System.out.println("Now printing your updated entry");
//                					print(numSearch(slChoice, param, idNum, db));
//                        		}
//                        	}
//                			break;
//                		case 3: //DELETE
//                			System.out.println("deleting a story or log!");
//                			System.out.println("Would you like to delete a story or log? Enter (S/L)\n");
//                			slChoice = in.next().toLowerCase();
//                			//input validation on story/log choice
//                			while (!slChoice.equals("s") && !slChoice.equals("l"))
//                        	{
//                        		System.out.println("Your input is invalid, please enter S/L");
//                        		slChoice = in.next().toLowerCase();
//                        	}
//                			
//                			param = "";
//                			if (slChoice.equals("s"))
//                			{
//                				slChoice = "stories";
//                				param = "story-id";
//                			}
//                			else if (slChoice.equals("l"))
//                			{
//                				slChoice = "logs";
//                				param = "log-id";
//                			}
//                			
//                			System.out.println("Please enter the id number of the entry you would like to remove:");
//                			idNum = 0;
//                			//input validation on the id number entry
//                			confirm = false;
//                        	while (!confirm)
//                        	{
//                        		try
//                        		{
//                        			idNum = in.nextInt();
//                        			if (idNum > 0)
//                        			{
//                        				confirm = true;
//                        			}
//                        			else
//                        			{
//                        				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
//                        			}
//                        			
//                        		}
//                        		catch (InputMismatchException e)
//                        		{
//                        			System.out.println("The input must be an integer, please enter a number from the above list:\n");
//                        			in.nextLine();
//                        		}
//                        	}
//                        	//attempts to delete entry if it exists
//                        	deleteEntry(idNum, slChoice, param, db);
//                			break;
//                		case 4: //SEARCH
//                			System.out.println("searching for a story or log!");
//                			System.out.println("Would you like to search for a story or log? Enter (S/L)\n");
//                			slChoice = in.next().toLowerCase();
//                			while (!slChoice.equals("s") && !slChoice.equals("l"))
//                        	{
//                        		System.out.println("Your input is invalid, please enter S/L");
//                        		slChoice = in.next().toLowerCase();
//                        	}
//                			
//                			if (slChoice.equals("s"))
//                				slChoice = "stories";
//                			else if (slChoice.equals("l"))
//                				slChoice = "logs";
//                			
//                			System.out.println("Would you like to search by a number or a phrase? Enter (N/P)");
//                			String npChoice = in.next().toLowerCase();
//                			//different search functions for numbers versus phrases due to var types and the way mongo handles searching
//                			while (!npChoice.equals("n") && !npChoice.equals("p"))
//                			{
//                				System.out.println("Your input is invalid, please enter N/P");
//                        		npChoice = in.next().toLowerCase();
//                			}
//                			if (npChoice.equals("n"))
//                			{
//            					System.out.println("Would you like to search by user, project, story, or log id? Enter (U/P/S/L)");
//            					System.out.println("Disclaimer: you may not search by log-id if you are searching the story collection");
//            					String idChoice = in.next().toLowerCase();
//            					//input validation for the choice of search
//            					param = "";
//            					confirm = false;
//            					while (!confirm)
//            					{
//            						switch (idChoice)
//            						{
//                						case "u":
//                							param = "user-id";
//                							confirm = true;
//                							break;
//                						
//                						case "p":
//                							param = "project-id";
//                							confirm = true;
//                							break;
//                							
//                						case "s":
//                							param = "story-id";
//                							confirm = true;
//                							break;
//                						
//                						case "l":
//                							if (slChoice.equals("stories"))
//                							{
//                								System.out.println("The log-id parameter does not exist in the story collection. Please select another");
//                								idChoice = in.next().toLowerCase();
//                							}
//                							else
//                							{
//                								param = "log-id";
//                								confirm = true;
//                							}
//                							break;
//            							
//            							default:
//            								System.out.println("Your input is invalid, please enter N/P");
//                                    		idChoice = in.next().toLowerCase();
//            								break;
//            						}
//            					}
//            					
//            					System.out.println("Please enter the id number of the entry you would like to search for:");
//                    			idNum = 0;
//                    			confirm = false;
//                            	while (!confirm)
//                            	{
//                            		try
//                            		{
//                            			idNum = in.nextInt();
//                            			if (idNum > 0)
//                            			{
//                            				confirm = true;
//                            			}
//                            			else
//                            			{
//                            				System.out.println("Your input is out of bounds, please enter a number from the above list:\n");
//                            			}
//                            			
//                            		}
//                            		catch (InputMismatchException e)
//                            		{
//                            			System.out.println("The input must be an integer, please enter a number from the above list:\n");
//                            			in.nextLine();
//                            		}
//                            	}
//                            	//prints the results of the search using validated inputs
//            					print(numSearch(slChoice, param, idNum, db));
//                			}
//                			else
//                			{
//                				System.out.println("Please enter the phrases you would like to search for");
//                				in.nextLine();
//                				String phrase = in.nextLine();
//                				//prints the results of the search using validated inputs
//                				print(phraseSearch(slChoice, phrase, db));
//                			}
//                			break;
//                		case 5:
//                			System.out.println("printing all stories or logs!");
//                			
//                			System.out.println("Would you like to print all stories or logs? Enter (S/L)\n");
//                			slChoice = in.next().toLowerCase();
//                			//input validation for story vs. log choice
//                			while (!slChoice.equals("s") && !slChoice.equals("l"))
//                        	{
//                        		System.out.println("Your input is invalid, please enter S/L");
//                        		slChoice = in.next().toLowerCase();
//                        	}
//                			
//                			if (slChoice.equals("s"))
//                				slChoice = "stories";
//                			else if (slChoice.equals("l"))
//                				slChoice = "logs";
//
//                			System.out.println("Now printing all entries from " + slChoice + " collection:\n");
//                			//call to the print function
//                			printCol(slChoice, db);
//                			
//                			break;
//                		
//                		// adding new user
//                		case 6:
//                			if (userSecurity < 3) {
//                				System.out.println("Invalid Security Level");
//                				break;
//                			}
//                			System.out.println("Please fill out required prompts to add a new user\n");
//                			System.out.println("Please enter a username\n");
//                			String username = in.nextLine();
//                			while (username.equals(""))
//                			{
//                				System.out.println("Invalid username. Username cannot be blank. Please re-enter\n");
//                				username = in.nextLine();
//                			}
//                			System.out.println("Please enter a password\n");
//                			String password = in.nextLine();
//                			while (password.equals(""))
//                			{
//                				System.out.println("Invalid password. Password cannot be blank. Please re-enter\n");
//                				password = in.nextLine();
//                			}
//                			System.out.println("Please enter a security level\n");
//                			int securityLevel = in.nextInt();
//                			while (securityLevel < 0 || securityLevel > 4)
//                			{
//                				System.out.println("Invalid Security Level. Security level must be an integer between 0 and 4 inclusive\n");
//                				securityLevel = in.nextInt();
//                			}
//                			
//                			loginSystems.addUser(username, password, securityLevel, db);
//                			
//                			break;
//                			
//                		case 7:
//                			if (userSecurity < 2) {
//                				System.out.println("Invalid Security Level");
//                				break;
//                			}
//                			System.out.println("Please enter a userID to delete the user: \n");
//                			
//                			long deleteID = in.nextLong();
//                			
//                			loginSystems.deleteUser(deleteID, db);
//                			System.out.println("User was Deleted");
//                			break;
//                			
//                		case 8:
//                			if (userSecurity < 2) {
//                				System.out.println("Invalid Security Level");
//                				break;
//                			}
//
//                			System.out.println("Please enter a username: \n");
//                			String findUsername = in.nextLine();
//                			while (findUsername.equals(""))
//                			{
//                				System.out.println("Username cannot be empty. Please reenter username: \n");
//                				findUsername = in.nextLine();
//                			}
//                			System.out.println("Please enter a password: \n");
//                			String findPassword = in.nextLine();
//                			while (findPassword.equals(""))
//                			{
//                				System.out.println("Password cannot be empty. Please reenter password: \n");
//                				findPassword = in.nextLine();
//                			}
//                			
//                			System.out.println("Please enter a new username: \n");
//                			String newUsername = in.nextLine();
//                			while (newUsername.equals(""))
//                			{
//                				System.out.println("Username cannot be empty. Please reenter username: \n");
//                				newUsername = in.nextLine();
//                			}
//                			System.out.println("Please enter a password: \n");
//                			String newPassword = in.nextLine();
//                			while (newPassword.equals(""))
//                			{
//                				System.out.println("Password cannot be empty. Please reenter password: \n");
//                				newPassword = in.nextLine();
//                			}
//                			System.out.println("Please enter a userID: \n");
//                			int newUserId = in.nextInt();
//                			
//                			System.out.println("Please enter a security level: \n");
//                			int newSecurityLevel = in.nextInt();
//                			while (newSecurityLevel < 0 || newSecurityLevel > 4)
//                			{
//                				System.out.println("Invalid Security Level. Security level must be an integer between 0 and 4 inclusive: \n");
//                				newSecurityLevel = in.nextInt();
//                			}
//                			
//                			boolean updated = loginSystems.updateUser(findUsername, findPassword, newUsername, newPassword, newUserId, newSecurityLevel, db);
//                			
//                			if (updated)
//                				System.out.println("User succesfully updated\n");
//                			else
//                				System.out.println("User not updated. Please recheck data entries and ensure this user exists already\n");
//                			break;
//                			
//                		// print all users in database
//                		case 9:
//                			if (userSecurity < 5) {
//                				System.out.println("Invalid Security Level");
//                				break;
//                			}
//                			System.out.println("Now printing all users in database");
//                			loginSystems.printUsers(db);
//                			break;
//                			
//                			
//                	} // end of switch
//                	
//                	System.out.println("Would you like to continue using EffortLogger V2? (Y/N)");
//                	continueChoice = in.next().toLowerCase();
//                	//input validation for continue choice
//                	while (!continueChoice.equals("y") && !continueChoice.equals("n"))
//                	{
//                		System.out.println("Your input is invalid, please enter Y/N");
//                		continueChoice = in.next().toLowerCase();
//                	}
//                }
//                //closing of scanner object
//                in.close();
                
 
       
    	
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
    public void insertStory(long userId, int projectId, String title, String description, MongoDatabase db) 
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
