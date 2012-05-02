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
	
	public static void createOrderInputTable()
	{
		/*
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try
		{
			con = DriverManager.getConnection("jbdc:default:connection");
			
			pstmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS \'transactions\' (
							\'id' int(11) NOT NULL AUTO_INCREMENT,
							'userid' varchar(32) NOT NULL,
							'date' datetime NOT NULL,
							'amount' int(11) NOT NULL,
							'addsubtract' tinyint(4) NOT NULL,
							'source' varchar(36) NOT NULL,
							'account_number' varchar(36) NOT NULL,
							'transaction_details' varchar(256) NOT NULL,
							'match' varchar(36) NOT NULL DEFAULT '0',
							PRIMARY KEY ('id')
							) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
			
			pstmt.executeStatement();
			
		}finally
		{
			if(pstmt != null)
			{
				pstmt.close();
			}
		}
		*/
	}
	
	public static void insertOrderInputRow()
	{
		
	}
	
	public static void updateKoersDB()
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