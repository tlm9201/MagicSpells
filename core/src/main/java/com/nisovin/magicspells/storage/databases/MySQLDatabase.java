package com.nisovin.magicspells.storage.databases;

import java.io.File;
import java.io.IOException;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.storage.Database;

public class MySQLDatabase extends Database {

	public MySQLDatabase(MagicSpells plugin, String dbLocation) {
		super(plugin, dbLocation);
	}

	@Override
	public Connection openConnection() {
		File folder = plugin.getInstance().getDataFolder();
		if (!folder.exists()) folder.mkdirs();

		File file = new File(folder, dbLocation);
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				MagicSpells.error("There was an error with creating the database file: " + e.getMessage());
			}
		}

		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + folder.toPath() + "/" + dbLocation);
		} catch (Exception e) {
			MagicSpells.error("There was an error with creating a connection for the database: " + e.getMessage());
		}
		return connection;
	}

	@Override
	public void createTables() {
		Connection connection = getConnection();
		try {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS player_data (id INTEGER PRIMARY KEY AUTO_INCREMENT, playerID VARCHAR(256) NOT NULL, level INTEGER"
					+ " NOT NULL, experience INTEGER NOT NULL);");
		} catch (SQLException e) {
			MagicSpells.error("There was an error with creating a table for the database: " + e.getMessage());
		}
	}

}
