package me.Jaryl.FoundBoxx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.Jaryl.FoundBoxx.Listeners.fBlockListener;
import me.Jaryl.FoundBoxx.Listeners.fBreakListener;
import me.Jaryl.FoundBoxx.Listeners.fPlayerListener;
import me.Jaryl.FoundBoxx.SQLwrapper.SQLwrapper;
import me.Jaryl.FoundBoxx.SQLwrapper.Threads.SQLLoad;
import me.Jaryl.FoundBoxx.Threads.Farmrate;
import net.gravitydevelopment.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;


public class FoundBoxx extends JavaPlugin {
	public PermissionsHandler PermHandler = new PermissionsHandler(this);
	private fBlockListener blockListener = new fBlockListener(this);
	private fBreakListener breakListener = new fBreakListener(this);
	private fPlayerListener playerListener = new fPlayerListener(this, this.getFile());
	public SQLwrapper sql = new SQLwrapper(this);

	public List<Location> foundblocks = new ArrayList<Location>();
	
	public boolean needRestart;
	
	public boolean Autoupdt;
	public boolean UpdtNotify;
	public boolean Stats;
	public boolean Creative;
	public boolean Nick;
	public boolean Perms;
	
	public String DarkMsg;
	public String OreMsg;
	//public int Delay;
	
	public boolean diagonal;
	
	public boolean Emeralds;
	public boolean Diamonds;
	public boolean Gold;
	public boolean Iron;
	public boolean Coal;
	public boolean Lapis;
	public boolean Red;
	public List<Integer> ExtraBlks;
	
	public int Chance;
	public int maxGive;
	public int Item;
	
	/*public boolean SpecReward;
	public int SpecOre;
	public int SpecVein;
	public int SpecItem;
	public boolean SpecFinder;*/
	
	public boolean Dark = false;
	
	public String useSQL;
	public String sqlURL;
	public int sqlPort;
	public String sqlDatabase;
	public int sqlDays;
	public int sqlLimit;
	public int sqlData;
	public String sqlPrefix;
	public String sqlUser;
	public String sqlPass;
	
	public boolean canAnnounce(Block block)
	{
		Location loc = block.getLocation();
		
		if (sql.Connected() && !foundblocks.contains(loc))
		{
			try {
				// Check if the block is placed by a player before
				ResultSet rs = sql.Query("SELECT * FROM `" + sqlPrefix + "-placed` WHERE `x` = " + loc.getX() + " AND `y` = " + loc.getY() + " AND `z` = " + loc.getZ() + " LIMIT 1;");
				if (rs.next())
				{
					rs.close();
					return false;
				}
				
				// Check if the block is found before
				ResultSet rs2 = sql.Query("SELECT * FROM `" + sqlPrefix + "-log` WHERE `x` = " + loc.getX() + " AND `y` = " + loc.getY() + " AND `z` = " + loc.getZ() + " LIMIT 1;");
				if (rs2.next())
				{
					rs2.close();
					return false;
				}

				rs.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[FoundBoxx] Unable to load the values above for checking.");
			}
		}
		return !foundblocks.contains(loc);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T parseConfig(String path, T def) {
		FileConfiguration config = getConfig();
		T rval = (T) config.get(path, def);
		config.set(path, rval);
		return rval;
	}
	private void loadConfigurations(CommandSender p)
	{
		reloadConfig();
    		Autoupdt = parseConfig("Auto_Update_On_Plugin_Enable", true);
    		UpdtNotify = parseConfig("Notify_On_Updates", true);
    		Stats = parseConfig("Allow_Usage_Stats_Collection", true);
	    	Creative = parseConfig("Survival_Only", true);
	    	Nick = parseConfig("Use_Nickname", false);
	    	//Delay = parseConfig("Delay_In_Seconds", 10);
	    	Perms = parseConfig("Use_Permissions", false);
	    	
	    	OreMsg = parseConfig("Messages.Found_Notification", "%ply found %amt %blk(s) (Visibility: %vis%)");
	    	DarkMsg = parseConfig("Messages.Must_Have_Light_To_Mine", "Interacting in the dark is dangerous! Put some torches!");
	    	
	    	diagonal = parseConfig("Count_Diagonal_Ores", false);
	    	
	    	Emeralds = parseConfig("EMERALDS", true);
	    	Diamonds = parseConfig("DIAMONDS", true);
	    	Gold = parseConfig("GOLD", true);
	    	Iron = parseConfig("IRON", true);
	    	Coal = parseConfig("COAL", false);
	    	Lapis = parseConfig("LAPIS", true);
	    	Red = parseConfig("REDSTONE", false);
	    	ExtraBlks = parseConfig("Extra_Blocks_IDs", new ArrayList<Integer>());
	    	
	    	Chance = parseConfig("Percentage_Chance_To_Give_Randoms_Item", 0);
	    	maxGive = parseConfig("Max_Random_Items_To_Give", 3);
	    	Item = parseConfig("Random_Item_To_Give", 365);
	    	
	    	/*SpecReward = parseConfig("Use_Special_Reward", false);
	    	SpecOre = parseConfig("Special_Reward.Required_Block_Mined", 56);
	    	SpecVein = parseConfig("Special_Reward.Required_Vein_Size", 3);
	    	SpecItem = parseConfig("Special_Reward.Random_Item_To_Give", 92);
	    	SpecFinder = parseConfig("Special_Reward.Only_Finder_Gets_Item", false);*/
	    	
	    	Dark = parseConfig("Must_Have_Light_To_Mine", false);
	    	
	    	useSQL = String.valueOf(parseConfig("SQL_Enabled", false));
	    	sqlURL = parseConfig("SQL.URL", "localhost");
	    	sqlPort = parseConfig("SQL.Port", 3306);
	    	sqlDatabase = parseConfig("SQL.Database", "minecraft");
	    	sqlDays = parseConfig("SQL.Days_To_Remove", 1);
	    	sqlLimit = parseConfig("SQL.Maximum_Queries", 5000);
	    	sqlData = parseConfig("SQL.Maximum_Data_Queries_Per_Second", 10);
	    	sqlPrefix = parseConfig("SQL.Prefix", "fb");
	    	sqlUser = parseConfig("SQL.User", "root@localhost");
	    	sqlPass = parseConfig("SQL.Pass", "");
	    	
	    	saveConfig();
    	if (p != null)
    	{
    		p.sendMessage(ChatColor.AQUA + "[FoundBoxx] New configurations:");
    		printConfig(p);
    	}
    	
    	if (!useSQL.equalsIgnoreCase("false"))
    	{
    		System.out.println("[FoundBoxx] Attempting to load " + (useSQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + ".");
    		Thread sqlload = new SQLLoad(this, sqlURL, sqlPort, sqlDatabase, sqlPrefix, sqlUser, sqlPass);
			sqlload.start();
			System.out.println("[FoundBoxx] " + (useSQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + " loaded.");
			
			sql.queueData("DELETE FROM `" + sqlPrefix + "-log` WHERE `date` <= CURDATE() -" + sqlDays + " LIMIT " + sqlLimit + ";");
    	}
    	else
    	{
    		try {
				sql.Stop();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
	}
	private void printConfig(CommandSender p)
	{
		p.sendMessage("    Auto-update on start: " + Autoupdt);
		p.sendMessage("    Updates available notification: " + UpdtNotify);
		p.sendMessage("    Allow usage stats collection: " + Stats);
		p.sendMessage("    Survival only: " + Creative);
		p.sendMessage("    Permissions: " + Perms);
		p.sendMessage("    Count diagonal ores: " + diagonal);
		p.sendMessage("    Blocks:");
		p.sendMessage("        EMERALDS: " + Emeralds);
		p.sendMessage("        DIAMONDS: " + Diamonds);
		p.sendMessage("        GOLD: " + Gold);
		p.sendMessage("        IRON: " + Iron);
		p.sendMessage("        LAPIS: " + Lapis);
		p.sendMessage("        REDSTONE: " + Red);
		p.sendMessage("        COAL: " + Coal);
		p.sendMessage("        EXTRA BLOCKS: " + ExtraBlks.toString());
		p.sendMessage("    Random Item:");
		p.sendMessage("        Chance random item: " + Chance + "%");
		p.sendMessage("        Max random items: " + maxGive);
		p.sendMessage("        Random item: " + Item);
		p.sendMessage("    Disallow mining in dark: " + Dark);
		p.sendMessage("    SQL: " + ((!useSQL.equalsIgnoreCase("false")) ? ((useSQL.equalsIgnoreCase("true") || useSQL.equalsIgnoreCase("SQL")) ? ("mysql://" + sqlUser + ":" + sqlPass + "@" + sqlURL + ":" + sqlPort + "/" + sqlDatabase + "/" + sqlPrefix + "-log" + " (" + sqlDays + ", " + sqlLimit + ")") : "H2 Database") : false));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(needRestart)
		{
			sender.sendMessage(ChatColor.RED + "[FB] A restart/reload is required as there are major changes made. (H2 downloaded, etc)");
			return true;
		}
		
		if(commandLabel.equalsIgnoreCase("foundboxx") || commandLabel.equalsIgnoreCase("fb")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.AQUA + "[FoundBoxx v" + getDescription().getVersion() + "] Commands:");
				sender.sendMessage("    /" + commandLabel);
				sender.sendMessage("        reload - Reload configurations");
				sender.sendMessage("        config - Print configurations");
				sender.sendMessage("        queue - Print queue size");
				sender.sendMessage("        farmrate (name) (days) - Print farmrates");
				sender.sendMessage("        update - Auto update plugin");
			}
			else
			{
				if (args[0].equalsIgnoreCase("reload") && PermHandler.hasPermission(sender, "foundboxx.cmd.reload", false, false))
				{
					loadConfigurations(sender);
					
					return true;
				}
				if (args[0].equalsIgnoreCase("config") && PermHandler.hasPermission(sender, "foundboxx.cmd.reload", false, false))
				{
					sender.sendMessage(ChatColor.AQUA + "[FoundBoxx] Configurations:");
					printConfig(sender);
					
					return true;
				}
				if (args[0].equalsIgnoreCase("queue"))
				{
					if (sql.Connected())
					{
						if (PermHandler.hasPermission(sender, "foundboxx.cmd.queue", false, false))
							sender.sendMessage("[FoundBoxx] Current queue size: " + sql.dataQueries.size());
					}
					else
						sender.sendMessage("[FoundBoxx] This server is not using farmrates!");
				
					return true;
				}
				if (args[0].equalsIgnoreCase("farmrate") && PermHandler.hasPermission(sender, "foundboxx.cmd.farmrate", false, false))
				{
					if (args.length == 3 && Integer.parseInt(args[2]) > 0)
					{
						findFarmRate(sender, args[1], args[2]);
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "[FoundBoxx] Proper input: /fb farmrate (name) (days [>0])");
					}
					
					return true;
				}
				if (args[0].equalsIgnoreCase("update") && PermHandler.hasPermission(sender, "foundboxx.cmd.update", false, false))
				{
					if (Autoupdt)
			    	{
			    		new Updater(this, 33366, this.getFile(), Updater.UpdateType.DEFAULT, true);
			    	}
					
					return true;
				}
					
				sender.sendMessage(ChatColor.RED + "[FoundBoxx] Unknown command or no permission");
			}
			return true;
		}
		return false;
	}
	
	private void findFarmRate(CommandSender asker, String name, String days)
	{
		if (sql.Connected())
		{	
			try {
				ResultSet rs = sql.Query("SELECT * FROM `" + sqlPrefix + "-log` WHERE `player` LIKE '" + name + "' AND `date` >= CURDATE() -" + days + " LIMIT " + sqlLimit + ";");
				Thread farmrate = new Farmrate(this, rs, name, days, asker);
				farmrate.start();
			} catch (SQLException e) {
				e.printStackTrace();
				asker.sendMessage("[FoundBoxx] Unable to load values above for checking.");
			}

			asker.sendMessage(ChatColor.YELLOW + "Yellow" + ChatColor.WHITE + " - Possible xRay, " + ChatColor.RED + "Red" + ChatColor.WHITE + " - Probably xRay");
			return;
		}
		
		asker.sendMessage(ChatColor.RED + "[FoundBoxx] SQL is not loaded, ignoring command input.");
	}
    
	@Override
	public void onDisable() {
		try {
			sql.Stop();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("[" + this.getDescription().getName() + " v" + this.getDescription().getVersion() + "] Disabled.");
	}

	@Override
	public void onEnable() {
		loadConfigurations(null);
		PermHandler.setupPermissions();

    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(blockListener, this);
    	pm.registerEvents(breakListener, this);
    	pm.registerEvents(playerListener, this);
    	
    	System.out.println("[" + this.getDescription().getName() + " v" + this.getDescription().getVersion() + "] Enabled" + (needRestart ? " but will need a restart soon." : "."));
	
    	if (Autoupdt)
    	{
    		new Updater(this, 33366, this.getFile(), Updater.UpdateType.DEFAULT, true);
    	}
    	
    	try {
	    	MetricsLite metrics = new MetricsLite(this);
	    	if (Stats) {
		    	metrics.enable();
	    	} else {
		    	metrics.disable();
	    	}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	System.out.println("[" + this.getDescription().getName() + " v" + this.getDescription().getVersion() + "] Stats collection is " + (Stats ? "is enabled." : "is disabled."));
	}
}
