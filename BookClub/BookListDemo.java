package BookClub;

import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BookListDemo implements Serializable {

	private SimpleStringProperty bookname;
	private SimpleIntegerProperty amount;

	public BookListDemo(String bookname, int amount) {
		super();
		this.bookname = new SimpleStringProperty(bookname);
		this.amount = new SimpleIntegerProperty(amount);
	}

	public int getAmount() {
		return amount.get();
	}

	public void setAmount(int amount) {
		this.amount.set(amount);
	}

	public String getBookname() {
		return bookname.get();
	}

	public void setUsername(String bookname) {
		this.bookname.set(bookname);
	}

	@Override
	public String toString() {
		return "BookListDemo [bookname=" + getBookname() + ", amount=" + getAmount() + "]";
	}

}
