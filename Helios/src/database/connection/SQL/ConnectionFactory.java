/**
 * 
 */
package database.connection.SQL;

import java.util.Arrays;

import database.connection.DatabaseConnection;
import database.connection.DatabaseConnectionFactory;
import exceptions.DatabaseConnectionCreationException;

/**
 * @author jdiamond
 *
 */
public class ConnectionFactory extends DatabaseConnectionFactory 
{
	private final String URL_PARAM = "url";
	private final String DRIVER_PARAM = "driver";
	private final String USER_PARAM = "user";
	private final String PASS_PARAM = "pass";

	/**
	 * 
	 */
	public ConnectionFactory() 
	{
		super();
		requiredParameters = new String[]{URL_PARAM, DRIVER_PARAM, USER_PARAM, PASS_PARAM};

	}

	/* (non-Javadoc)
	 * @see database.connection.DatabaseConnectionFactory#getConnection()
	 */
	@Override
	public RemoteConnection getConnection()  throws DatabaseConnectionCreationException
	{
		//build a remote connection and return it

		RemoteConnection con = null;

		if(hasValidParams())
		{
			con = new RemoteConnection
			(
				parameters.get(URL_PARAM),
				parameters.get(USER_PARAM),
				parameters.get(PASS_PARAM),
				parameters.get(DRIVER_PARAM)
			);
		}
		else
		{
			throw new DatabaseConnectionCreationException("Invalid Parameters for connection creation");
		}

		return con;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		ConnectionFactory fact = new ConnectionFactory();

		fact.load("/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdbs27.properties");

		DatabaseConnection con = null;

		try
		{
			con = fact.getConnection();

			for(String[] row : con.runQuery("SELECT  CRM_MST_USER.USER_USERID AS USER_USERID,CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE AS ORDER_DATE,  CRM_TRN_ORDERDETAILS.ORDDET_AMOUNT AS ORDER_AMOUNT  FROM CRM_MST_USER INNER JOIN CRM_TRN_ORDERDETAILS ON CRM_MST_USER.USER_USERID = CRM_TRN_ORDERDETAILS.ORDDET_CREATEDBY  WHERE CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE >= '2012-01-01 00:00:00' AND CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE <= '2012-01-31 23:59:59'"))
			{
				System.out.println(Arrays.asList(row).toString());
			}
		} 
		catch (DatabaseConnectionCreationException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			if(con != null)
			{
				con.close();
			}
		}

	}

}
