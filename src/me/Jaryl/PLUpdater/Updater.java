package me.Jaryl.PLUpdater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import me.Jaryl.PLUpdater.RSSReader.Feed;
import me.Jaryl.PLUpdater.RSSReader.FeedMessage;
import me.Jaryl.PLUpdater.RSSReader.RSSFeedParser;

public class Updater extends Thread {
	// START OF STRINGS TO MODIFY ON NEW PLUGINS \\
	private String bukdev = "foundboxx"; //"http://dev.bukkit.org/server-mods/(THIS TEXT HERE)/files.rss"
	// END OF STRINGS TO MODIFY ON NEW PLUGINS \\
	
	private Plugin plugin;
	private CommandSender sender;
	
	public Updater(Plugin plugin, CommandSender sender)
	{
		this.plugin = plugin;
		this.sender = sender;
	}
	
	public void run()
	{
		int curver = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
		int latestver = curver;
		String latestlink = "";
		RSSFeedParser parser = new RSSFeedParser(
				"http://dev.bukkit.org/server-mods/" + bukdev + "/files.rss");
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
				    	int inde = line.indexOf(".jar");
				    	if (inde != -1)
				    	{
				    		BufferedInputStream in = new BufferedInputStream(new URL(line.substring(inds, inde) + ".jar").openStream());
				    		
				    		String fileLoc = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
				    		String fileName = fileLoc.substring(fileLoc.indexOf("/plugins/") + 9);
				    		FileOutputStream fos = new FileOutputStream("./plugins/" + plugin.getServer().getUpdateFolder() + "/" + fileName);
				    	    
				    		byte data[] = new byte[1024];
				    		int i;
				    		while((i = in.read(data, 0, 1024)) != -1)
				    			fos.write(data, 0, i);
				    		
				            if (in != null)
			                    in.close();
				            if (fos != null)
			                    fos.close();
				            
				            String msg = ChatColor.GREEN + "[" + plugin.getDescription().getFullName() + "] Plugin updated. Reload to complete. If you think this is bugged, inform " + (plugin.getDescription().getAuthors().size() > 0 ? "[" + plugin.getDescription().getAuthors().toString() + "]" : "the author of the plugin") + ".";
				            if (sender != null)
				            	sender.sendMessage(msg);
				            else
				            	System.out.println(msg);
				            
				            break;
				    	}
				    }
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (sender != null)
			sender.sendMessage(ChatColor.YELLOW + "[" + plugin.getDescription().getFullName() + "] No updates available.");
	}
}
