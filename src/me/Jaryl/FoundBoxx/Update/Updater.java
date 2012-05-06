package me.Jaryl.FoundBoxx.Update;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import me.Jaryl.FoundBoxx.FoundBoxx;
import me.Jaryl.FoundBoxx.Update.RSS.Feed;
import me.Jaryl.FoundBoxx.Update.RSS.FeedMessage;
import me.Jaryl.FoundBoxx.Update.RSS.RSSFeedParser;

public class Updater {
	
	public static boolean update(FoundBoxx plugin) throws IOException
	{
		int curver = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
		int latestver = curver;
		String latestlink = "";
		RSSFeedParser parser = new RSSFeedParser(
				"http://dev.bukkit.org/server-mods/foundboxx/files.rss");
		Feed feed = parser.readFeed();
		for (FeedMessage message : feed.getMessages()) {
			latestver = Integer.parseInt(message.getTitle().replace("v", "").replace(".", ""));
			latestlink = message.getLink();
			break;
		}

		BufferedReader res;
		URL u;
		try {
			u = new URL(latestlink);
			if (latestver > curver)
			{
				res = new BufferedReader(new InputStreamReader(u.openStream())); 
				String line = res.readLine();
				while (line != null) {
					line = res.readLine();
					
					int inds = line.indexOf("http://dev.bukkit.org/media/files/");
				    if (inds != -1);
				    {
				    	int inde = line.indexOf("\">FoundBoxx.jar");
				    	if (inde != -1)
				    	{
				    		BufferedInputStream in = new BufferedInputStream(new URL(line.substring(inds, inde)).openStream());
				    		
				    		FileOutputStream fos = new FileOutputStream("./plugins/update/FoundBoxx.jar");
				    	    
				    		byte data[] = new byte[1024];
				    		int i;
				    		while((i = in.read(data, 0, 1024)) != -1)
				    			fos.write(data, 0, i);
				    		
				            if (in != null)
			                    in.close();
				            if (fos != null)
			                    fos.close();
				            
				            return true;
				    	}
				    }
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Deprecated
		/* BufferedReader res;
		URL u;
		try {
			u = new URL("http://dev.bukkit.org/server-mods/foundboxx/files/" + (FoundBoxx.curver + 1));
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			if (!c.getResponseMessage().equalsIgnoreCase("Not Found"))
			{
				res = new BufferedReader(new InputStreamReader(u.openStream())); 
				String line = res.readLine();
				while (line != null) {
					line = res.readLine();
					
					int inds = line.indexOf("http://dev.bukkit.org/media/files/");
				    if (inds != -1);
				    {
				    	int inde = line.indexOf("\">FoundBoxx.jar");
				    	if (inde != -1)
				    	{
				    		BufferedInputStream in = new BufferedInputStream(new URL(line.substring(inds, inde)).openStream());
				    		
				    		FileOutputStream fos = new FileOutputStream("./plugins/update/FoundBoxx.jar");
				    	    
				    		byte data[] = new byte[1024];
				    		int i;
				    		while((i = in.read(data, 0, 1024)) != -1)
				    			fos.write(data, 0, i);
				    		
				            if (in != null)
			                    in.close();
				            if (fos != null)
			                    fos.close();
				            
				            return true;
				    	}
				    }
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	    
		return false;
	}
}
