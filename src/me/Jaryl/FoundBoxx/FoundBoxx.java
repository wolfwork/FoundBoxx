package me.Jaryl.FoundBoxx;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.Jaryl.FoundBoxx.Update.Updater;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FoundBoxx extends JavaPlugin {
	public PermissionsHandler PermHandler = new PermissionsHandler(this);
	private fBlockListener blockListener = new fBlockListener(this);
	private fBreakListener breakListener = new fBreakListener(this);
	public SQL sql = new SQL(this);

	public List<Location> relsblocks = new ArrayList<Location>();
	public List<Location> brokenblocks = new ArrayList<Location>();
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
	
	public String SQL;
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
	    	
	    	SQL = String.valueOf(config.parse("SQL_Enabled", false));
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
    	
    	if (!SQL.equalsIgnoreCase("false"))
    	{
    		System.out.println("[FoundBoxx] Attempting to load " + (SQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + ".");
    		try {
				sql.Load(sqlURL, sqlPort, sqlDatabase, sqlPrefix, sqlUser, sqlPass);
				System.out.println("[FoundBoxx] " + (SQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + " loaded.");
				sql.queueData("DELETE FROM `" + sqlPrefix + "-log` WHERE `date` <= CURDATE() -" + sqlDays + " LIMIT " + sqlLimit + ";");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("[FoundBoxx] Unable to load " + (SQL.equalsIgnoreCase("h2") ? "H2" : "SQL") + " properly.");
			}
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
		p.sendMessage("    Messages:");
		p.sendMessage("        Ore found: " + OreMsg);
		p.sendMessage("        Must have light to mine: " + DarkMsg);
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
		p.sendMessage("    SQL: " + ((!SQL.equalsIgnoreCase("false")) ? ((SQL.equalsIgnoreCase("true") || SQL.equalsIgnoreCase("SQL")) ? ("mysql://" + sqlUser + ":" + sqlPass + "@" + sqlURL + ":" + sqlPort + "/" + sqlDatabase + "/" + sqlPrefix + "-log" + " (" + sqlDays + ", " + sqlLimit + ")") : "H2 Database") : false));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("foundboxx") || commandLabel.equalsIgnoreCase("fb")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.AQUA + "[FoundBoxx] Commands:");
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
					try {
						if (Updater.update(this))
							sender.sendMessage(ChatColor.GREEN + "[FoundBoxx] Plugin updated. Reload to complete. If you think this is bugged, inform Jaryl.");
						else
							sender.sendMessage(ChatColor.YELLOW + "[FoundBoxx] No updates available.");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "[FoundBoxx] Problem updating, check console.");
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
			int coal = 0;
			int iron = 0;
			int red = 0;
			int lapis = 0;
			int gold = 0;
			int dias = 0;
			
			HashMap<Integer, Integer> extra = new HashMap<Integer, Integer>();
			
			try {
				ResultSet rs = sql.Query("SELECT * FROM `" + sqlPrefix + "-log` WHERE `player` LIKE '" + name + "' AND `date` >= CURDATE() -" + days + " LIMIT " + sqlLimit + ";");
				while (rs.next())
				{
					int id = rs.getInt("block_id");
					if (Diamonds && id == 56)
					{
						dias++;
					}
					if (Gold && id == 14)
					{
						gold++;
					}
					if (Iron && id == 15)
					{
						iron++;
					}
					if (Lapis && id == 21)
					{
						lapis++;
					}
					if (Red && (id == 73 || id == 74))
					{
						red++;
					}
					if (Coal && id == 16)
					{
						coal++;
					}
					
					if (ExtraBlks.size() > 0)
					{
						if (ExtraBlks.contains(id))
						{
							extra.put(id, (extra.containsKey(id) ? extra.get(id) + 1 : 0));
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				asker.sendMessage("[FoundBoxx] Unable to load values above for checking.");
			}
			
			asker.sendMessage(ChatColor.AQUA + "[FoundBoxx] Farming rates for " + name + " for the past " + days + " day(s):");
			if (Diamonds && dias > 0)
				asker.sendMessage("    Diamonds: " + (dias > (70 * Integer.parseInt(days)) ? ChatColor.RED : (dias > (50 * Integer.parseInt(days)) ? ChatColor.YELLOW : "")) + dias);
			if (Gold && gold > 0)
				asker.sendMessage("    Gold: " + (gold > (170 * Integer.parseInt(days)) ? ChatColor.RED : (gold > (100 * Integer.parseInt(days)) ? ChatColor.YELLOW : "")) + gold);
			if (Iron && iron > 0)
				asker.sendMessage("    Iron: " + (iron > (500 * Integer.parseInt(days)) ? ChatColor.RED : (iron > (350 * Integer.parseInt(days)) ? ChatColor.YELLOW : "")) + iron);
			if (Lapis && lapis > 0)
				asker.sendMessage("    Lapis Lazuli: " + (lapis > (90 * Integer.parseInt(days)) ? ChatColor.RED : (lapis > (60 * Integer.parseInt(days)) ? ChatColor.YELLOW : "")) + lapis);
			if (Red && red > 0)
				asker.sendMessage("    Red Stone: " + red);
			if (Coal && coal > 0)
				asker.sendMessage("    Coal: " + coal);
			
			if (extra.size() > 0)
			{
				for (int b : extra.keySet())
				{
					asker.sendMessage("    '" + Material.getMaterial(b).name() + "': " + extra.get(b));
				}
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
    	
    	System.out.println("[" + this.getDescription().getName() + " v" + this.getDescription().getVersion() + "] Enabled.");
	
    	try {
    		if (Updater.update(this))
				System.out.println("[FoundBoxx] Plugin auto-updated. Reload to complete. If you think this is bugged, inform Jaryl.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
