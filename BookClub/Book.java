package BookClub;

import java.io.Serializable;

public class Book implements Serializable {

	private int amount;
	private String bookname;

	public Book(String bookname, int amount) {
		super();
		this.amount = amount;
		this.bookname = bookname;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	@Override
	public String toString() {
		return "Book [amount=" + amount + ", bookname=" + bookname + "]";
	}

}
