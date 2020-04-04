package fr.zetioz.opsyfreeze.object;

import org.bukkit.Location;

import net.md_5.bungee.api.ChatColor;

public class Freeze {
	
	private String freezer;
	private String reason;
	private Location location;
	
	public Freeze(String freezer, String reason, Location location)
	{
		this.freezer = freezer;
		this.reason = ChatColor.translateAlternateColorCodes('&', reason);
		this.location = location;
	}
	
	public String getFreezer()
	{
		return this.freezer;
	}
	
	public String getReason()
	{
		return this.reason;
	}
	
	public Location getLocation()
	{
		return this.location;
	}
}
