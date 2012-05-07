package me.Jaryl.FoundBoxx;

import me.Jaryl.FoundBoxx.Threads.Notify;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class fBlockListener implements Listener {
	private FoundBoxx plugin;

	public static int total;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.isCancelled() && event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();
			Location loc = block.getLocation();
			
			/*if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null)
			{
				Plugin guard = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
				if (guard != null && !guard.canBuild(player, block))
					return;
			}*/
			
			Block rela = block.getRelative(event.getBlockFace());
			int light = (rela.isEmpty() ? Math.round(((rela.getLightLevel()) & 0xFF) * 100) / 15 : 15);
			if (plugin.Dark && light == 0 && loc.getY() < 60 && player.getGameMode() == GameMode.SURVIVAL && !plugin.PermHandler.hasPermission(player, "foundboxx.dark", false, false) && !block.getWorld().getEnvironment().equals(Environment.NETHER))
			{
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "[FB] " + plugin.DarkMsg);
			}
			
			if (plugin.Creative && player.getGameMode() != GameMode.CREATIVE)
			{
		    	Thread notify = new Notify(plugin, player, block, loc, light);
				notify.start();
			}
		}
	}
	
	public fBlockListener(FoundBoxx instance)
	{
		plugin = instance;
	}
}
