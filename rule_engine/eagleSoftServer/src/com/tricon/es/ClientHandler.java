package com.tricon.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This Class extends Thread . This used to as handler method which starts a new thread and helps in initiating the 
 * reading of  data from EagleSoft Installed on local system.
 * @author Deepak.Dogra
 *
 */
class ClientHandler extends Thread {
	final OutputStreamWriter writer;
	final BufferedReader reader;
	final Socket s;
	final ServerSocket ss;
	
	private static final Logger logger = LogManager.getLogger();


	/**
	 * This is a constructor
	 * @param ss ServerSocket
	 * @param s Socket
	 * @param writer OutputStreamWriter
	 * @param reader BufferedReader
	 */
	public ClientHandler(ServerSocket ss, Socket s, OutputStreamWriter writer, BufferedReader reader) {
		this.ss = ss;
		this.s = s;
		this.writer = writer;
		this.reader = reader;
	}

	@Override
	public void run() {
		RuleEngineQuery query = null;
		try {

			String line = reader.readLine();
			ObjectMapper map = new ObjectMapper();
			query = map.readValue(line, RuleEngineQuery.class);
			Object obj = EagleSoftData.readData(query);
			ObjectMapper mapper2 = new ObjectMapper();
			mapper2.writeValue(writer, obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.writer.close();
				this.reader.close();
				this.s.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		if (query != null && query.getQuery().equals(Constants.kill_PROCESS)) {
			logger.info("Client " + this.s + " sends exit...");
			logger.info("Closing this connection.");
			try {
				this.ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Connection closed");
		}

	}
}