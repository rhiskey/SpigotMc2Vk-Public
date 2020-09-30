package com.kolovanja.Spigot2VKPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinLeaveEvent implements Listener{
    //public static Spigot2VKPlugin plugin;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // Called when a player joins a server
        Player player = event.getPlayer();
        String joinMessage = event.getJoinMessage();
        joinMessage= Utils.aggressiveStrip(joinMessage); //remove colors
    	//Send Join Message to VK BOT CHAT
        joinMessage = joinMessage.replace("§e", "");
		Spigot2VKPlugin.getPlugin().getUserMessageQueue().add(joinMessage);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Called when a player leaves a server
        Player player = event.getPlayer();
        String quitMessage = event.getQuitMessage();
        quitMessage= Utils.aggressiveStrip(quitMessage); //remove colors
    	//Send Leave Message to VK BOT CHAT
 
        quitMessage=quitMessage.replace("§e", "");
		Spigot2VKPlugin.getPlugin().getUserMessageQueue().add(quitMessage);
    }
    
}
