package inc.vareli.crusman.databases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CMConnectionTest {

	@Test
	void testCMConnection() {
		//create a database connection in my database
		CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
	}

}
