package BookClub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookDataOperation {

	private static String url = "jdbc:mysql://localhost/bookclub?user=Guest&password=Guest123";
	// Create a connection to the database
	private Connection conn = null;

	// Register information to the database
	public void RegisterInfo(Member member) {
		// connect to the database
		try {
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > The Database connected successfully!");
			// create a statement from the connection
			Statement stmt = conn.createStatement();

			String str = "insert into member(id,username,password,email,book) values ";
			String values = "(" + member.getId() + ",'" + member.getUsername() + "','" + member.getPassword() + "','"
					+ member.getEmail() + "','" + member.getBook() + "');";
			String insert_str = str + values;
			stmt.executeUpdate(insert_str);

			stmt.close();
			conn.close();
			System.out.println(">Database > Register information successfully!");
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	// public ObservableList<BookListDemo> showBookList() {
	public ArrayList<Book> showBookList() {

		ArrayList<Book> data = new ArrayList<Book>();

		// to store the book information
		Map<String, Integer> bookmap = null;
		String book = null;
		ArrayList<Entry<String, Integer>> list = null;
		Book booklist_element = null;

		// connect to the database
		try {
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > The Database connected successfully!");
			bookmap = new TreeMap<String, Integer>();
			// create a statement from the connection
			Statement stmt = conn.createStatement();
			// Execute the query
			ResultSet rs = stmt.executeQuery("select book from member");
			while (rs.next()) {
				book = rs.getString(1);
				if (!bookmap.containsKey(book)) {
					bookmap.put(book, 1);
				} else {
					int value = bookmap.get(book);
					value++;
					bookmap.put(book, value);
				}
			}
			list = new ArrayList<Map.Entry<String, Integer>>(bookmap.entrySet());
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e);
		}

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});

		for (int i = 0; i < 3; i++) {
			Entry<String, Integer> id = list.get(i);
			System.out.println(">Database >" + id.getKey() + " >> " + id.getValue());
			booklist_element = new Book(id.getKey(), id.getValue());
			data.add(booklist_element);
		}

		System.out.println(">Database > showBookList() >> " + data.toString());
		System.out.println(">Database > Query all books list successfully!");

		return data;

	}

	// query all member information and return as an observable list
	// public ObservableList<MemberDemo> memberList() {
	public ArrayList<Member> memberList() {

		ArrayList<Member> data = new ArrayList<Member>();
		Member member = null;
		int id = 0;
		String username = null;
		String password = null;
		String email = null;
		String book = null;

		// connect to the database
		try {
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > The Database connected successfully!");
			// create a statement from the connection
			Statement stmt = conn.createStatement();

			// Execute the query
			ResultSet rs = stmt.executeQuery("select * from member");

			while (rs.next()) {
				username = rs.getString(2);
				password = rs.getString(3);
				email = rs.getString(4);
				book = rs.getString(5);
				member = new Member(id, username, password, email, book);
				data.add(member);
			}

			System.out.println(">Database > memberlist() >> " + data.toString());

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			System.out.println(">Database > memberList() >> Query all information successfully!");

		} catch (SQLException e) {
			System.err.println(">Database >" + e);
		}

		return data;

	}

	// Query all the information
	public void showAll() {
		// connect to the database
		try {
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > The Database connected successfully!");
			// create a statement from the connection
			Statement stmt = conn.createStatement();

			// Execute the query
			ResultSet rs = stmt.executeQuery("select * from member");

			while (rs.next()) {
				System.out.print(rs.getString(1) + "\t");
				System.out.print(rs.getString(2) + "\t");
				System.out.print(rs.getString(3) + "\t");
				System.out.print(rs.getString(4) + "\t");
				System.out.print(rs.getString(5));
				System.out.println();
			}
			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			System.out.println(">Database > Query all information successfully!");
		} catch (SQLException e) {
			System.err.println(">Database >" + e);
		}
	}

	// Query the login information
	public Member LoginQuery(int login_id, String username, String password) {

		Member member = null;

		// connect to the database
		try {
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > LoginQuery > The Database connected successfully!");
			// create a statement from the connection
			Statement stmt = conn.createStatement();

			int id_db = 0;
			String username_db = null;
			String password_db = null;
			String email_db = null;
			String book_db = null;

			ResultSet rs = null;

			// Execute the query
			rs = stmt.executeQuery("select * from member where username = '" + username + "';");

			while (rs.next()) {
				System.out.print(rs.getString(1) + "\t");
				id_db = Integer.parseInt(rs.getString(1));
				System.out.print(rs.getString(2) + "\t");
				username_db = rs.getString(2);
				System.out.print(rs.getString(3) + "\t");
				password_db = rs.getString(3);
				System.out.print(rs.getString(4) + "\t");
				email_db = rs.getString(4);
				System.out.print(rs.getString(5) + "\t");
				book_db = rs.getString(5);
				System.out.println();
			}

			// login as the administrator find the username
			if (id_db == 1 && username_db.equals(username)) {
				System.out.println(">Database > LoginQuery as Administrator >>> Find the username");
				// check the password
				if (password_db.equals(password)) {
					System.out.println(">Database > LoginQuery as Administrator >>> Successfully!");
				} else {
					System.out.println(">Database > LoginQuery as Administrator >>> Wrong password");
				}

				// login as the ordinary member
			} else if (id_db == 0 && username_db.equals(username)) {
				System.out.println(">Database > LoginQuery as Ordinary Member >>> Find the username");
				// check the password
				if (password_db.equals(password)) {
					System.out.println(">Database > LoginQuery as Ordinary Member >>> Successfully!");
				} else {
					System.out.println(">Database > LoginQuery as Ordinary Member  >>> Wrong password");
				}
			} else {
				// member is not in the system
				System.out.println(">Database > LoginQuery >>> Didn't find the username");
			}

			member = new Member(id_db, username_db, password_db, email_db, book_db);

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(">Database >" + e);
		}

		return member;
	}

	// Update the information
	public void UpdateInfo(Member member) {

		String username = member.getUsername();

		System.out.println(">Database >" + username);

		// find the member information
		try {
			// connect to the database
			conn = DriverManager.getConnection(url);
			System.out.println(">Database > UpdateQuery >> The Database connected successfully!");
			// create a statement from the connection
			Statement stmt = conn.createStatement();

			int id = member.getId();
			String password = member.getPassword();
			String email = member.getEmail();
			String book = member.getBook();

			String update = "update member set id = " + id + " , password = '" + password + "' , email = '" + email
					+ "' , book = '" + book + "' where username = '" + username + "';";

			// execute the update base on the username
			stmt.executeUpdate(update);

			System.out.println(">Database > Update successfully");

			// Close the statement and the connection
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e);
		}

	}

	// public static void main(String[] args) {
	//
	// ArrayList<Entry<String, Integer>> list = showBookList();
	//
	// for (int i = 0; i < list.size(); i++) {
	// Entry<String, Integer> id = list.get(i);
	// System.out.println(id.getKey() + " >> " + id.getValue());
	// }
	// }

}
