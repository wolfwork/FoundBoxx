package me.Jaryl.FoundBoxx.Listeners;

import java.io.File;

import me.Jaryl.FoundBoxx.FoundBoxx;
import net.gravitydevelopment.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class fPlayerListener implements Listener{
	private FoundBoxx plugin;
	private File file;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.Autoupdt && plugin.PermHandler.hasPermission(event.getPlayer(), "foundboxx.cmd.update", false, false))
		{
    		Updater update = new Updater(plugin, 33366, file, Updater.UpdateType.NO_DOWNLOAD, true);
    		if (update.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
    			event.getPlayer().sendMessage(ChatColor.AQUA + "[FoundBoxx] An update is available! Run \"/foundboxx update\" and reload to update!");
    		}
		}
	}
	
	public fPlayerListener(FoundBoxx instance, File file)
	{
		plugin = instance;
		this.file = file;
	}
}
