
package collegeadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection getConnection() {

        Properties prop = new Properties();

        try {
            FileInputStream fis = new FileInputStream("db.properties");
            prop.load(fis);
            fis.close();

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String password = prop.getProperty("db.password");

            Connection con = DriverManager.getConnection(
                    url, user, password);

            return con;

        } catch (IOException | SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {

        Connection con = getConnection();

        if (con != null) {
            System.out.println("Database Connected Successfully!");

            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
