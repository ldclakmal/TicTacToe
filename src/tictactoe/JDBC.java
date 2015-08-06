package tictactoe;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Chanaka
 */
public class JDBC {

    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/tictactoe";

    static Connection con;

    Connection getConnection() throws Exception {
        Class.forName(driver);
        if (con == null) {
            con = DriverManager.getConnection(url, "root", "123");
        }
        return con;
    }

    public void putData(String sql) {
        try {
            Statement state = getConnection().createStatement();
            state.executeUpdate(sql);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public ResultSet getData(String sql) throws Exception {
        Statement state = getConnection().createStatement();
        ResultSet rset = state.executeQuery(sql);
        return rset;
    }
}
