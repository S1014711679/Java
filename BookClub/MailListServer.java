package BookClub;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MailListServer extends Application {

	@Override
	public void start(Stage primaryStage) {

		TextArea ta = new TextArea();
		Scene scene = new Scene(new ScrollPane(ta), 450, 200);
		primaryStage.setTitle("MailList Server");
		primaryStage.setScene(scene);
		primaryStage.show();

		new Thread(() -> {
			try {

				ServerSocket serverSocket = new ServerSocket(8999);

				Platform.runLater(() -> ta.appendText("Server started at " + new Date() + '\n'));

				// Listen for a connection request
				Socket socket = serverSocket.accept();

				// Create data input and output streams
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

				while (true) {

					// receive the ID from client
					int id = inputFromClient.readInt();
					System.out.println(">server> id from client : " + id);

					// query the information from the Database
					EmailInquery in = new EmailInquery();

					String email_from_DB = in.getEmailByName(id);

					if (email_from_DB != null) {

						Platform.runLater(() -> {
							ta.appendText("ID received from client: " + id + '\n');
							ta.appendText("Email Address is: " + email_from_DB + '\n');
						});
						System.out.println(">server> mail from DB : " + email_from_DB);
						outputToClient.writeUTF(email_from_DB);

					} else {
						outputToClient.writeUTF("-1");
						ta.appendText("No such student exist! ");
						System.out.println(">server> no such student exist");
					}
				}

			} catch (IOException | NoSuchIdException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	public static void main(String[] args) {
		launch(args);

	}
}

//data operation
class EmailInquery {
	private static final String url = "jdbc:mysql://localhost/maillist?user=Guest&password=Guest123";
	private Connection conn = null;

	public String getEmailByName(int id) throws NoSuchIdException {

		String email_db = null;
		int id_db = id; // get from the client

		try {
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > The Database connected successfully!");
			// create a statement from the connection
			Statement stmt = conn.createStatement();
			ResultSet rs = null;
			// execute the query and return other email-address
			rs = stmt.executeQuery("select * from maillist where id =  '" + id + "';");
			while (rs.next()) {
				id_db = Integer.parseInt(rs.getString(1));
				System.out.println(">server> id_db = " + id_db);
				email_db = rs.getString(2);
				System.out.println(">server> email_db = " + email_db);
				System.out.println();
			}

			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return email_db;
	}
}

class NoSuchIdException extends Exception {

	public NoSuchIdException() {
		super();
	}

	public NoSuchIdException(String message) {
		super(message);
	}

	public void noSuchID() throws NoSuchIdException {
		throw new NoSuchIdException("No such student id is found!");
	}
}