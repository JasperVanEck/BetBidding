import java.sql.*;

public class DataBaseConnection
{
	private String dbms = "mysql";
	private String serverName = "localhost"; //"database.betbidding.com";
	private int portNumber = 3306;
	private Connection conn;
	
	/**
	 * DataBaseConnection constructor, takes no argument.
	 */
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
	
	public void createActivitiesSearchTable() throws SQLException
	{
		
		
	}
	
	public void updateActivitiesSearchTable() throws SQLException
	{
		
	}
	
	/**
	 * Creates the bidhighasklow table.
	 * Also enters the first entry, so the id can be returned and used in future, updates.
	 */
	public int createBidHighAskLowTable(int[] askLow, int[] bidHigh, String activity) throws SQLException
	{
		int id = 0;
		String createTable = "CREATE TABLE IF NOT EXISTS bidhighasklow " +
					"( id int(11) NOT NULL AUTO_INCREMENT, " +
					" activity varchar(40) NOT NULL, " + 
					" outcome_bid_0 tinyint(4) NOT NULL, " + 
					" outcome_bid_1 tinyint(4) NOT NULL, " +
					" outcome_bid_2 tinyint(4) NOT NULL, " +
					" outcome_ask_0 tinyint(4) NOT NULL, " +
					" outcome_ask_1 tinyint(4) NOT NULL, " +
					" outcome_ask_2 tinyint(4) NOT NULL, " +
					" PRIMARY KEY (id) " +
					" )";
		
		String firstEntry = "insert into bidhighasklow " +
					"values( '" + activity + "', " + 
					bidHigh[0] + ", " + 
					bidHigh[1] + ", " + 
					bidHigh[2] + ", " + 
					askLow[0] + ", " + 
					askLow[1] + ", " + 
					askLow[2] +  
					" )";
		
		String idQuery = "select id " + 
					"from bidhighasklow " +
					"where outcome_bid_0 = " + bidHigh[0] +
					"AND outcome_bid_1 = " + bidHigh[1] + 
					"AND outcome_bid_2 = " + bidHigh[2] + 
					"AND outcome_ask_0 = " + askLow[0] + 
					"AND outcome_ask_1 = " + askLow[1] + 
					"AND outcome_ask_2 = " + askLow[2] + 
					"AND activity = '" +  activity + "' ";
		
		Statement stmt = null;
		Statement pstmt = null;
		Statement stmt2 = null;
		try{
			this.conn.setAutoCommit(false);
			stmt = this.conn.createStatement();
			pstmt = this.conn.createStatement();
			stmt2 = this.conn.createStatement();
			stmt.executeUpdate(createTable);
			pstmt.executeUpdate(firstEntry);
			ResultSet rs = stmt2.executeQuery(idQuery);
			conn.commit();
			while(rs.next())
			{
				id = rs.getInt("id");
			}
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}finally
		{
			if(stmt != null || pstmt != null || stmt2 != null)
			{
				stmt.close();
				pstmt.close();
				stmt2.close();
			}
			this.conn.setAutoCommit(true);
			return id;
		}
	}
	
	public void updateBidHighAskLow(int[] askLow, int[] bidHigh, String activity, int id) throws SQLException
	{
		String updateTable = "update bidhighasklow " +
					"set outcome_bid_0 = " + bidHigh[0] +
					"outcome_bid_1 = " + bidHigh[1] + 
					"outcome_bid_2 = " + bidHigh[2] + 
					"outcome_ask_0 = " + askLow[0] + 
					"outcome_ask_1 = " + askLow[1] + 
					"outcome_ask_2 = " + askLow[2] + 
					"where activity = '" + activity + "' " + 
					"AND id = " + id;
		Statement stmt = null;
		try{
			stmt = this.conn.createStatement();
			stmt.executeUpdate(updateTable);
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
	
	public void createOrderInputTable() throws SQLException
	{
		String createTable = "CREATE TABLE IF NOT EXISTS transactions " + 
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
					" PRIMARY KEY (id)" +
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
	
	public void insertNewOrder(Ticket ticket) throws SQLException
	{
		String insertRow = "insert into transactions " + 
					"values ( " + ticket.getUserID() + ", '" + 
					ticket.dateToString() + "', '" +
					ticket.getActivity() + "', " +
					ticket.getAmount() + ", " + 
					"0" + ", '" + //addsubtract
					"source" + "', '" + 
					"account_number" + "', '" + 
					"transaction_details" + "', '" + 
					ticket.getType() + 
					"') ";
		
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(insertRow);
			
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
	
	public void createOrderHandledTable(String activity) throws SQLException
	{
		String createTable = 
		"CREATE TABLE IF NOT EXISTS "+ activity +"_handled (" +
		"id int(11) NOT NULL AUTO_INCREMENT, " +
		"userid varchar(32) NOT NULL, " +
		"party int(15) NOT NULL, " +
		"bet tinyint(4) unsigned NOT NULL, " +
		"datum datetime NOT NULL, " +
		"datum_handled datetime NOT NULL DEFAULT 0000-00-00 00:00:00, " +
		"betid int(11) NOT NULL DEFAULT 0, " +
		"trade_user int(11) NOT NULL DEFAULT 0, " +
		"tickets int(11) unsigned NOT NULL, " + 
		"buysell varchar(10) NOT NULL, " +
		"bet_category tinyint(4) NOT NULL DEFAULT 0 " +
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
	
	public void insertOrderHandled(Ticket ticket, int activityID) throws SQLException
	{
		int tickets = (ticket.getAmount() - ticket.getAmountLeft());
		String insertRow = "insert into " + ticket.getActivity() + "_handled " +
					" values( '" + ticket.getUserID() + "', " +
					"0" + ", " + //party
					ticket.getPrice() + ", " +
					ticket.dateToString() + ", " +  
					" 0000-00-00 00:00:00" + ", " + //date handled
					activityID + ", " + //betid
					tickets + ", '" +
					ticket.getBidOrAsk() + "', " +
					ticket.getOutcome() +
					" )";
		
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(insertRow);
			
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
	
	public void createKoersTable(String activity) throws SQLException
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
	
	public void insertKoersTable(String activity, int[] koers, String date) throws SQLException
	{
		String insertRow = 
			"insert into " + activity + "_koers" +
			"values( '" + date + "'," + 
			koers[0] + ", " +
			koers[1] + ", " +
			koers[2] + ")";
		
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(insertRow);
			
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