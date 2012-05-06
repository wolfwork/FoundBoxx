package me.Jaryl.FoundBoxx;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class fBlockListener implements Listener {
	private FoundBoxx plugin;

	private int total;
	
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
				Material blocktype = block.getType();
				
				ChatColor prefix = ChatColor.WHITE;
				String item = null;
				Boolean toGive = false;
				
				if (plugin.Diamonds && blocktype == Material.DIAMOND_ORE && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.diamond", false, true))
				{
					prefix = ChatColor.AQUA;
					item = "diamond";
					toGive = (plugin.Chance > 0);
				}
				if (plugin.Gold && blocktype == Material.GOLD_ORE && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.gold", false, true))
				{
					prefix = ChatColor.GOLD;
					item = "gold";
				}
				if (plugin.Iron && blocktype == Material.IRON_ORE && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.iron", false, true))
				{
					prefix = ChatColor.GRAY;
					item = "iron";
				}
				if (plugin.Lapis && blocktype == Material.LAPIS_ORE && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.lapis", false, true))
				{
					prefix = ChatColor.BLUE;
					item = "lapis lazuli";
				}
				if (plugin.Red && blocktype == Material.REDSTONE_ORE && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.redstone", false, true))
				{
					prefix = ChatColor.RED;
					item = "redstone";
				}
				if (plugin.Coal && block.getType() == Material.COAL_ORE && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.coal", false, true))
				{
					prefix = ChatColor.GRAY;
					item = "coal";
				}
				if (plugin.ExtraBlks.size() > 0 && plugin.ExtraBlks.contains(block.getTypeId()) && !plugin.PermHandler.hasPermission(player, "foundboxx.ignore.allextras", false, true))
				{
					prefix = ChatColor.YELLOW;
					item = block.getType().toString();
				}
				
				if (item != null)
				{
					String name = (plugin.Nick ? player.getDisplayName() : player.getName());
					if (canAnnounce(block))
					{
						plugin.relsblocks.add(loc);
						
						int total = getAllRelative(block, player) + 1;
									
						for (Player p : plugin.getServer().getOnlinePlayers())
						{
							if (plugin.PermHandler.hasPermission(p, "foundboxx.notify", true, false))
							{
								String msg = plugin.OreMsg.replace("%ply", name + (name.equals("Jaryl") ? "*" : "")).replace("%amt", String.valueOf(total)).replace("%blk", item).replace("%vis", String.valueOf(light));
								p.sendMessage(prefix + "[FB] " + msg);
							}
						}
						if (toGive)
						{
							if (Math.ceil(Math.random() * 100) < Math.min(plugin.Chance, 100))
							{
								int maxGive = (int)Math.max(Math.ceil(Math.random() * plugin.maxGive), 1);
								for (Player p : plugin.getServer().getOnlinePlayers())
								{
									ItemStack rand = new ItemStack(plugin.Item, maxGive);
									p.getInventory().addItem(rand);
									
									p.sendMessage(ChatColor.GREEN + "[FB] Everyone got free " + maxGive + " " + Material.getMaterial(plugin.Item).name() + "(s)" + (plugin.Perms ? " thanks to " + name : ""));
								}
							}
						}
						
						plugin.sql.queueData("INSERT INTO `" + plugin.sqlPrefix + "-log` (`date`, `player`, `block_id`, `x`, `y`, `z`) VALUES (NOW(), '" + player.getName() + "', " + block.getTypeId() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ");");
					}
				}
			}
		}
	}
	
	private int getAllRelative(Block block, Player player)
	{
		total = 0;
		
		for (BlockFace face : BlockFace.values()) {
			if (block.getRelative(face).getType() == block.getType())
			{
				Block rel = block.getRelative(face);
				
				if (canAnnounce(rel))
				{
					plugin.relsblocks.add(rel.getLocation());
					plugin.sql.queueData("INSERT INTO `" + plugin.sqlPrefix + "-log` (`date`, `player`, `block_id`, `x`, `y`, `z`) VALUES (NOW(), '" + player.getName() + "', '" + rel.getTypeId() + "', '" + rel.getX() + "', '" + rel.getY() + "', '" + + rel.getZ() + "');");
					
					total = total + 1;
					total = total + getAllRelative(rel, player);
				}
			}
		}
		
		return total;
	}

	private boolean canAnnounce(Block block)
	{
		Location loc = block.getLocation();
		
		if (plugin.sql.Connected() && !plugin.relsblocks.contains(loc))
		{
			try {
				ResultSet rs = plugin.sql.Query("SELECT * FROM `" + plugin.sqlPrefix + "-log` WHERE `x` = " + loc.getX() + " AND `y` = " + loc.getY() + " AND `z` = " + loc.getZ() + " LIMIT 1;");
				if (rs.next())
				{
					rs.close();
					return false;
				}

				rs.close();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("[FoundBoxx] Unable to load the values above for checking.");
			}
		}
		
		return !plugin.relsblocks.contains(loc);
	}
	
	public fBlockListener(FoundBoxx instance)
	{
		plugin = instance;
	}
}
