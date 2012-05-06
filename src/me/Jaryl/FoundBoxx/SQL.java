package me.Jaryl.FoundBoxx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQL {
	private FoundBoxx plugin; // NOTE TO SELF: CHANGE THIS ON NEW PROJECTS
	public SQL(FoundBoxx pl) // NOTE TO SELF: CHANGE THIS ON NEW PROJECTS
	{
		plugin = pl;
	}
	
	private Connection conn;
	public List<String> dataQueries = new ArrayList<String>();
	
	private boolean isQueuing = false;
	
	public boolean Connected()
	{
		if (conn != null)
		{
			try {
				return !conn.isClosed();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	// DATAQUERIES
	private void executeQueue()
	{
		if (Connected())
		{
			isQueuing = true;
			
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					if (isQueuing == false || !dataQueries.isEmpty())
					{
						for (int i = 0; i < (dataQueries.size() > plugin.sqlData ? plugin.sqlData - 1 : (dataQueries.size() - 1)); i++)
						{
							String query = dataQueries.get(i);
							if (!query.isEmpty())
							{
								try {
									dataQuery(query);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							dataQueries.remove(i);
						}
						
						executeQueue();
					}
					else
					{
						isQueuing = false;
					}
				}
			}, 40);
		}
	}
	public void queueData(String query) {
		if (Connected())
		{
			dataQueries.add(query);
			
			if (!isQueuing)
			{
				executeQueue();
			}
		}
	}
	private void dataQuery(String query) throws SQLException {
		if (Connected())
		{
			PreparedStatement QueryStatement = conn.prepareStatement(query);
			QueryStatement.executeUpdate();
		}
	}
	
	
	// QUERIES
	public ResultSet Query(String query) throws SQLException {
		if (Connected())
		{
			PreparedStatement QueryStatement = conn.prepareStatement(query);
			ResultSet rs = QueryStatement.executeQuery();
			return rs;
		}
		
		return null;
	}
	
	
	// LOADING AND STOPPING
	public void Load(String url, int port, String database, String prefix, String user, String pass) throws SQLException
	{
		Stop();

		DriverManager.setLoginTimeout(0);
		
		if (plugin.SQL.equalsIgnoreCase("h2"))
		{
			if (!checkLibrary()) {
				System.out.println("[" +plugin.getDescription().getFullName() + "] Downloading H2 library file..");
				downloadLibrary();
				System.out.println("[" +plugin.getDescription().getFullName() + "] H2 download complete! Restart server to complete process!");
				return;
			}
			
			try {
				Class.forName("org.h2.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			conn = DriverManager.getConnection("jdbc:h2:" + plugin.getDataFolder() + File.separator + prefix + "-log;IGNORECASE=TRUE");
		}
		else
		{
			conn = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database, user, pass);
		}
		
		conn.setAutoCommit(true);
		if (conn != null)
		{
			this.dataQuery("CREATE TABLE IF NOT EXISTS `" + prefix + "-log` (`date` datetime NOT NULL, `player` longtext NOT NULL, `block_id` smallint NOT NULL, `x` int NOT NULL, `y` int NOT NULL, `z` int NOT NULL)");
		}
	}
	
	private boolean checkLibrary() {
		File file = new File("lib/h2.jar");
		return file.exists() && !file.isDirectory();
	}
	private void downloadLibrary() {
		File dir = new File("lib");
		if (!dir.exists())
			dir.mkdir();

		File file = new File(dir, "h2.jar");
		BufferedInputStream input = null;
		FileOutputStream output = null;

		try {
			URL url = new URL("http://www.h2database.com/automated/h2-latest.jar");
			input = new BufferedInputStream(url.openStream());
			output = new FileOutputStream(file);

			byte data[] = new byte[1024];
			int count;

			while ((count = input.read(data)) != -1)
				output.write(data, 0, count);
		} catch (IOException e) {
			System.out.println("[" + plugin.getDescription().getFullName() + "] Could not downloaded H2 library file!");
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {}

			try {
				if (output != null)
					output.close();
			} catch (IOException e) {}
		}
	}
	
	public void Stop() throws SQLException
	{
		if (Connected())
		{
			System.out.println("[" + plugin.getDescription().getFullName() + "] Attempting to unload " + (plugin.SQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + ".");
			
			isQueuing = false;
			if (!dataQueries.isEmpty())
			{
				System.out.println("[" + plugin.getDescription().getFullName() + "] There are still some queries in the queue. Attempting to finish.");
				for (int i = 0; i < dataQueries.size(); i++)
				{
					String query = dataQueries.get(i);
					if (!query.isEmpty())
					{
						try {
							dataQuery(query);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
					}
					dataQueries.remove(i);
				}
				
				executeQueue();
			}
			
			conn.close();
			conn = null;
			
			System.out.println("[" + plugin.getDescription().getFullName() + "] " + (plugin.SQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + " unloaded.");
		}
	}
}