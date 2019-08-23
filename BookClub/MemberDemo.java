package BookClub;

import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MemberDemo  implements Serializable{

	private SimpleIntegerProperty id;
	private SimpleStringProperty username;
	private SimpleStringProperty password;
	private SimpleStringProperty email;
	private SimpleStringProperty book;

	public MemberDemo(int id, String username, String password, String email, String book) {
		super();
		this.id = new SimpleIntegerProperty(id);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.email = new SimpleStringProperty(email);
		this.book = new SimpleStringProperty(book);
	}

	public int getId() {
		return id.get();
	}

	public void setID(int id) {
		this.id.set(id);

	}

	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
		;
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
	}

	public String getBook() {
		return book.get();
	}

	public void setBook(String book) {
		this.book.set(book);
	}

	@Override
	public String toString() {
		return "MemberDemo [username=" + getUsername() + ", password=" + getPassword() + ", email=" + getEmail()
				+ ", book=" + getBook() + "]";
	}

}
