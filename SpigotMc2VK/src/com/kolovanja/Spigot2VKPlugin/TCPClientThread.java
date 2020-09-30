package com.kolovanja.Spigot2VKPlugin;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;

public class TCPClientThread extends Thread {
	int port;
	public int sleeptime = 360; //Seconds for sleep if cant connect
	int delay; //Delay for sending messages TO VK

	byte[] utf8bytes;
	boolean admin;
	
	TCPClientThread(int port , boolean admin, int delay/*, String message*/) /*throws UnsupportedEncodingException*/ {
		this.port = port;
		this.admin = admin;
		this.delay = delay; //in seconds

	}

	public void run() {
		Socket clientSocket;

		{ while (true) //endless cycle
			try {
				if (admin==true)
				{
					StringBuilder message = new StringBuilder();
					String line = Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().poll();
					clientSocket = new Socket("localhost", port);
					DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

					while (line != null) {
						if (message.length() + line.length() + 1 > 2000) {
							//Send to VK
							utf8bytes = message.toString().getBytes("UTF-8");  
							outToServer.write(utf8bytes);
							clientSocket.close();
							message = new StringBuilder();
						}
						message.append(line).append("\n");

						line = Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().poll();
					}

					if (StringUtils.isNotBlank(message.toString().replace("\n", "")))
						//Send to VK
					{ 
						utf8bytes = message.toString().getBytes("UTF-8");  
						outToServer.write(utf8bytes);
						clientSocket.close();
					}
					Thread.sleep(delay*1500);
				}else {
					StringBuilder message = new StringBuilder();
					String line = Spigot2VKPlugin.getPlugin().getUserMessageQueue().poll();
					clientSocket = new Socket("localhost", port);
					DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
					while (line != null) {
						if (message.length() + line.length() + 1 > 2000) {
							//Send to VK
							utf8bytes = message.toString().getBytes("UTF-8");  
							outToServer.write(utf8bytes);
							clientSocket.close();
							message = new StringBuilder();
						}
						message.append(line).append("\n");

						line = Spigot2VKPlugin.getPlugin().getUserMessageQueue().poll();
					}

					if (StringUtils.isNotBlank(message.toString().replace("\n", "")))
						//Send to VK
					{ 
						utf8bytes = message.toString().getBytes("UTF-8");  
						outToServer.write(utf8bytes);
						clientSocket.close();
					}
					
				}

				Thread.sleep(delay*1000);
			} catch (UnknownHostException e) {
				try {
					Thread.sleep(sleeptime*1000);
					
					if(Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().size()>0)
					Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().clear();
					if(Spigot2VKPlugin.getPlugin().getUserMessageQueue().size()>0)
						Spigot2VKPlugin.getPlugin().getUserMessageQueue().clear();
				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}

				System.out.print(e.getLocalizedMessage());

			
			} catch (IOException e) {

				try {
					Thread.sleep(sleeptime*1000);
					if(Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().size()>0)
						Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().clear();
					if(Spigot2VKPlugin.getPlugin().getUserMessageQueue().size()>0)
						Spigot2VKPlugin.getPlugin().getUserMessageQueue().clear();
				} catch (InterruptedException e1) {
					

					e1.printStackTrace();
				}

				System.out.print(e.getLocalizedMessage());
			}
			catch (InterruptedException e) {

			return;
			}

		}
	}
	


}