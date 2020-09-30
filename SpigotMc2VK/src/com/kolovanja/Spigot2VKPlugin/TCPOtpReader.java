package com.kolovanja.Spigot2VKPlugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


//TCP Listener Server Thread
public class TCPOtpReader extends Thread {
	int port;
	int delay; // in Seconds for Executing Commands
	int sleeptime = 5; //seconds
	String playerName;
	String otp;
	
	TCPOtpReader(int port, int delay) {
		this.port = port;
		this.delay = delay; //in seconds
	}

	@SuppressWarnings("resource")
	public void run() {
		String message;

		ServerSocket welcomeSocket = null;
		try {
			welcomeSocket = new ServerSocket(port);
		} catch (IOException e) {

			e.printStackTrace();
		}

		while (true) {
			Socket connectionSocket;
			try {
				connectionSocket = welcomeSocket.accept();
				BufferedReader inFromClient =
						new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));			
				message = inFromClient.readLine();

				byte[] content = message.getBytes();

				String decodedMessage = new String(content, "UTF-8");  

				int divider = decodedMessage.indexOf(':');
				int success = decodedMessage.indexOf('+');
				int unlink =  decodedMessage.indexOf('-');

				if (divider!= -1)
				{
					otp = decodedMessage.substring(divider+1);
					try {
						playerName = decodedMessage.substring(0, divider);
						Player player=Bukkit.getPlayer(playerName);
						String combo = ChatColor.RED +"[MC2VK]"+ChatColor.WHITE+" Отправь в группу ВК одноразовый код: " + ChatColor.BLUE+ otp;
						if (player.isOnline()==true)
							try {
								player.sendMessage(combo);	
							}catch (Exception plEx) {/*plEx.printStackTrace();*/}		
					}catch (Exception noPlayer) {}
				}
				else if (success!= -1) {
					playerName = decodedMessage.substring(success+1);

					Player player=Bukkit.getPlayer(playerName);

					String combo = ChatColor.RED +"[MC2VK]"+ChatColor.GREEN+" Учетная запись успешно привязана к ВК";	
					if ( player!=null) // if isOnline ==true
						player.sendMessage(combo);

					String pexCommand = "pex user " + playerName + " group set Linked [kolovanja2]";
					Spigot2VKPlugin.getPlugin().ExecCommand(pexCommand);	
				}
				else if (unlink!= -1) {
					playerName = decodedMessage.substring(unlink+1);
					Player player = Bukkit.getServer().getPlayer(playerName);
					String combo = ChatColor.RED +"[MC2VK]"+ChatColor.GREEN+" Учетная запись отвязана от ВК";
					if (player!=null)
						player.sendMessage(combo);
					//Remove Privilleges
					String pexCommandRemove = "pex user " + playerName + " group remove Linked [kolovanja2]";
					Spigot2VKPlugin.getPlugin().ExecCommand(pexCommandRemove);
					String pexCommandSet = "pex user " + playerName + " group set default [kolovanja2]";
					Spigot2VKPlugin.getPlugin().ExecCommand(pexCommandSet);
				}
				Thread.sleep(delay*1000);			
			} catch (IOException e) {
				try {
					Thread.sleep(sleeptime*1000);					
				} catch (InterruptedException e1) {

				}

			} catch (InterruptedException e) { 

				e.printStackTrace();
			}

		}
	}
}