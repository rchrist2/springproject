package myproject;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class ConnectionClass {
    public static Connection connect(){
        Connection c = null;
        String url = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;database=4375db";
        Properties props= new Properties();
        FileInputStream input = null;
        try
        {
            input = new FileInputStream("src/main/resources/application.properties");
            props.load(input );
            c = DriverManager.getConnection(props.getProperty("spring.datasource.url"),
                    props.getProperty("spring.datasource.username"),
                    props.getProperty("spring.datasource.password"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
}