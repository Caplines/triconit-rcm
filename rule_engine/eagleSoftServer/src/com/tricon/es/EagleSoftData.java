package com.tricon.es;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class contains methods for handling data requests coming from Rule Engine
 * @author Deepak.Dogra
 *
 */
public class EagleSoftData {
	
	private static final Logger logger = LogManager.getLogger();

	

	/**
	 * This method will return Data from Eagle soft based on query requested from source
	 * @param query 
	 * @return Object containing Eagle Soft Data
	 */
	public static Object readData(RuleEngineQuery query) {

		Connection con = null;
		SenderObject obj = null;

		try {

			con = ESConnection.getConnection();

			PreparedStatement pstmt = con.prepareStatement(query.getQuery());
			String ids[]=query.getIds().split(",");
			for (int pmst = 1; pmst <= query.getPrepStCount(); pmst++) {
				pstmt.setString(pmst, ids[pmst-1]);

			}
			ResultSet rs = pstmt.executeQuery();
			// Process the result set.
			int ctr = 0;
			while (rs.next()) {
				if (ctr == 0) {
					obj = new SenderObject();
				}
				List<String> dataList = new ArrayList<>();

				for (int x = 1; x <= query.getColumnCount(); x++) {
					dataList.add(rs.getString(x));

				}
				Map<String, List<String>> map = obj.getDataMap();
				map.put(Integer.toString(ctr), dataList);
				ctr++;

			}
			rs.close();
			pstmt.close();
		} catch (SQLException sqe) {
			logger.error("Unexpected exception : " + sqe.toString() + ", sqlstate = " + sqe.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			ESConnection.closeConnection(con);

		}
		return obj;
	}


}
