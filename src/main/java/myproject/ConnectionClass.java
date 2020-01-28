package myproject;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class ConnectionClass {
    public static Connection connect(){
        Connection c = null;
        String url = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;database=4375db";
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            c = DriverManager.getConnection(url, "sa", "1234");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
}