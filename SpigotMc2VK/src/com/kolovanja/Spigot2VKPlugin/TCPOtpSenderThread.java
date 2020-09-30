package com.kolovanja.Spigot2VKPlugin;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPOtpSenderThread implements Runnable{
	int port, sleeptime = 5; //seconds
	String playerName;
	String otp;
	byte[] utf8bytes;
	private volatile boolean exit = false;

	TCPOtpSenderThread(int port, String otp, String playerName) /*throws UnsupportedEncodingException*/ {
		this.port = port;
		this.otp = otp;
		this.playerName = playerName;
	}

	public void run() {
		Socket clientSocket;

        while(!exit)
			try {

				clientSocket = new Socket("localhost", port);
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				
				//Pass username+OTP 
				String userOTP = playerName+":"+otp;
				
				utf8bytes = userOTP.toString().getBytes("UTF-8");  		
				outToServer.write(utf8bytes);
				clientSocket.close();

			}
		catch (UnknownHostException e) {
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e1) {

			}

		} catch (IOException e) {

			try {
				Thread.sleep(sleeptime*1000);
			} catch (InterruptedException e1) {

			}

		}
	}
	
    public void stop(){
        exit = true;
    }
}
