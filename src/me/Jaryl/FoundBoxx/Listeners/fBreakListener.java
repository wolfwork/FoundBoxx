package me.Jaryl.FoundBoxx.Listeners;

import me.Jaryl.FoundBoxx.FoundBoxx;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
			if (plugin.foundblocks.contains(loc))
				plugin.foundblocks.remove(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
		{
			Block blk = event.getBlock();
			if (plugin.canAnnounce(blk))
			{
				plugin.foundblocks.add(blk.getLocation());
				plugin.sql.queueData("INSERT INTO `" + plugin.sqlPrefix + "-placed` (`x`, `y`, `z`) VALUES ('" + blk.getX() + "', '" + blk.getY() + "', '" + + blk.getZ() + "');");
				
			}
		}
	}
	
	public fBreakListener(FoundBoxx instance)
	{
		plugin = instance;
	}
}