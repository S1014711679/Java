package BookClub;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class BookClubServer extends Application {

	protected BookDataOperation bdo = new BookDataOperation();
	// Text area for displaying contents
	private TextArea ta = new TextArea();
	// number of clients
	private int clientNo = 0;

	Member member_current_login = new Member();

	HashSet<String> member_insystem_set = new HashSet<String>();

	String information = "";

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(ta), 450, 200);
		primaryStage.setTitle("BookClub System Server");
		primaryStage.setScene(scene);
		primaryStage.show();

		new Thread(() -> {

			try {
				// create a server socket
				ServerSocket serverSocket = new ServerSocket(6996);
				ta.appendText("Book Club System Server started at " + new Date() + '\n');
				System.out.println("Book Club System Server started");

				while (true) {
					// listen for a connection request
					Socket socket = serverSocket.accept();
					System.out.println(">> Server > ClientNo \t" + clientNo);
					// get flag from Client to decide request thread
					DataInputStream inputFromClient_flag = new DataInputStream(socket.getInputStream());
					int flag_client = inputFromClient_flag.readInt();

					System.out.println(">> Server > flag >: " + flag_client);
					Thread login_thread = new Thread(new ServerThread_Login(socket));
					Thread register_thread = new Thread(new ServerThread_Register(socket));
					Thread update_thread = new Thread(new ServerThread_Update(socket));
					Thread admin_viewList = new Thread(new ServerThread_AdminView(socket));
					Thread admin_message = new Thread(new ServerThread_Admiview_Message(socket));
					Thread message = new Thread(new ServerThread_Message(socket));

					switch (flag_client) {
					case 1:
						System.out.println(">> LoginPane*Server > Login Thread created!");
						login_thread.start();
						break;

					case 2:
						System.out.println("*>> Reigster*Server > Register Thread created!");
						register_thread.start();
						break;

					case 3:
						System.out.println(">> UpdatePane*Server > Update Thread created!");
						update_thread.start();
						break;

					case 4:
						System.out.println(">> AdminView*Server: >>> AdminView List created!");
						admin_viewList.start();
						break;

					case 5:
						System.out.println(">> AdminView*Sever: >>> Send information to all Client");
						admin_message.start();
						break;

					case 6:
						System.out.println(">> Server > send information to all client");
						message.start();
						break;

					case -1:
						System.out.println(">> For LoginPane > Close the login thread!");
						login_thread.destroy();
						break;

					case -2:
						System.out.println(">> For RegisterPane > Close the register thread!");
						register_thread.destroy();
						break;

					case -3:
						System.out.println(">> For UpdatePane > Close the update thread!");
						update_thread.destroy();
						break;

					case -4:
						System.out.println(">> For AdminView > Close the admin view thread!");
						admin_viewList.destroy();
						break;

					case -5:
						System.out.println(">> For AdminView > Close the admin send message thread!");
						admin_message.destroy();
						break;

					case -6:
						System.out.println(">> For Message > close the sned message thread!");
						message.destroy();
						break;

					default:
						break;
					}// end of switch

					Platform.runLater(() -> {
						ta.appendText("Starting a thread for client at " + new Date() + '\n');
					});
				} // end of while

			} catch (IOException e) {
				System.err.println(e);
			}

		}).start();

	}

	class ServerThread_Register implements Runnable {
		private Socket client_register = null;

		public ServerThread_Register(Socket client) {
			this.client_register = client;
		}

		@Override
		public void run() {

			try {
				ObjectInputStream inputFromClient = new ObjectInputStream(client_register.getInputStream());
				System.out.println("*Server: Client >> Register member information >>> Server");
				// read from the input
				Member member_register = (Member) inputFromClient.readObject();
				member_current_login = member_register;

				System.out.println(">Server > member currrent login > " + member_current_login.toString());

				// if no new username then add to the set
				if (!member_insystem_set.contains(member_current_login.getUsername())) {
					member_insystem_set.add(member_current_login.getUsername());
				}

				System.out.println("*Server: Client >> register member : " + member_register.toString());

				bdo.RegisterInfo(member_register);

				DataOutputStream outputToClient_flag = new DataOutputStream(client_register.getOutputStream());
				outputToClient_flag.writeInt(99);
				outputToClient_flag.flush();

				/************** Send object information to the Client *************/
				Platform.runLater(() -> {
					ta.appendText("Register information:\n");
					ta.appendText("Register Account:" + member_register.getUsername() + "\n");
					ta.appendText(member_register.toString() + "\n");
					ta.appendText("________________________________________________________\n");
				});

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	class ServerThread_Login implements Runnable {
		private Socket client_login = null;

		public ServerThread_Login(Socket client) {
			this.client_login = client;
		}

		@Override
		public void run() {
			try {

				ObjectOutputStream outputToClient = new ObjectOutputStream(client_login.getOutputStream());
				ObjectInputStream inputFromClient = new ObjectInputStream(client_login.getInputStream());

				System.out.println("*Server: Client >>> Login information >>> Server");

				Member member = (Member) inputFromClient.readObject();

				if (!member_insystem_set.contains(member.getUsername())) {
					member_insystem_set.add(member.getUsername());
					clientNo++;
				}

				// get id,username,password from Member
				int login_id = member.getId();
				String login_username = member.getUsername();
				String login_password = member.getPassword();

				Platform.runLater(() -> {
					ta.appendText("Account: " + login_username + "\t try to login the system!\n");
				});

				// check the database to find the member information form the database
				Member login_member = bdo.LoginQuery(login_id, login_username, login_password);

				/******************** Send object information to the Client *****************/
				if (login_member != null) {
					System.out.println(login_member.toString());
					System.out.println("*Server: Server >>> Login Member in Database >>>> Client");
					outputToClient.writeObject(login_member);

					/*** display to the text area in the server side ***/
					Platform.runLater(() -> {
						ta.appendText("_____________________________________________________\n");
						// Find the client's host name, and IP address
						InetAddress inetAddress = client_login.getInetAddress();
						ta.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
						ta.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
						ta.appendText("Account : " + login_member.toString() + "is found in the system \n");
						ta.appendText("_____________________________________________________\n");

					});
				} else {
					Platform.runLater(() -> {
						ta.appendText("Account : " + member.toString() + "is not found in the system \n");
						ta.appendText("_____________________________________________________\n");
					});

				}

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	class ServerThread_Update implements Runnable {
		private Socket client_update = null;

		public ServerThread_Update(Socket client) {
			this.client_update = client;
		}

		@Override
		public void run() {
			try {

				ObjectInputStream inputFromClient = new ObjectInputStream(client_update.getInputStream());
				System.out.println("*Server: Client >>> update member information >>>> Server");
				// read from the input
				Member update_member = (Member) inputFromClient.readObject();

				System.out.println("*Server: update member information: " + update_member.toString());
				bdo.UpdateInfo(update_member);

				DataOutputStream outputToClient_flag = new DataOutputStream(client_update.getOutputStream());
				outputToClient_flag.writeInt(99);
				outputToClient_flag.flush();

				/********************* Send object information to the Client *****************/
				// create data output to client show the status
				Platform.runLater(() -> {
					ta.appendText("Update information:\n");
					ta.appendText("Account : " + update_member.getUsername() + "\n");
					ta.appendText(update_member.toString() + "\n");
					ta.appendText("________________________________________________________\n");
				});
				client_update.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	class ServerThread_AdminView implements Runnable {
		private Socket client_adminview = null;

		public ServerThread_AdminView(Socket client) {
			this.client_adminview = client;
		}

		@Override
		public void run() {

			try {

				System.out.println("*Server: Client >>> All member information >>> Server");
				/******************** Send object information to the Client *****************/
				ArrayList<Member> adminview_memberlist = bdo.memberList(); // get from database
				ArrayList<Book> adminview_booklist = bdo.showBookList();
				Member[] member_to_client = new Member[adminview_memberlist.size()];
				Book[] book_to_client = new Book[adminview_booklist.size()];
				ObjectOutputStream outputToClient = new ObjectOutputStream(client_adminview.getOutputStream());

				for (int i = 0; i < member_to_client.length; i++) {
					member_to_client[i] = adminview_memberlist.get(i);
				}
				outputToClient.writeObject(member_to_client);

				for (int i = 0; i < book_to_client.length; i++) {
					book_to_client[i] = adminview_booklist.get(i);
				}

				outputToClient.writeObject(book_to_client);
				System.out.println("*Server: >>> showBookList() Send to Client Done! ");
				/*** display to the text area in the server side ***/
				Platform.runLater(() -> {
					ta.appendText("Account : ADMIN \t View all the member information! \n");
					ta.appendText("_____________________________________________________\n");
				});

				outputToClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	class ServerThread_Admiview_Message implements Runnable {
		private Socket client_message = null;

		public ServerThread_Admiview_Message(Socket client) {
			this.client_message = client;
		}

		@Override
		public void run() {
			System.out.println("*Server: Client >>> All member information >>> Server");
			ArrayList<Member> adminview_memberlist = bdo.memberList(); // get from database
			Member[] member_to_client = new Member[adminview_memberlist.size()];
			for (int i = 0; i < member_to_client.length; i++) {
				member_to_client[i] = adminview_memberlist.get(i);
				String username = member_to_client[i].getUsername();
				String book = member_to_client[i].getBook();
				information = information + "Memeber:\t" + username + "\tFavorite Book:\t" + book + "\n";
			}
			System.out.println(">Server > store the information");
		}
	}

	class ServerThread_Message implements Runnable {
		private Socket client_message = null;

		public ServerThread_Message(Socket client) {
			this.client_message = client;
		}

		@Override
		public void run() {

			try {
				System.out.println(">Server > send information to the board");
				DataOutputStream output = new DataOutputStream(client_message.getOutputStream());
				output.writeUTF(information);
				output.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		launch(args);
	}

}
