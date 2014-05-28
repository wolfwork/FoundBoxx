package me.Jaryl.FoundBoxx;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsHandler {
	private FoundBoxx plugin; //NOTE TO SELF: RMB TO CHANGE THIS ON NEW PROJECTS
	public PermissionsHandler(FoundBoxx pl) { //NOTE TO SELF: RMB TO CHANGE THIS ON NEW PROJECTS
		plugin = pl;
	}

    // PermissionsEX
    public ru.tehkode.permissions.PermissionManager pexPermissions;
    Boolean PEXB = false;
	
	public boolean hasPermission(Player p, String perm, boolean def, boolean ignoreop)
	{
		if (plugin.Perms)
		{
			if (PEXB) // PEX
				return (pexPermissions.has(p, perm) || p.hasPermission(perm));
			
			return p.hasPermission(perm);
		}
		
		return ((!ignoreop && p.isOp()) || def);
	}
	public boolean hasPermission(CommandSender p, String perm, boolean def, boolean ignoreop)
	{
		if (!(p instanceof Player))
		{
			return true;
		}
		
		return hasPermission((Player)p, perm, def, ignoreop);
	}
	
	public void setupPermissions() {
        if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
            pexPermissions = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
            PEXB = true;
            System.out.println("[" + plugin.getDescription().getName() + "] Listening to PermissionsEX.");
            return;
        }
        
        System.out.println("[" + plugin.getDescription().getName() + "] No custom permission plugins found, using original permissions.");
	}
}
