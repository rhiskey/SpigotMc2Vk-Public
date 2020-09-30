package com.kolovanja.Spigot2VKPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.Bukkit;


public class playerChatListener implements Listener{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

	}
}
