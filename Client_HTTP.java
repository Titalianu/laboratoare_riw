package elCliento;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client_HTTP {

	private final static int SERVER_PORT = 80;

	private static String domeniu = "";
	private static String resursa = "";
	private static String user_agent = "CLIENT RIW";

	private static Socket clientSocket = null;
	private static DataOutputStream dataOutput = null;
	private static BufferedReader inFromServer = null;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			try {
				Client_HTTP.getHttpClient();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	static void Domain_resource(String url) {
		if (url.startsWith("http://")) {
			int numberOfSlahes = 0;
			for (int i = 0; i < url.length(); i++) {
				if (numberOfSlahes == 2 && url.charAt(i) != '/') {
					domeniu += url.charAt(i);
				}
				if (url.charAt(i) == '/') {
					numberOfSlahes++;
				}
				if (numberOfSlahes > 2) {
					resursa += url.charAt(i);
				}
			}
			System.out.println(domeniu);
			System.out.println(resursa);
		} else {
			System.out.println("Url invalid!");
			return;
		}
	}
	//Preluare raspuns de la server
	static void GET_response() throws IOException {
		if (clientSocket != null && !clientSocket.isClosed()) {
			clientSocket.close();
			dataOutput.close();
			inFromServer.close();
			clientSocket = null;
			dataOutput = null;
			inFromServer = null;
		}
		
		clientSocket = new Socket(domeniu, SERVER_PORT);
		dataOutput = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		dataOutput.writeBytes("GET " + resursa + " HTTP/1.1\r\n");
		dataOutput.writeBytes("Host:" + domeniu + "\r\n");
		dataOutput.writeBytes("User-Agent" + user_agent +"\r\n");
		dataOutput.writeBytes("\r\n");
	}

	public static void getHttpClient() throws IOException {
		String url = "http://riweb.tibeica.com/crawl/";
		Domain_resource(url);
		GET_response();
		
		PrintWriter error_writer = new PrintWriter("Log.txt","UTF-8");
		String received = inFromServer.readLine();
		boolean foundResource = false;
		int tries = 5;

		while (!foundResource && tries > 0) {
			if (received.contains("HTTP/1.1 200 OK")) {
				boolean flag = false;
				PrintWriter writer = new PrintWriter("Resursica.html", "UTF-8");

				while (received != null) {
					if (received.startsWith("<!DOCTYPE html"))
						flag = true;

					if (flag) {
						writer.println(received);
						//System.out.println(received);
					} else
						System.out.println(received);

					received = inFromServer.readLine();
				}
				foundResource = true;
				writer.close();
			} else {
				while (received != null) {
					error_writer.println(received);
					received = inFromServer.readLine();
				}
				error_writer.close();
				
			}
			--tries;
		}

		if (clientSocket != null && !clientSocket.isClosed()) {
			clientSocket.close();
			dataOutput.close();
			inFromServer.close();
		}
	}

}
