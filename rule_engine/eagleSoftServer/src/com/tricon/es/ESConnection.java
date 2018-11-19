package com.tricon.es;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * This class provides methods that will be helpful in doing operations like
 * connecting,disconnection to Database (Eagle Soft DB)
 * 
 * @author Deepak.Dogra
 *
 */
public class ESConnection {

	static Connection con = null;
	static InputStream input = null;
	static Properties prop = null;
	static {
		try {
			input = new FileInputStream("c:/es/config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prop = new Properties();
		try {
			prop.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static BasicDataSource ds = new BasicDataSource();

	static {
		ds.setUrl("jdbc:sybase:Tds:localhost:" + prop.getProperty("port"));
		ds.setUsername(prop.getProperty("dbuser"));
		ds.setPassword(prop.getProperty("dbpassword"));
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(20);
	}

	/**
	 * Get connection to Eagle Soft database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	/**
	 * Close connection to Eagle Soft Database
	 * 
	 * @param con
	 *            Connection
	 */
	public static void closeConnection(Connection con) {

		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
