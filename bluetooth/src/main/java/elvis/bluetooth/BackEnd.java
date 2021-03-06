package elvis.bluetooth;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BackEnd {
    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static final String SERVER = "jdbc:mysql://group4database.cf7zjl1loo3b.ca-central-1.rds.amazonaws.com:3306/group4";
    private static final String USER_NAME = "group4";
    private static final String PASSWORD = "litony0426";
    private static String ins;

    static public boolean addUser(User user) {
        Statement stmt = null;
        try {
            Class.forName(DRIVER_NAME);
            Connection myConn = DriverManager.getConnection(SERVER, USER_NAME, PASSWORD);
            stmt = myConn.createStatement();
            String sql = ("SELECT * FROM userinfo WHERE email='" + user.getEmail() + "'");
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            while (rs.next()){
                count++;
            }
            if(count>0)
                return false;
            PreparedStatement st =  myConn.prepareStatement("insert into userinfo values (?,?,NULL)");

            st.setString(1, user.getEmail());
            st.setString(2, user.getPassword());
            st.execute();
            st.close();
            myConn.close();
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
        return true;
    }
    /**
     @param email - email user entered
     @param entPassword - password user entered
     @return if email matches the password, select_return User, else select_return null
     */
    static public User checkLogin(String email, String entPassword) {
        Connection myConn = null;
        Statement stmt = null;
        try {
            Class.forName(DRIVER_NAME);
            myConn = DriverManager.getConnection(SERVER, USER_NAME, PASSWORD);
            stmt = myConn.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM userinfo WHERE email='" + email + "'AND password='" + entPassword+"'");
            ResultSet rs = stmt.executeQuery(sql.toString());

            int count = 0;
            while (rs.next()){
                count++;
            }

            if(count == 1) {
                System.out.println("find");
                rs.first();
                User user = new User(rs.getString("email"), rs.getString("password"), rs.getInt("userId"));
                stmt.close();
                myConn.close();
                return user;
            }
            else {
                stmt.close();
                myConn.close();
                return null;
            }
        }
        catch (Exception exc) {
            exc.printStackTrace();

            if(stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            if(myConn != null)
                try {
                    myConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public static void changePsd(String email, String newPsd) {
        Connection myConn = null;
        Statement stmt = null;
        try{
            Class.forName(DRIVER_NAME);
            myConn = DriverManager.getConnection(SERVER, USER_NAME, PASSWORD);
            stmt = myConn.createStatement();
            String sql = "UPDATE userinfo SET password=" + newPsd + " WHERE email=" + email;
            stmt.executeUpdate(sql);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    static public void addPost(byte[] imgBuffer) {
        Connection myConn = null;
        PreparedStatement st = null;
        try {
            Class.forName(DRIVER_NAME);
            myConn = DriverManager.getConnection(SERVER, USER_NAME, PASSWORD);
            st =  myConn.prepareStatement("insert into images values (NULL,?,NULL,?)");
            st.setString(2,Current.getCurUserEmail());
            /* prepare image blob */
            Blob blob = myConn.createBlob();
            blob.setBytes(1, imgBuffer);
            st.setBlob(1, blob);
            blob.free();

            st.execute();
            //myConn.commit();
            st.close();
            myConn.close();
        }
        catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally {
            //finally block used to close resources
            try {
                if (st != null)
                    st.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (myConn != null)
                    myConn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    static public String getInstrument(String email) {
        Connection myConn = null;
        Statement stmt = null;
        try {
            Class.forName(DRIVER_NAME);
            myConn = DriverManager.getConnection(SERVER, USER_NAME, PASSWORD);
            stmt = myConn.createStatement();

            String query =
                    "SELECT * FROM images WHERE email = '" +email + "' ORDER BY id DESC LIMIT 1" ;
            System.out.println(query);

            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            ins = rs.getString("result");
            System.out.println("It is a "+ins+" !");
            StartPage.insUpdated = true;
            StartPage.instrumrnt = ins;
            rs.close();
            myConn.close();
            stmt.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (myConn != null)
                    myConn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ins;
    }

}
