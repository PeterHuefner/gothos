package gothos.DatabaseCore;

import gothos.Common;
import gothos.Start;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SqliteConnection {

	protected String file;
	protected Connection connection;
	//protected ArrayList<ResultSet> sets = new ArrayList<>();

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

		if(connection != null){
			execute("VACUUM;");
			connection.setAutoCommit(false);
			//execute("PRAGMA journal_mode = WAL;");
		}

		return this.connection;
	}

	public void close(){
		if(this.connection != null){
			try{
				this.connection.close();
			}catch (Exception e){
				Common.printError(e);
			}
			this.connection = null;
		}
	}

	public boolean reconnect(){
		close();
		try {
			connect();
			return true;
		}catch (Exception e){
			Common.printError(e);
		}
		return false;
	}

	protected PreparedStatement bindParamsToSql(String sql, ArrayList<DatabaseParameter> parameters){
		PreparedStatement statement = null;

		try{
		   statement = this.connection.prepareStatement(sql);

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

		}catch (SQLException e){
			Common.printError(e);
		}

		return statement;
	}

	/*public void clearAllSets(){

		for(ResultSet set: sets){
			try{
				if(set != null && !set.isClosed()){
					set.close();
				}
				sets.remove(set);
			}catch (SQLException e){
				Common.printError(e);
			}
		}

	}*/

	public ResultSet query(String sql){
		return this.query(sql, new ArrayList<DatabaseParameter>());
	}

	public ResultSet query(String sql, ArrayList<DatabaseParameter> parameters){
		ResultSet result = null;

		if(this.connection != null && !sql.isEmpty()){
			try{
				PreparedStatement statement = this.bindParamsToSql(sql, parameters);
				if(statement != null){
					result = statement.executeQuery();
					connection.commit();
				}

			}catch (SQLException e){
				Common.printError(e);
			}catch (Exception e){
				Common.printError(e);
			}
		}

		return result;
	}

	public String fetchFirstColumn(ResultSet result){
		String col = null;
		try {
			while (result.next()) {
				col = result.getString(1);
			}
			result.close();
		}catch (SQLException e){}
		return col;
	}

	public String fetchFirstColumn(String sql){
		ResultSet result = this.query(sql);
		return this.fetchFirstColumn(result);
	}

	public String fetchFirstColumn(String sql, ArrayList<DatabaseParameter> paramters){
		ResultSet result = this.query(sql, paramters);
		return this.fetchFirstColumn(result);
	}

	public boolean execute(String sql, ArrayList<DatabaseParameter> parameters){
		boolean success = true;

		try{
			PreparedStatement statement = this.bindParamsToSql(sql, parameters);
			if(statement != null){
				statement.execute();
				connection.commit();
			}
		}catch (SQLException e){
			success = false;
			System.out.println(e.getMessage());
		}

		return success;
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

	public long getLastInsertId(){
		long id = 0;

		try{
			Statement statement = this.connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT last_insert_rowid();");

			if(result.next()){
				id = result.getLong(1);
			}
			result.close();
		}catch (SQLException e){
			Common.printError(e);
		}

		return id;
	}

	protected long _insertData(String table, LinkedHashMap<String, DatabaseParameter> params, String sqlShit) {
		String sql = "INSERT " + sqlShit + " INTO `" + table + "`";
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		String comma = "";

		ArrayList<DatabaseParameter> databseParams = new ArrayList<>();

		for(Map.Entry<String, DatabaseParameter> param: params.entrySet()){
			String columnName = param.getKey();
			//DatabaseParameter val = param.getValue();

			columns.append(comma);
			columns.append("`");
			columns.append(columnName);
			columns.append("`");

			values.append(comma);
			values.append("?");

			databseParams.add(param.getValue());

			comma = ", ";
		}

		sql += " (" + columns.toString() + ") VALUES (" + values + ");";

		this.execute(sql, databseParams);

		return this.getLastInsertId();
	}

	public long insertOrIgnoreData(String table, LinkedHashMap<String, DatabaseParameter> params) {
		return _insertData(table, params, "OR IGNORE");
	}

	public long insertData(String table, LinkedHashMap<String, DatabaseParameter> params) {
		return _insertData(table, params, "");
	}

	public boolean updateData(String table, LinkedHashMap<String, DatabaseParameter> params, String primaryKey, String primaryColumn){
		String sql = "UPDATE `" + table + "` SET ";
		StringBuilder columns = new StringBuilder();
		String comma = "";

		ArrayList<DatabaseParameter> databseParams = new ArrayList<>();

		for(Map.Entry<String, DatabaseParameter> param: params.entrySet()){
			String columnName = param.getKey();
			//DatabaseParameter val = param.getValue();

			columns.append(comma);
			columns.append("`");
			columns.append(columnName);
			columns.append("` = ?");

			databseParams.add(param.getValue());

			comma = ", ";
		}

		databseParams.add(new DatabaseParameter(primaryKey));

		sql += columns.toString() + " WHERE `" + primaryColumn + "` = ?;";

		return this.execute(sql, databseParams);
	}
}
