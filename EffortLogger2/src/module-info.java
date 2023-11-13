module EffortLogger2 {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires org.mongodb.driver.sync.client;
	requires org.mongodb.bson;
	requires org.mongodb.driver.core;

	opens application to javafx.graphics, javafx.fxml;
}
