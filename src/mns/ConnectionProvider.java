
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mns;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author mulan
 */
public class ConnectionProvider {
    public static Connection getCon(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/medical?useSSL=false";
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");
            Connection con = DriverManager.getConnection(url, user, pass);
            return con;
        } catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }
}

