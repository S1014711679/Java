package  BookClub;

import java.io.*;
import java.sql.*;

public class MysqlTest {

    /**
     * Connect to MySQL and read the table "Cats", then print the contents of the first column.
     */

    public static void main(String[] args) {
        try
        {
            String url = "jdbc:mysql://localhost/pets?user=Guest&password=Guest123";
            //Create a connection to the database
            Connection conn = DriverManager.getConnection(url);
            System.out.println("The Database connected successfully!");

            // Print all warnings
            for (SQLWarning warn = conn.getWarnings(); warn != null; warn = warn.getNextWarning()) {
                System.out.println("SQL Warning:");
                System.out.println("State  : " + warn.getSQLState());
                System.out.println("Message: " + warn.getMessage());
                System.out.println("Error  : " + warn.getErrorCode());
            }

            // create a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query
            stmt.executeUpdate("DELETE from cats where name = 'Charlie'");
            stmt.executeUpdate("INSERT INTO cats (name, owner,birth) values ('Charlie','River', '2016-06-02')");
            // Execute the query
            ResultSet rs = stmt.executeQuery("SELECT * FROM cats");

            // Loop through the result set
            System.out.println("start looping through the result set");
            while (rs.next()){
                System.out.print(rs.getString(1)+"\t");
                System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getString(3));
                System.out.println();
            }
            
            System.out.println("end of loop");
            // Close the result set, statement and the connection
            rs.close();

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            System.out.println("SQL Exception:");

            // Loop through the SQL Exceptions
            while (se != null) {
                System.out.println("State  : " + se.getSQLState());
                System.out.println("Message: " + se.getMessage());
                System.out.println("Error  : " + se.getErrorCode());

                se = se.getNextException();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("________________________Program done!__________________________");


    }
}