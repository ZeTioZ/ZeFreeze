package fr.zetioz.zefreeze;

import org.bukkit.Location;

import static fr.zetioz.coreutils.ColorUtils.color;

public class FreezeElement {
	
	private String freezer;
	private String reason;
	private Location location;
	
	public FreezeElement(String freezer, String reason, Location location)
	{
		this.freezer = freezer;
		this.reason = color(reason);
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
