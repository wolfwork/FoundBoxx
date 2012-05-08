package me.Jaryl.FoundBoxx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.Jaryl.FoundBoxx.Listeners.fBlockListener;
import me.Jaryl.FoundBoxx.Listeners.fBreakListener;
import me.Jaryl.FoundBoxx.Threads.Farmrate;
import me.Jaryl.FoundBoxx.Update.Updater;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import SQL.SQL;
import SQL.Threads.SQLLoad;

public class FoundBoxx extends JavaPlugin {
	public PermissionsHandler PermHandler = new PermissionsHandler(this);
	private fBlockListener blockListener = new fBlockListener(this);
	private fBreakListener breakListener = new fBreakListener(this);
	public SQL sql = new SQL(this);

	public List<Location> relsblocks = new ArrayList<Location>();
	public List<Location> brokenblocks = new ArrayList<Location>();
	
	public boolean needRestart;
	
	public boolean Creative;
	public boolean Nick;
	public boolean Perms;
	
	public String DarkMsg;
	public String OreMsg;
	//public int Delay;
	
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
	
	public boolean SpecReward;
	public int SpecOre;
	public int SpecVein;
	public int SpecItem;
	public boolean SpecFinder;
	
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
	
	public boolean Dark = false;
	
	private void loadConfigurations(CommandSender p)
	{
    	Configuration config = new Configuration(this);
    	
    	if (config.exists())
    	{
    		config.load();
    	}
    	
	    	Creative = config.parse("Survival_Only", true);
	    	Nick = config.parse("Use_Nickname", false);
	    	//Delay = config.parse("Delay_In_Seconds", 10);
	    	Perms = config.parse("Use_Permissions", false);
	    	
	    	OreMsg = config.parse("Messages.Found_Notification", "%ply found %amt %blk(s) (Visibility: %vis%)");
	    	DarkMsg = config.parse("Messages.Must_Have_Light_To_Mine", "Interacting in the dark is dangerous! Put some torches!");
	    	
	    	Diamonds = config.parse("DIAMONDS", true);
	    	Gold = config.parse("GOLD", true);
	    	Iron = config.parse("IRON", true);
	    	Coal = config.parse("COAL", false);
	    	Lapis = config.parse("LAPIS", true);
	    	Red = config.parse("REDSTONE", false);
	    	ExtraBlks = config.parse("Extra_Blocks_IDs", new ArrayList<Integer>());
	    	
	    	Chance = config.parse("Percentage_Chance_To_Give_Randoms_Item", 0);
	    	maxGive = config.parse("Max_Random_Items_To_Give", 3);
	    	Item = config.parse("Random_Item_To_Give", 365);
	    	
	    	/*SpecReward = config.parse("Use_Special_Reward", false);
	    	SpecOre = config.parse("Special_Reward.Required_Block_Mined", 56);
	    	SpecVein = config.parse("Special_Reward.Required_Vein_Size", 3);
	    	SpecItem = config.parse("Special_Reward.Random_Item_To_Give", 92);
	    	SpecFinder = config.parse("Special_Reward.Only_Finder_Gets_Item", false);*/
	    	
	    	Dark = config.parse("Must_Have_Light_To_Mine", false);
	    	
	    	useSQL = String.valueOf(config.parse("SQL_Enabled", false));
	    	sqlURL = config.parse("SQL.URL", "localhost");
	    	sqlPort = config.parse("SQL.Port", 3306);
	    	sqlDatabase = config.parse("SQL.Database", "minecraft");
	    	sqlDays = config.parse("SQL.Days_To_Remove", 1);
	    	sqlLimit = config.parse("SQL.Maximum_Queries", 5000);
	    	sqlData = config.parse("SQL.Maximum_Data_Queries_Per_Second", 10);
	    	sqlPrefix = config.parse("SQL.Prefix", "fb");
	    	sqlUser = config.parse("SQL.User", "root@localhost");
	    	sqlPass = config.parse("SQL.Pass", "");
	    	
    	config.save();
    	
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	private void printConfig(CommandSender p)
	{
		p.sendMessage("    Survival only: " + Creative);
		p.sendMessage("    Permissions: " + Perms);
		p.sendMessage("    Blocks:");
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
				sender.sendMessage(ChatColor.AQUA + "[FoundBoxx v" + getDescription().getVersion() + " ] Commands:");
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
					Thread updater = new Updater(this, sender);
					updater.start();
					
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
				// TODO Auto-generated catch block
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
    	
    	System.out.println("[" + this.getDescription().getName() + " v" + this.getDescription().getVersion() + "] Enabled" + (needRestart ? " but will need a restart soon." : "."));
	
    	Thread updater = new Updater(this, null);
		updater.start();
	}
}
