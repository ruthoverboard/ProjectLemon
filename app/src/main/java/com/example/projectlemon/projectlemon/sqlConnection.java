package com.example.projectlemon.projectlemon;

/**
 * Created by rbeli on 11/21/2016.
 */

import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import net.sourceforge.jtds.jdbc.*;
import java.sql.*;


public class sqlConnection {

    String connString = "cetyscarpool.c6aodczcoukl.us-east-1.rds.amazonaws.com"; //ip
    String db = "prjctLemon";
    String userName = "admin";
    String pass = "Lemon2016";

    public void main(String[] args) {
        String connString = "cetyscarpool.c6aodczcoukl.us-east-1.rds.amazonaws.com"; //ip
        String db = "prjctLemon";
        String userName = "admin";
        String pass = "Lemon2016";
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            con = connectionclass(userName, pass, db, connString);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    private Connection connectionclass(String userName, String pass, String db, String connString) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String conUrl = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conUrl = "jdbc:jtds:sqlserver://" + connString + ";" + "databaseName=" + db + ";user=" + userName + ";password=" + pass + ";";
            conn = DriverManager.getConnection(conUrl);
        }
        catch(Exception e){
            Log.e("error: ", e.getMessage());
        }

        return conn;
    }


    public String[] findUserData(){
        String[] data = new String[5];
        String msg;
        Connection con = null;

        try{
            con = connectionclass(userName, pass, db, connString);
            if(con == null){
                msg = "Oops";
            }
            else{
                String query = "";
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }
}
