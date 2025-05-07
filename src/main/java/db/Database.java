package db;
import java.sql.Connection;
import java.sql.DriverManager;
 import java.sql.SQLException;
 
public class Database{

  private static String url = "jdbc:mysql://localhost/bank";
  private static String user = "root";
  private static String password = "Salami*06";
  
  private Database(){
  }
  public static Connection getConnection() throws SQLException{
  Connection connection = null;
  connection = DriverManager.getConnection(url,user,password);
  return connection;
  }
}