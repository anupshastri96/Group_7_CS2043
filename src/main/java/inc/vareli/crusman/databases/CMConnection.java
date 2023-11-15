package inc.vareli.crusman.databases;

import inc.vareli.crusman.data.*;

import java.sql.*;

public abstract class CMConnection implements Connection {
	/*
	 * Arhaan, I put the name and stuff here as a starting point, but you don't 
	 * do it one particular way. The rest of the program will just be written 
	 * so that it requires a Connection instance, so if you write a class that
	 * implements Connection, or a class that provides a Connection by returning
	 * it etc, that's fine.
	 * I'm just going to write them to use the java.sql.Connection interface.
	 *
	 */

	/*
	 * Another way to deal with fetching info from the DB, maybe this is preferred, idk
	 */
	public abstract Ship fetchShip(long ID);

	public abstract Trip fetchTrip(long ID);
}
