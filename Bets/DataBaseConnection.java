import java.sql.*;

public class DataBaseConnection
{
	private String dbms = "mysql";
	private String serverName = "212.64.153.49";
	private int portNumber = 3306;
	private Connection conn;
	private String userName = "user";
	private String password = "password";
	
	public DataBaseConnection()
	{
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.conn = getConnection();
		}catch(Exception e)
		{
			System.out.println("Failed to load driver: " + e.getMessage());
		}
	}
	
	public void createOrderInputTable() throws SQLException
	{
		String createTable = "CREATE TABLE IF NOT EXISTS transactions" + 
					"( id int(11) NOT NULL AUTO_INCREMENT," +
					" userid varchar(32) NOT NULL," + 
					" date datetime NOT NULL," +
					" activity varchar(40) NOT NULL," +
					" amount int(11) NOT NULL," +
					" addsubtract tinyint(4) NOT NULL," +
					" source varchar(36) NOT NULL," +
					" account_number varchar(36) NOT NULL," +
					" transaction_details varchar(256) NOT NULL," + 
					" match varchar(36) NOT NULL DEFAULT 0," +
					" PRIMARY KEY (ID)" +
					" ) ENGINE=MyISAM DEFAULT CHARSET=latin1;";
		
		Statement stmt = null;
		try{
			stmt = this.conn.createStatement();
			stmt.executeUpdate(createTable);
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}finally
		{
			if(stmt != null)
			{
				stmt.close();
			}
		}
	}
	
	public void insertOrderInputRow()
	{
		
	}
	
	public void createOrderHandledTable(String activity) throws SQLException
	{
		String createTable = 
		"CREATE TABLE IF NOT EXISTS "+ activity +"_handled (" +
		"id int(11) NOT NULL DEFAULT 0," +
		"userid varchar(32) NOT NULL," +
		"party int(15) NOT NULL," +
		"bet tinyint(4) unsigned NOT NULL," +
		"datum datetime NOT NULL," +
		"datum_handled datetime NOT NULL DEFAULT 0000-00-00 00:00:00," +
		"betid int(11) NOT NULL DEFAULT 0," +
		"trade_user int(11) NOT NULL DEFAULT 0," +
		"tickets int(11) unsigned NOT NULL," + 
		"buysell varchar(10) NOT NULL," +
		"bet_category tinyint(4) NOT NULL DEFAULT 0" +
		") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		
		Statement stmt = null;
		try{
			stmt = this.conn.createStatement();
			stmt.executeUpdate(createTable);
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}finally
		{
			if(stmt != null)
			{
				stmt.close();
			}
		}
	}
	
	public void createKoersDB(String activity) throws SQLException
	{
		String createTable = 
			"CREATE TABLE IF NOT EXISTS " + activity + "_koers (" +
			"id int(11) NOT NULL AUTO_INCREMENT," +
			"datum datetime NOT NULL" +
			"stock_0 tinyint(4) NOT NULL DEFAULT 0, " +
			"stock_1 tinyint(4) NOT NULL DEFAULT 0, " +
			"stock_2 tinyint(4) NOT NULL DEFAULT 0, " +
			"PRIMARY KEY (id) " +
			") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		
		Statement stmt = null;
		try{
			stmt = this.conn.createStatement();
			stmt.executeUpdate(createTable);
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}finally
		{
			if(stmt != null)
			{
				stmt.close();
			}
		}
	}
	
	public void updateKoersDB()
	{
		
	}
	
	public Connection getConnection() throws SQLException 
	{
		Connection conn = null;
		//Properties connectionProps = new Properties();
		//connectionProps.put("user", this.userName);
		//connectionProps.put("password", this.password);
		
		if (this.dbms.equals("mysql")) 
		{
			conn = DriverManager.getConnection(
				"jdbc:" + this.dbms + "://" +
				this.serverName +
				":" + this.portNumber + "/");//,
				//connectionProps);
		}
		System.out.println("Connected to database");
		return conn;
	}
}