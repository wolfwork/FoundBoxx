package me.Jaryl.FoundBoxx;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class fBreakListener implements Listener{
	private FoundBoxx plugin;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled())
		{
			Location loc = event.getBlock().getLocation();
			if (plugin.relsblocks.contains(loc))
				plugin.relsblocks.remove(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
		{
			Location loc = event.getBlock().getLocation();
			if (!plugin.relsblocks.contains(loc))
				plugin.relsblocks.add(loc);
		}
	}
	
	public fBreakListener(FoundBoxx instance)
	{
		plugin = instance;
	}
}