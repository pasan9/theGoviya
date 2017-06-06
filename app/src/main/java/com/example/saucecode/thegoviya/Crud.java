package com.example.saucecode.thegoviya; /**
 * Created by buwaneka on 04/05/2017.
 */

import java.sql.*;
import java.util.ArrayList;


public class Crud {

    private String connectionString = "";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public Crud(){
        this.connectionString = "jdbc:jtds:sqlserver://projectgoviya.database.windows.net:1433/goviyaDB;instance=SQLEXPRESS;user=hgiedse@projectgoviya;password=";

    }


    public String connectDBLogin(String query) {

        String type = "";

        CurrentUser user = new CurrentUser();

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString);

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next())
            {
                user.setNicNumber(resultSet.getString(1));
                user.setfName(resultSet.getString(2));
                user.setMobileNumber(Integer.parseInt(resultSet.getString(3)));
                user.setAddress(resultSet.getString(4));
                user.setType(resultSet.getString(6));
                type = resultSet.getString(6);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Close the connections after the data has been handled
            if (resultSet != null)try {resultSet.close();} catch (Exception e) {}
            if (statement != null) try { statement.close(); } catch(Exception e) {}
            if (connection != null) try { connection.close(); } catch(Exception e) {}
        }
            return type;
    }

    public void insertData(String query){
        try {
            System.out.println(query);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString);

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Close the connections after the data has been handled
            if (resultSet != null)try {resultSet.close();} catch (Exception e) {}
            if (statement != null) try { statement.close(); } catch(Exception e) {}
            if (connection != null) try { connection.close(); } catch(Exception e) {}
        }

       // return added;
    }

    public ArrayList<Products> selectData(String query){
        ArrayList<Products> prodList = new ArrayList<Products>();
        try {
            System.out.println(query);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString);

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {

                Products prod = new Products(resultSet.getInt(1),resultSet.getString(2),resultSet.getDouble(3),resultSet.getDouble(4),resultSet.getDouble(5),resultSet.getString(6),resultSet.getString(7),"");
                prodList.add(prod);
                //System.out.println(resultSet.getInt(1)+" "+resultSet.getString(2)+" "+resultSet.getDouble(3)+" "+resultSet.getDouble(4)+" "+resultSet.getDouble(5)+" "+resultSet.getString(6)+" "+resultSet.getString(7) );
            }

        }
        catch (Exception e) {
            e.printStackTrace();

        }

        return prodList;

    }


}