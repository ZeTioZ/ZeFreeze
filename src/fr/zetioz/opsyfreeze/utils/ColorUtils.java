package fr.zetioz.opsyfreeze.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ColorUtils
{
	private ColorUtils() {}
	
	/**
	 * A simple way to color a string or a list of string
	 * Where @textToColor is indeed the line of text to color
	*/
	public static final String color(String textToColor)
	{
		return ChatColor.translateAlternateColorCodes('&', textToColor);
	}
	
	public static final List<String> color(List<String> textToColor)
	{
		List<String> coloredText = new ArrayList<>();
		
		for(String line : textToColor)
		{
			line = ChatColor.translateAlternateColorCodes('&', line);
			coloredText.add(line);
		}
		return coloredText;
	}
	
	/**
	 * A simple way to discolor a string or a list of string
	 * Where @textToDiscolor is indeed the line of text to discolor
	*/
	public static final String discolor(String textToDiscolor)
	{
		return ChatColor.stripColor(textToDiscolor);
	}
	
	public static final List<String> discolor(List<String> textToDiscolor)
	{
		List<String> discoloredText = new ArrayList<>();
		
		for(String line : textToDiscolor)
		{
			line = ChatColor.stripColor(line);
			discoloredText.add(line);
		}
		return discoloredText;
	}
	
	
	// Player
	public static final void sendMessage(Player player, String textToColor, String prefix)
	{
		prefix = prefix != null ? prefix : "";
		player.sendMessage(color(prefix + textToColor));
	}
	
	public static final void sendMessage(Player player, List<String> textToColor, String prefix)
	{
		prefix = prefix != null ? prefix : "";
		for(String line : textToColor)
		{
			player.sendMessage(color(prefix + line));
		}
	}
	
	public static final void sendMessage(Player player, String textToColor, String prefix, String... placeholders)
	{
		prefix = prefix != null ? prefix : "";
		if(placeholders != null && placeholders.length % 2 == 0)
		{
			for(int i = 0; i < placeholders.length; i = i + 2)
			{
				textToColor = textToColor.replace(placeholders[i], placeholders[i + 1]);
			}
		}
		player.sendMessage(color(prefix + textToColor));
	}
	
	public static final void sendMessage(Player player, List<String> textToColor, String prefix, String... placeholders)
	{
		prefix = prefix != null ? prefix : "";
		for(String line : textToColor)
		{
			if(placeholders != null && placeholders.length % 2 == 0)
			{
				for(int i = 0; i < placeholders.length; i = i + 2)
				{
					line = line.replace(placeholders[i], placeholders[i + 1]);
				}
			}
			player.sendMessage(color(prefix + line));
		}
	}
	
	// Command Sender
	public static final void sendMessage(CommandSender sender, String textToColor, String prefix)
	{
		prefix = prefix != null ? prefix : "";
		sender.sendMessage(color(prefix + textToColor));
	}
	
	public static final void sendMessage(CommandSender sender, List<String> textToColor, String prefix)
	{
		prefix = prefix != null ? prefix : "";
		for(String line : textToColor)
		{
			sender.sendMessage(color(prefix + line));
		}
	}
	
	public static final void sendMessage(CommandSender sender, String textToColor, String prefix, String... placeholders)
	{
		prefix = prefix != null ? prefix : "";
		if(placeholders != null && placeholders.length % 2 == 0)
		{
			for(int i = 0; i < placeholders.length; i = i + 2)
			{
				textToColor = textToColor.replace(placeholders[i], placeholders[i + 1]);
			}
		}
		sender.sendMessage(color(prefix + textToColor));
	}
	
	public static final void sendMessage(CommandSender sender, List<String> textToColor, String prefix, String... placeholders)
	{
		prefix = prefix != null ? prefix : "";
		for(String line : textToColor)
		{
			if(placeholders != null && placeholders.length % 2 == 0)
			{
				for(int i = 0; i < placeholders.length; i = i + 2)
				{
					line = line.replace(placeholders[i], placeholders[i + 1]);
				}
			}
			sender.sendMessage(color(prefix + line));
		}
	}
	
	
	// Broadcast
	public static final void broadcastMessage(String textToColor, String prefix)
	{
		prefix = prefix != null ? prefix : "";
		Bukkit.getServer().broadcastMessage(color(prefix + textToColor));
	}
	
	public static final void broadcastMessage(List<String> textToColor, String prefix)
	{
		prefix = prefix != null ? prefix : "";
		for(String line : textToColor)
		{
			Bukkit.getServer().broadcastMessage(color(prefix + line));
		}
	}
	
	public static final void broadcastMessage(String textToColor, String prefix, String... placeholders)
	{
		prefix = prefix != null ? prefix : "";
		if(placeholders != null && placeholders.length % 2 == 0)
		{
			for(int i = 0; i < placeholders.length; i = i + 2)
			{
				textToColor = textToColor.replace(placeholders[i], placeholders[i + 1]);
			}
		}
		Bukkit.getServer().broadcastMessage(color(prefix + textToColor));
	}
	
	public static final void broadcastMessage(List<String> textToColor, String prefix, String... placeholders)
	{
		prefix = prefix != null ? prefix : "";
		for(String line : textToColor)
		{
			if(placeholders != null && placeholders.length % 2 == 0)
			{
				for(int i = 0; i < placeholders.length; i = i + 2)
				{
					line = line.replace(placeholders[i], placeholders[i + 1]);
				}
			}
			Bukkit.getServer().broadcastMessage(color(prefix + line));
		}
	}
}
