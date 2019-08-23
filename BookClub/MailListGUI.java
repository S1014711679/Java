package BookClub;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.NumberFormat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MailListGUI extends Application {

	Label lb_ID = new Label("ID:");
	Label lb_Email = new Label("E-mail:");
	Label lb_status = new Label("Status: ");
	TextField tf_ID = new TextField();
	TextField tf_Email = new TextField();
	Button bt_Lookup = new Button("Lookup");

	String email_str = null;

	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane borderpane = new BorderPane();

		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(30, 30, 30, 30));

		gridpane.addRow(0, lb_ID, tf_ID);
		gridpane.addRow(1, lb_Email, tf_Email);
		gridpane.addRow(2, bt_Lookup);

		String id_str = tf_ID.getText().trim();

		// send information to the server
		bt_Lookup.setOnAction(e -> {
			try {

				if (id_str != null) {

					int id = Integer.parseInt(tf_ID.getText().trim());
					System.out.println(">GUI> The ID is not null;");
					// send the id to the server
					// Create a socket to connect to the server
					Socket socket = new Socket("localhost", 8999);
					System.out.println(">GUI > socket created ");

					// send id to the server
					DataOutputStream toServer_id = new DataOutputStream(socket.getOutputStream());
					toServer_id.writeInt(id);
					toServer_id.flush();
					System.out.println(">GUI> sent ID to the server");

					// get the input form the server the email address
					DataInputStream fromServer_email = new DataInputStream(socket.getInputStream());

					email_str = fromServer_email.readUTF();

					if (email_str.equals("-1")) {
						lb_status.setText("Status: No such student! ");
						tf_Email.setText("Exception:ID does not exist");
					} else {
						System.out.println(">GUI> email " + email_str);
						tf_Email.setText(email_str);
						lb_status.setText("Status: Found the email");
					}

				} else if (id_str == null) {

					lb_status.setText("Status: ID cannot be empty! Please input numbers! ");

				}

			} catch (IOException | NumberFormatException e1) {
				e1.printStackTrace();
			}

		});

		borderpane.setCenter(gridpane);
		borderpane.setBottom(lb_status);

		Scene scene = new Scene(borderpane);
		primaryStage.setTitle("Look Up");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
