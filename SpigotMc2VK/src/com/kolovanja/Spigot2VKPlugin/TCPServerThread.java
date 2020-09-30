package com.kolovanja.Spigot2VKPlugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;


//TCP Listener Server Thread
public class TCPServerThread extends Thread {
	int port;
	boolean admin;
	int delay; // in Seconds for Executing Commands
	TCPServerThread(int port, boolean admin, int delay) {
		this.port = port;
		this.admin = admin;
		this.delay = delay; //in seconds
	}

	@SuppressWarnings("resource")
	public void run() {
		String message;

		ServerSocket welcomeSocket = null;
		try {
			welcomeSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

				String decodedMessage = new String(content, "UTF-8");   // decoding


				if (admin==true )
				{
					try {
						//send to console FROM VK
						Spigot2VKPlugin.getPlugin().ExecCommand(decodedMessage);
						Thread.sleep(delay*1000);
					}catch (Exception e) {
						//TODO When not exec
					}
					
				}
				else {
	
					try {
						//Tell console & players

							String formattedMessage = /*ChatColor.DARK_RED+*/"[VK] "+decodedMessage; 
							Bukkit.broadcastMessage(formattedMessage); //message
							//Resend Back to VK Community BOT
							Spigot2VKPlugin.getPlugin().getUserMessageQueue().add(formattedMessage);
							Thread.sleep(delay*1000);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			} catch (IOException e) {

				e.printStackTrace();

			} catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}
}