package com.kolovanja.Spigot2VKPlugin;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.kolovanja.auth.OneTimePassword;



public class UserCommandSender implements CommandExecutor {
	
	public static Spigot2VKPlugin plugin;
	private static int otpPort = 8340;

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Here we need to give items to our player
        }
    	
		if (args.length == 0) {
			if(!sender.hasPermission("Spigot2VK.help")) {
				sender.sendMessage("§cSpigot2VK §8у вас недостаточно прав.");
				return true;
			}
			sender.sendMessage("§cSpigot2VK.");
			sender.sendMessage("");

			sender.sendMessage("§c/"+label+" reload перезагружает конфиг и плагин");
			sender.sendMessage("");		
		}
		else {
			switch (args[0].toLowerCase()) {
			
			case "link":

				if (sender instanceof Player) {
					Player player = (Player) sender;

					//Generate Random Password
					String otpString = OneTimePassword.generateOTP();
					System.out.println(otpString);
					String otpMessage = "Чтобы получить расширенные возможности, отправьте в сообщения нашего паблика временный код привязки к ВК: " + otpString;				
					player.sendMessage(otpMessage);

					//TODO Pass otpString and playerName to TCP Bot
					String playerName = player.getName();

				}
				//}
				break;

			case "rl":
			case "reload":
				if(!sender.hasPermission("spigotmc2vk.reload")) {
					sender.sendMessage("§cSpigot2VK §8» §6у вас недостаточно прав.");
					return true;
				}
				Spigot2VKPlugin.getPlugin().reloadConfig();

				Spigot2VKPlugin.getPlugin().loadConfig();
				sender.sendMessage("§cSpigot2VK §8» §6Конфигурация успешно перезагружена!");
				break;
			}
		}
		return false;
    }
    
	//Send Any message in console
	public void tellConsole(String message){
	    Bukkit.getConsoleSender().sendMessage(message);

	}
	

	public boolean hasSendmsg(Player player) {
		if (player.hasPermission("spigotmc2vk.sendmsg")) {
			return true;
		} else if (player.hasPermission("spigotmc2vk.*")) {
			return true;
		}
		return false;
	}
	
	public boolean hasReload(Player player) {
		if (player.hasPermission("spigotmc2vk.reload")) {
			return true;
		} else if (player.hasPermission("spigotmc2vk.*")) {
			return true;
		}
		return false;
	}
}
