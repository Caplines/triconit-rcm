package com.tricon.es;

import java.io.*;
import java.util.*;

import javax.net.ssl.SSLServerSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;

/**
 * This class helps in initiating a thread that will serve as Server for
 * handling request from Rule Engine. We need a pre - configured self generated
 * SSL certificates. We use socket here for listening requests from Rule Engine
 * on preconfigured port (mentioned in File c:/es/config.properties)
 * 
 * @author Deepak.Dogra
 *
 */
public class Server {

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

	private static final Logger logger = LogManager.getLogger();

	/**
	 * Starting point of Program /Application
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// https://www.youtube.com/watch?v=l4_JIIrMhIQ
		System.setProperty("javax.net.ssl.trustStore", "cacerts.jks");
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", prop.getProperty("sslpassword"));

		logger.info("Server has started to Listen request from Rule Engine");

		ServerSocket ss = null;
		try {
			ss = ((SSLServerSocketFactory) SSLServerSocketFactory.getDefault())
					.createServerSocket(Integer.parseInt(prop.getProperty("listenport")));

			while (true) {
				Socket s = null;
				if (ss.isClosed() || (s != null && s.isClosed()))
					break;
				try {
					// socket object to receive incoming client requests
					s = ss.accept();
					logger.info("Client IP is :" + s.getRemoteSocketAddress().toString());
					logger.info("A new client is connected :" + s);

					OutputStreamWriter writer = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
					BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));

					logger.info("Assigning new thread for this client");
					if (prop.getProperty("production").equals("false")
							|| prop.getProperty("ruleengineip").startsWith(s.getRemoteSocketAddress().toString())) {
						// create a new thread object
						Thread t = new ClientHandler(ss, s, writer, reader);

						// Invoking the start() method
						t.start();
					}

				} catch (Exception e) {
					if (s != null && s.isClosed())
						break;
					if (s != null)
						s.close();
					// e.printStackTrace();
					logger.error(e.getMessage());
				}
			} // while

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ss.close();
		}
	}
}
