package gothos.DatabaseCore;

import gothos.Start;

import java.sql.*;
import java.util.ArrayList;

public class SqliteConnection {

    protected String file;
    protected Connection connection;

    public SqliteConnection(String file) throws Exception {
        this.file = file;

        if(file.equals("")){
            throw new Exception("No database file provided");
        }

        this.connect();
    }

    public Connection connect() throws Exception {
        String url = "jdbc:sqlite:" + file;
        this.connection = null;

        try {
            this.connection = DriverManager.getConnection(url);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return this.connection;
    }

    public ResultSet query(String sql){
        return this.query(sql, new ArrayList<DatabaseParameter>());
    }

    public ResultSet query(String sql, ArrayList<DatabaseParameter> parameters){
        ResultSet result = null;

        if(this.connection != null && !sql.isEmpty()){
            try{
                PreparedStatement statement = this.connection.prepareStatement(sql);

                int index = 0;
                for(DatabaseParameter parameter: parameters){
                    index++;
                    switch (parameter.getType()){
                        case "int":
                            statement.setInt(index, parameter.getIntValue());
                            break;
                        case "double":
                            statement.setDouble(index, parameter.getDoubleValue());
                             break;
                        case "string":
                            statement.setString(index, parameter.getStringValue());
                            break;
                    }
                }

                result = statement.executeQuery();

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        return result;
    }

    public boolean execute(String sql){
        boolean success = true;

        try{
            Statement statement = this.connection.createStatement();
            statement.execute(sql);
        }catch (SQLException e){
            success = false;
            System.out.println(e.getMessage());
        }

        return success;
    }
}
