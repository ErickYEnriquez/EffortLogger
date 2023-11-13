package application;

import static com.mongodb.client.model.Filters.eq;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
// designed by James Kim
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

public class Login{


	private static SecretKeySpec secretKey;
	private static byte[] key;
	private static final String ALGORITHM = "AES";
	private static String password;
	private static String username;

	//getters and setters I added
	public static void setPassword(String newPassword) {
		password = newPassword;

	}

	public static void setUsername(String newUser) {
		username = newUser;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	// gathers information about the user such as username, password, etc. and pushes it to the database
	public void addUser(String username, String password, int securityLevel, MongoDatabase db)
	{
		// variable acquisition

		String addUsername = username;
		String addPassword = password;
		int addSecurityLevel = securityLevel;

		// userID creation
		UUID userID = UUID.randomUUID();
		long userInt = Long.parseLong(Math.abs(userID.getLeastSignificantBits()) + "");

		// initialize collection
    	MongoCollection<Document> col = db.getCollection("users");
    	// create new initial user with dummy password and insert it into database
    	Document newUser = new Document("username", addUsername)
    			.append("password", "")
    			.append("userID", userInt)
    			.append("securityLevel", addSecurityLevel);
    	col.insertOne(newUser);
    	// create secret string through the newly inserted user's database objectID
    	String secret = newUser.getObjectId(newUser) + "";
    	// create filter document that finds the dummy newUser
    	Document filter = new Document("userID", userInt);
    	// Update object that contains new encrypted password
    	Bson updates = Updates.combine(Updates.set("password", encrypt(addPassword, secret)));
    	// UpdateOptions for inserting object to reduce risk
    	UpdateOptions options = new UpdateOptions().upsert(true);

    	// update database. if failure, print error
    	try
    	{
    		col.updateOne(filter, updates, options);
    		System.out.println("Inserted User");
    	}
    	catch (MongoException e)
    	{
    		System.out.println("Insertion Error: " + e);
    	}

	}

	// searches for user in database. If parameters match, return true. Else return false
	public boolean findUser(String username, String password, MongoDatabase db)
	{
		MongoCollection<Document> col = db.getCollection("users");
		// find user name in database
		FindIterable<Document> filterUsers = col.find(eq("username", username));
		Document targetObject = filterUsers.first();
		if(targetObject == null)
			return false;
		// use user name to get object's database id
		String secret = targetObject.getObjectId(targetObject) + "";
		// find password in database
		String dbPassword = targetObject.get("password").toString();
		// check given password against database password decrypted with key
		String encryptedPass = encrypt(password, secret);
		//System.out.println(encryptedPass);
		if (encryptedPass.equals((dbPassword)) && targetObject.getString("username").toString().equals(username))
		{
			//System.out.println("User found and logged in succesfully");
			return true;
		}
		return false;
	}

	// toString method. Print list of all users in database into json file
	public void printUsers(MongoDatabase db)
	{
		try
		{
			// create some sort of iterable that contains all users somehow
			MongoCollection<Document> col = db.getCollection("users");
			FindIterable<Document> iterable = col.find();
			MongoCursor<Document> results = iterable.iterator();
			Document targetObject = results.next();
			while(results.hasNext())
			{
				System.out.println(targetObject.toString());
				String secret = targetObject.getObjectId(targetObject) + "";
				String decryptedPass = decrypt(targetObject.getString("password"), secret);
				System.out.println("Decrypted Password: " + decryptedPass);
				targetObject = results.next();
			}
			System.out.println("\n" + targetObject.toString());
			String secret = targetObject.getObjectId(targetObject) + "";
			String decryptedPass = decrypt(targetObject.getString("password"), secret);
			System.out.println("Decrypted Password: " + decryptedPass);
		}
		catch (MongoException e)
		{
			e.printStackTrace();
		}
	}

	// deletes user from database by providing userID and password????
	public void deleteUser(long userId, MongoDatabase db)
	{
		MongoCollection<Document> col = db.getCollection("users");
		col.deleteOne(eq("userID", userId));
	}

	// function to change variables in user object
	public Boolean updateUser(String username, String password, String newUsername, String newPassword, int newUserId, int newSecurityLevel, MongoDatabase db)
	{
		// check if user is in database first
		if (!findUser(username, password, db))
			return false;

		MongoCollection<Document> col = db.getCollection("users");


		System.out.println("Made past id filter checks and username checks");
		// find user name in database
		FindIterable<Document> filterUsers = col.find(eq("username", username));
		MongoCursor<Document> iterable = filterUsers.iterator();
		Document targetObject = iterable.next();

		// create secret string through the newly inserted user's database objectID
    	String secret = targetObject.getObjectId(targetObject) + "";
    	// create filter document that finds the dummy newUser
    	Document filter = new Document("username", username);
    	// Update object that contains new encrypted password
    	Bson updates = Updates.combine(
    			Updates.set("username", newUsername),
    			Updates.set("password", encrypt(newPassword, secret)),
    			Updates.set("userID", newUserId),
    			Updates.set("securityLevel", newSecurityLevel)
    			);

    	// UpdateOptions for inserting object to reduce risk
    	UpdateOptions options = new UpdateOptions().upsert(true);

    	// update database. if failure, print error
    	try
    	{
    		col.updateOne(filter, updates, options);

    		// check if any inputs would already belong to an existing user
    		System.out.println("Created check filters");
    		if (col.find(eq("username", newUsername)).first() != null)
    			return false;
    		System.out.println("Cleared checkID");
    		if (col.find(eq("userID", newUserId)).first() != null)
    			return false;
    		System.out.println("Inserted User");
    		return true;
    	}
    	catch (MongoException e)
    	{
    		System.out.println("Insertion Error: " + e);
    	}

    	return false;
	}

	// encrypts given string with on-demand generated AES key. Sends data via base64
	public String encrypt(String target, String secret)
	{
		try
		{
			prepareSecretKey(secret);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(target.getBytes("UTF-8")));
		}
		catch (Exception e)
		{
			System.out.println("Encryption error: " + e.toString());
		}
		return null;
	}


	public String decrypt(String target, String secret)
	{
		try {
			prepareSecretKey(secret);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE,  secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(target)));
		}
		catch (Exception e)
		{
			System.out.println("Decryption Error: " + e.toString());
		}
		return null;
	}


	// creates a key based off of a string input using sha hash and AES type 1 encryption
	public void prepareSecretKey(String myKey)
	{
		MessageDigest sha = null;
		try {
			key = myKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, ALGORITHM);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

}	// end of Login.java

