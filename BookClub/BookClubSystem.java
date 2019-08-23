package BookClub;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static javafx.geometry.Pos.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.sound.midi.ControllerEventListener;

public class BookClubSystem extends Application {

	protected BookDataOperation bdo = new BookDataOperation();

	/************************** Login Pane **************************/
	int login_id = 0; // login_id as administrator 1 or ordinary 0
	String login_username = null;
	String login_password = null;
	final ObservableList<String> items = FXCollections.observableArrayList("Ordinary", "Admin");

	Member toServer_member = null;
	Member fromServer_member = null;

	Member login_member = null;

	BorderPane login_borderpane = new BorderPane();
	GridPane login_gridpane = new GridPane();

	HBox login_hbox = new HBox();
	HBox login_hbox_status = new HBox();
	Label login_lb_membership = new Label("Membership:\t");
	Label login_lb_username = new Label("Username:\t");
	Label login_lb_password = new Label("Password:\t");
	Label login_lb_status = new Label("Status");

	TextField login_tf_username = new TextField();
	TextField login_tf_password = new TextField();

	Button login_bt_login = new Button(" Login ");
	Button login_bt_register = new Button("Register");

	ChoiceBox<String> login_choicebox = new ChoiceBox<String>(items);

	/****************** Memberinfo Pane *******************************/

	TextArea memberinfo_ta = new TextArea();
	String information = "";

	TextField memberinfo_tf_membership = new TextField();
	TextField memberinfo_tf_username = new TextField();
	TextField memberinfo_tf_email = new TextField();
	TextField memberinfo_tf_book = new TextField();

	public static int memberinfo_flag = 0;
	//////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		launch(args);
	}

	/************ Start Function ************/
	@Override
	public void start(Stage primaryStage) throws IOException {

		/************************** Login Scene **************************/
		// get the value from the choice
		login_choicebox.setOnAction(e -> {
			if (login_choicebox.valueProperty().get().equals("Admin")) {
				System.out.println(">LoginPane > Login as an Administratory");
				login_id = 1;
				login_lb_status.setText("Status: Administrator");

			} else if (login_choicebox.valueProperty().get().equals("Ordinary")) {
				System.out.println(">LoginPane > Login as an Ordinary Member");
				login_id = 0;
				login_lb_status.setText("Status: Oridinary Member");
			}
		});

		login_hbox.getChildren().addAll(login_bt_register, login_bt_login);
		login_hbox.setAlignment(BASELINE_RIGHT);
		login_hbox.setPadding(new Insets(10, 10, 10, 10));
		login_hbox.setSpacing(30);
		login_hbox_status.getChildren().add(login_lb_status);
		login_hbox_status.setStyle("-fx-background-color:azure");

		login_gridpane.add(login_lb_membership, 0, 0);
		login_gridpane.add(login_lb_username, 0, 1);
		login_gridpane.add(login_lb_password, 0, 2);

		login_gridpane.add(login_choicebox, 1, 0);
		login_gridpane.add(login_tf_username, 1, 1);
		login_gridpane.add(login_tf_password, 1, 2);
		login_gridpane.add(login_hbox, 0, 3, 2, 1);
		login_gridpane.setVgap(10);

		login_borderpane.setMargin(login_gridpane, new Insets(50, 90, 40, 50));
		login_borderpane.setCenter(login_gridpane);
		login_borderpane.setBottom(login_hbox_status);

		// login
		login_bt_login.setOnAction(e -> {

			try {
				// Create a socket to connect to the server
				Socket socket = new Socket("localhost", 6996);
				System.out.println(">LoginPane > socket created ");

				login_username = login_tf_username.getText().trim();
				login_password = login_tf_password.getText().trim();

				// IO streams // send flag to the server first to do login operation
				DataOutputStream toServer_flag = new DataOutputStream(socket.getOutputStream());
				toServer_flag.writeInt(1);

				// login return the object as admin or ordinary
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				toServer_member = new Member(login_id, login_username, login_username);
				toServer.writeObject(toServer_member);
				toServer.flush();

				// create an input stream to receive data from the server && check the
				ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
				fromServer_member = (Member) fromServer.readObject();
				login_member = fromServer_member;

				// admin login
				if (login_id == 1 && fromServer_member != null) {
					login_lb_status.setText("Status: Administrator");
					// check the password
					if (login_id == fromServer_member.getId() && login_username.equals(fromServer_member.getUsername())
							&& login_password.equals(fromServer_member.getPassword())) {
						login_lb_status.setText("Status: Administrator Login Successfully");
						System.out.println(">LoginPane > Admin Login Successfully");
						MemberInfoPane();
						login_lb_status.setText("Status:");
						primaryStage.close();
					} else {
						System.out.println(">LoginPane > Admin Login failed");
						login_lb_status.setText("Status: Administrator >> Wrong Password");
					}

				}

				// ordinary member login
				else if (login_id == 0 && fromServer_member != null) {
					// check the password
					if (login_id == fromServer_member.getId() && login_username.equals(fromServer_member.getUsername())
							&& login_password.equals(fromServer_member.getPassword())) {
						login_lb_status.setText("Status: Ordinary Member");
						System.out.println(">LoginPane > Ordianry Member Login Successfully!");
						// go to the MemberInfoPane
						MemberInfoPane();
						login_lb_status.setText("Status:");
						primaryStage.close();
					} else {
						System.out.println(">LoginPane > Ordianry Member Login failed!");
						login_lb_status.setText("Status: Oridinary Member >> Wrong Password");
					}

				} else {
					System.out.println(">LoginPane > Account is not in the system!");
					login_lb_status.setText("Status: Account is not in the System! Please Register !");
				}

				// send flag to server to destroy the thread
				toServer_flag.writeInt(-1);
				toServer_flag.flush();
				System.out.println(">LoginPane > desotry the thread");
				socket.close();
				System.out.println(">LoginPane > socket closed");
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
			} finally {
				login_tf_username.clear();
				login_tf_password.clear();
			}

		});

		// register user information
		login_bt_register.setOnAction(e -> {
			RegisterPane();
		});

		// start point
		Scene login_scene = new Scene(login_borderpane);
		primaryStage.setTitle("Book Club Login & Register");
		primaryStage.setScene(login_scene);
		primaryStage.show();

	}

	/************************** Member information Pane **************************/
	public void MemberInfoPane() {

		Member memberinfo = login_member;
		System.out.println(">MemberInfoPane > memberinfo: " + memberinfo.toString());

		BorderPane memberinfo_borderpane = new BorderPane();
		GridPane memberinfo_gridpane = new GridPane();

		HBox hbox_lb_status = new HBox();
		HBox hbox_button = new HBox();
		VBox vbox_right = new VBox();

		Label lb_title = new Label("Information Board");
		ScrollPane scrollPane = new ScrollPane(memberinfo_ta);

		Label memberinfo_lb_membership = new Label("Membership:\t");
		Label memberinfo_lb_username = new Label("Username:\t");
		Label memberinfo_lb_email = new Label("E-mail:\t");
		Label memberinfo_lb_book = new Label("Favorite Book:\t");

		lb_title.setFont(new Font("Arial", 18));

		vbox_right.getChildren().addAll(lb_title, scrollPane);
		vbox_right.setPrefSize(250, 200);

		VBox.setMargin(scrollPane, new Insets(5, 5, 5, 0));
		memberinfo_ta.setText("New Information: \n");
		Label memberinfo_lb_status = new Label("Status:");

		Button memberinfo_bt_update = new Button("Update Profile");
		Button memberinfo_bt_message = new Button("Message");
		Button memberinfo_bt_adminview = new Button("AdminView");

		System.out.println(">Memberinfo Pane > socket created");

		if (memberinfo.getId() == 0) {
			memberinfo_tf_membership.setText("Ordianry Member");
			memberinfo_lb_status.setText("Ordinary Member");
		} else if (memberinfo.getId() == 1) {
			memberinfo_tf_membership.setText("Administator");
			memberinfo_lb_status.setText("Administrator");
		}

		memberinfo_tf_membership.setEditable(false);
		memberinfo_tf_username.setText(memberinfo.getUsername());
		memberinfo_tf_username.setEditable(false);
		memberinfo_tf_email.setText(memberinfo.getEmail());
		memberinfo_tf_email.setEditable(false);
		memberinfo_tf_book.setText(memberinfo.getBook());
		memberinfo_tf_book.setEditable(false);

		memberinfo_gridpane.add(memberinfo_lb_membership, 0, 0);
		memberinfo_gridpane.add(memberinfo_lb_username, 0, 1);
		memberinfo_gridpane.add(memberinfo_lb_email, 0, 2);
		memberinfo_gridpane.add(memberinfo_lb_book, 0, 3);

		memberinfo_gridpane.add(memberinfo_tf_membership, 1, 0);
		memberinfo_gridpane.add(memberinfo_tf_username, 1, 1);
		memberinfo_gridpane.add(memberinfo_tf_email, 1, 2);
		memberinfo_gridpane.add(memberinfo_tf_book, 1, 3);

		if (memberinfo.getId() == 0) {
			hbox_button.getChildren().addAll(memberinfo_bt_update, memberinfo_bt_message);
		} else if (memberinfo.getId() == 1) {
			hbox_button.getChildren().addAll(memberinfo_bt_update, memberinfo_bt_message, memberinfo_bt_adminview);
		}
		hbox_button.setPadding(new Insets(10, 10, 10, 10));
		hbox_button.setAlignment(BASELINE_RIGHT);
		hbox_button.setSpacing(30);
		memberinfo_gridpane.add(hbox_button, 0, 4, 2, 1);

		hbox_lb_status.getChildren().add(memberinfo_lb_status);
		hbox_lb_status.setStyle("-fx-background-color:azure");

		memberinfo_gridpane.setVgap(10);
		memberinfo_borderpane.setCenter(memberinfo_gridpane);
		memberinfo_borderpane.setMargin(memberinfo_gridpane, new Insets(20, 90, 40, 50));

		memberinfo_borderpane.setRight(vbox_right);
		memberinfo_borderpane.setBottom(hbox_lb_status);

		memberinfo_bt_update.setOnAction(e -> {
			UpdatePane();
		});

		memberinfo_bt_adminview.setOnAction(e -> {
			AdminView();
		});

		memberinfo_bt_message.setOnAction(e -> {

			// IO streams
			try {
				String message = null;
				Socket socket = new Socket("localhost", 6996);
				DataOutputStream flag_to_server = new DataOutputStream(socket.getOutputStream());
				flag_to_server.writeInt(6);

				DataInputStream input = new DataInputStream(socket.getInputStream());

				message = input.readUTF();
				// System.out.println("message.." + message);
				memberinfo_ta.appendText(message);

				System.out.println(">Memberinfo > destory the thread");
				flag_to_server.writeInt(-6);

				System.out.println(">Memberinfo > close the socket");

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		Scene memberinfo_scene = new Scene(memberinfo_borderpane);
		Stage memberinfo_stage = new Stage();

		memberinfo_stage.setScene(memberinfo_scene);
		memberinfo_stage.setTitle("Profile");
		memberinfo_stage.show();

	}

	/************************** Update Pane **************************/
	public void UpdatePane() {

		BorderPane update_borderpane = new BorderPane();
		GridPane update_gridpane = new GridPane();
		HBox update_hbox = new HBox();
		HBox update_hbox_status = new HBox();

		Label update_lb_username = new Label("Username:\t");
		Label update_lb_password = new Label("Password:\t");
		Label update_lb_confirm_password = new Label("Confirm Password:\t");
		Label update_lb_email = new Label("E-mail:\t");
		Label update_lb_book = new Label("Favorite Book:\t");
		Label update_lb_status = new Label("Status");

		TextField update_tf_username = new TextField();
		TextField update_tf_password = new TextField();
		TextField update_tf_confirm_password = new TextField();
		TextField update_tf_email = new TextField();
		TextField update_tf_book = new TextField();

		Button update_bt_clear = new Button(" Clear ");
		Button update_bt_update = new Button("Update");

		Scene update_scene = new Scene(update_borderpane);
		Stage update_stage = new Stage();

		Member update_member = login_member;
		System.out.println(">UpdatePane >Update_ member: " + update_member.toString());

		/************ receiver the member from the server *************/

		update_tf_username.setText(update_member.getUsername());
		update_tf_username.setEditable(false);
		update_tf_password.setText(update_member.getPassword());
		update_tf_confirm_password.setText(update_member.getPassword());
		update_tf_email.setText(update_member.getEmail());
		update_tf_book.setText(update_member.getBook());

		update_hbox.getChildren().addAll(update_bt_clear, update_bt_update);
		update_hbox.setAlignment(BASELINE_RIGHT);
		update_hbox.setPadding(new Insets(10, 10, 10, 10));
		update_hbox.setSpacing(30);
		update_hbox_status.getChildren().add(update_lb_status);
		update_hbox_status.setStyle("-fx-background-color:azure");

		update_gridpane.add(update_lb_username, 0, 0);
		update_gridpane.add(update_lb_password, 0, 1);
		update_gridpane.add(update_lb_confirm_password, 0, 2);
		update_gridpane.add(update_lb_email, 0, 3);
		update_gridpane.add(update_lb_book, 0, 4);
		update_gridpane.add(update_tf_username, 1, 0);
		update_gridpane.add(update_tf_password, 1, 1);
		update_gridpane.add(update_tf_confirm_password, 1, 2);
		update_gridpane.add(update_tf_email, 1, 3);
		update_gridpane.add(update_tf_book, 1, 4);
		update_gridpane.add(update_hbox, 0, 5, 2, 1);
		update_gridpane.setVgap(10);

		update_borderpane.setMargin(update_gridpane, new Insets(20, 90, 40, 50));
		update_borderpane.setCenter(update_gridpane);
		update_borderpane.setBottom(update_hbox_status);

		update_bt_update.setOnAction(ex -> {
			try {
				Socket socket = new Socket("localhost", 6996);
				System.out.println(">Update Pane > socket created");

				String password = update_tf_password.getText();
				String confirm = update_tf_confirm_password.getText();
				String email = update_tf_email.getText().toLowerCase();
				String book = update_tf_book.getText().toUpperCase();

				DataOutputStream toServer_flag = new DataOutputStream(socket.getOutputStream());

				// if 2 passwords are the same
				if (password.trim().equals(confirm.trim())) {

					update_member.setPassword(password);
					update_member.setEmail(email);
					update_member.setBook(book);

					toServer_flag.writeInt(3);
					toServer_flag.flush();

					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

					// sent the update_member information to the server
					ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
					toServer.writeObject(update_member);
					toServer.flush();

					DataInputStream fromServer_flag = new DataInputStream(socket.getInputStream());
					int flag = fromServer_flag.readInt();

					System.out.println(">UpdatePane > From Server update flag: " + flag);

					if (flag > 0) {
						System.out.println("> UpdatePane > Update into database successfully!");
						// System.out.println("_________UpatePane New information_______________");
						bdo.showAll();
						// System.out.println("_________________UpatePane END___________________");
						update_lb_status.setText("Status: Update successfully!");

						// reset the member info pane
						memberinfo_tf_email.setText(email);
						memberinfo_tf_book.setText(book);

					}
				} else {
					update_lb_status.setText("Status: Please check 2 passwords!");
				}

				toServer_flag.writeInt(-3);
				System.out.println(">Update Pane >  destory the thread");

				socket.close();
				System.out.println(">Update Pane > socket closed");

			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		update_bt_clear.setOnAction(ex -> {
			update_tf_password.clear();
			update_tf_confirm_password.clear();
			update_tf_email.clear();
			update_tf_book.clear();
		});

		update_stage.setScene(update_scene);
		update_stage.setTitle("Member Update");
		update_stage.show();

	}

	/************************** Register Pane **************************/
	public void RegisterPane() {

		BorderPane register_borderpane = new BorderPane();
		GridPane register_gridpane = new GridPane();
		HBox register_hbox = new HBox();
		HBox register_hbox_status = new HBox();

		Label register_lb_username = new Label("Username:\t");
		Label register_lb_password = new Label("Password:\t");
		Label register_lb_confirm_password = new Label("Confirm Password:\t");
		Label register_lb_email = new Label("E-mail:\t");
		Label register_lb_book = new Label("Favorite Book:\t");
		Label register_lb_status = new Label("Status");

		TextField register_tf_username = new TextField();
		TextField register_tf_password = new TextField();
		TextField register_tf_confirm_password = new TextField();
		TextField register_tf_email = new TextField();
		TextField register_tf_book = new TextField();

		Button register_bt_clear = new Button(" Clear ");
		Button register_bt_register = new Button("Register");

		Scene register_scene = new Scene(register_borderpane);
		Stage register_stage = new Stage();

		register_hbox.getChildren().addAll(register_bt_clear, register_bt_register);
		register_hbox.setAlignment(BASELINE_RIGHT);
		register_hbox.setPadding(new Insets(10, 10, 10, 10));
		register_hbox.setSpacing(30);
		register_hbox_status.getChildren().add(register_lb_status);
		register_hbox_status.setStyle("-fx-background-color:azure");

		register_gridpane.add(register_lb_username, 0, 0);
		register_gridpane.add(register_lb_password, 0, 1);
		register_gridpane.add(register_lb_confirm_password, 0, 2);
		register_gridpane.add(register_lb_email, 0, 3);
		register_gridpane.add(register_lb_book, 0, 4);
		register_gridpane.add(register_tf_username, 1, 0);
		register_gridpane.add(register_tf_password, 1, 1);
		register_gridpane.add(register_tf_confirm_password, 1, 2);
		register_gridpane.add(register_tf_email, 1, 3);
		register_gridpane.add(register_tf_book, 1, 4);
		register_gridpane.add(register_hbox, 0, 5, 2, 1);
		register_gridpane.setVgap(10);

		register_borderpane.setMargin(register_gridpane, new Insets(20, 90, 40, 50));
		register_borderpane.setCenter(register_gridpane);
		register_borderpane.setBottom(register_hbox_status);

		register_bt_register.setOnAction(e -> {
			try {
				Socket socket = new Socket("localhost", 6996);
				System.out.println(">RegisterPane > socket created");

				String username = register_tf_username.getText();
				String password = register_tf_password.getText();
				String confirm = register_tf_confirm_password.getText();
				String email = register_tf_email.getText().toLowerCase();
				String book = register_tf_book.getText().toUpperCase();

				DataOutputStream toServer_flag = new DataOutputStream(socket.getOutputStream());
				toServer_flag.writeInt(2);

				// if 2 passwords are the same
				if (password.equals(confirm)) {
					// IO streams
					ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

					Member member = new Member(0, username, password, email, book);
					// send the information to the sever and register in to database;
					toServer.writeObject(member);
					toServer.flush();

					System.out.println("> RegisterPane > Client: new member information >> Server");

					DataInputStream fromServer_flag = new DataInputStream(socket.getInputStream());
					int flag = fromServer_flag.readInt();

					System.out.println("> RegisterPane > From Server register flag: " + flag);

					if (flag == 99) {
						register_lb_status.setText("Status: Register Successfully!");
						System.out.println("> RegisterPane > Register into database successfully!");
						bdo.showAll();
					}

				} else {
					register_lb_status.setText("Status: Please check 2 passwords!");
				}

				toServer_flag.writeInt(-2);
				System.out.println("> RegisterPane > destory the thread");
				socket.close();
				System.out.println("> RegisterPane > socket closed");

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		register_bt_clear.setOnAction(e -> {
			register_tf_username.clear();
			register_tf_password.clear();
			register_tf_confirm_password.clear();
			register_tf_email.clear();
			register_tf_book.clear();
		});
		register_stage.setScene(register_scene);
		register_stage.setTitle("Register");
		register_stage.show();
	}

	/************************** AdminView Pane **************************/
	public void AdminView() {

		BorderPane adminview_borderpane = new BorderPane();
		TableView<MemberDemo> adminview_table = new TableView<MemberDemo>();
		TableView<BookListDemo> adminview_book_table = new TableView<BookListDemo>();

		Label adminview_lb_title = new Label("BOOK CLUB");
		Label adminview_lb_list = new Label("LIST");

		adminview_lb_title.setFont(new Font("Arial", 18));
		adminview_lb_list.setFont(new Font("Arial", 18));

		VBox adminview_vbox = new VBox();
		VBox adminview_vbox_right = new VBox();

		adminview_borderpane.setCenter(adminview_vbox);
		adminview_borderpane.setRight(adminview_vbox_right);

		ObservableList<MemberDemo> adminview_memberlist = FXCollections.observableArrayList();
		ObservableList<BookListDemo> adminview_booklist = FXCollections.observableArrayList();

		Button bt_send_message = new Button(" Send Message");

		Scene adminview_scene = new Scene(new Group());
		Stage adminview_stage = new Stage();

		ArrayList<Member> adminview_memberlist_al = new ArrayList<Member>(25);
		ArrayList<Book> adminview_booklist_al = new ArrayList<Book>();

		// Create a socket to connect to the server

		/*** get the list from database */
		try {
			Socket socket = new Socket("localhost", 6996);
			System.out.println(">Adminview > socket created");

			DataOutputStream toServer_flag = new DataOutputStream(socket.getOutputStream());
			toServer_flag.writeInt(4);

			ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

			// read member array from server
			Member[] member_from_server = (Member[]) fromServer.readObject();

			for (int i = 0; i < member_from_server.length; i++) {
				int id = member_from_server[i].getId();
				String username = member_from_server[i].getUsername();
				String password = member_from_server[i].getPassword();
				String email = member_from_server[i].getEmail();
				String book = member_from_server[i].getBook();
				MemberDemo memberdemo = new MemberDemo(id, username, password, email, book);
				// change member to memberdemo
				adminview_memberlist.add(memberdemo);
			}

			Book[] book_from_server = (Book[]) fromServer.readObject();

			for (int i = 0; i < book_from_server.length; i++) {
				int amount = book_from_server[i].getAmount();
				String bookname = book_from_server[i].getBookname();
				BookListDemo book = new BookListDemo(bookname, amount);
				adminview_booklist.add(book);
			}

			System.out.println();
			System.out.println(">AdminView > Change to OberservableList Done");

			toServer_flag.writeInt(-4);
			System.out.println(">Adminview > destory the thread");

			socket.close();
			System.out.println(">Adminview > socket closed");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// TableColumn cl_id = new TableColumn("ID");
		TableColumn<MemberDemo, String> cl_username = new TableColumn<MemberDemo, String>("Username");
		cl_username.setPrefWidth(100);
		cl_username.setCellValueFactory(new PropertyValueFactory<MemberDemo, String>("username"));

		TableColumn<MemberDemo, String> cl_password = new TableColumn<MemberDemo, String>("Password");
		cl_password.setPrefWidth(100);
		cl_password.setCellValueFactory(new PropertyValueFactory<MemberDemo, String>("password"));

		TableColumn<MemberDemo, String> cl_email = new TableColumn<MemberDemo, String>("Email");
		cl_email.setPrefWidth(200);
		cl_email.setCellValueFactory(new PropertyValueFactory<MemberDemo, String>("email"));

		TableColumn<MemberDemo, String> cl_book = new TableColumn<MemberDemo, String>("Book");
		cl_book.setPrefWidth(200);
		cl_book.setCellValueFactory(new PropertyValueFactory<MemberDemo, String>("book"));

		adminview_table.setItems(adminview_memberlist);
		adminview_vbox.setSpacing(5);
		adminview_vbox.setPadding(new Insets(10, 10, 10, 10));
		adminview_vbox.getChildren().addAll(adminview_lb_title, adminview_table);
		adminview_table.getColumns().addAll(cl_username, cl_password, cl_email, cl_book);

		TableColumn<BookListDemo, String> cl_bookname = new TableColumn<BookListDemo, String>("Book Name");
		cl_bookname.setPrefWidth(200);
		cl_bookname.setCellValueFactory(new PropertyValueFactory<BookListDemo, String>("bookname"));

		TableColumn<BookListDemo, Integer> cl_bookamount = new TableColumn<BookListDemo, Integer>("Amount");
		cl_bookamount.setPrefWidth(70);
		cl_bookamount.setCellValueFactory(new PropertyValueFactory<BookListDemo, Integer>("amount"));

		adminview_book_table.setItems(adminview_booklist);
		adminview_vbox_right.setSpacing(5);
		adminview_vbox_right.setPadding(new Insets(10, 10, 10, 10));
		adminview_vbox_right.getChildren().addAll(adminview_lb_list, adminview_book_table, bt_send_message);
		adminview_vbox_right.setSpacing(5);
		// adminview_vbox_right.setAlignment(CENTER_RIGHT);

		adminview_book_table.setPrefSize(270, 150);
		adminview_book_table.getColumns().addAll(cl_bookname, cl_bookamount);

		// send message to all client
		bt_send_message.setOnAction(e -> {
			try {
				Socket socket = new Socket("localhost", 6996);
				System.out.println(">Adminview > prepare message > socket created");

				// message to server to send information to all client
				DataOutputStream toServer_flag = new DataOutputStream(socket.getOutputStream());
				toServer_flag.writeInt(5);
				toServer_flag.flush();

				System.out.println(">Adminview > prepare message > destory the thread");
				toServer_flag.writeInt(-5);
				socket.close();
				System.out.println(">Adminview > prepare message > socket closed");

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		((Group) adminview_scene.getRoot()).getChildren().addAll(adminview_borderpane);

		adminview_stage.setTitle("Administrator View");
		adminview_stage.setScene(adminview_scene);
		adminview_stage.show();

	}

}
