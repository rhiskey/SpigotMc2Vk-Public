package com.kolovanja.Spigot2VKPlugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//-------------------------------------------------------------
// port_tcp_chat_uplink - канал общения TCP для ЮЗЕРОВ, ТОЛЬКО ЧАТ В ЛИЧКУ
// port_tcp_console_uplink
public class ConsoleFilter implements Filter { // public class ConsoleFilter extends JavaPlugin implements Filter {

	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM hh:mm:ss");
	// Log to FIle
	static String fileName = "MC2VKConsole.log";
	// For VK Reading from the config
	private static int chat_id;
	private static String access_token; // Get from Your Group https://vk.com/PUBLICNAME?act=tokens
	public static int port_tcp_chat_uplink = 8334; // FROM JAVA TO GO
	public static int port_tcp_console_uplink = 8336; // FROM JAVA TO GO
	private static boolean isConsole;
	public ArrayList<String> messages = new ArrayList<>();
	// super();

	// TCPClientThread tcp = new TCPClientThread(port_tcp_console_uplink,null);

	// Get config from file
	public static void setCfg(String accesstoken, int chatid, boolean console) {
		access_token = accesstoken;
		chat_id = chatid;
		isConsole = console;		
	}
	
	public static String getFilename()
	{
		return fileName;
	}

	@Override
	public Result getOnMismatch() {
		return null;
	}

	@Override
	public Result getOnMatch() {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s,
			Object... objects) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3, Object o4) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3, Object o4, Object o5) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o,
			Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Object o,
			Throwable throwable) {
		return null;
	}

	@Override
	public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Message message,
			Throwable throwable) {
		return null;
	}

	@Override
	public Result filter(LogEvent logEvent) {
		String message = logEvent.getMessage().getFormattedMessage();
		
		String timestamp = Utils.timeStamp();
		
		//if message from dynmap (contains [WEB]) -> send to userChat BOT
		boolean isFoundWEB = message.contains("[WEB]"); 
		boolean isFoundServer = message.contains("[Server]"); //ещё Очистка от выброшенных вещей, нужно отсеять
		if (isFoundWEB==true /*||isFoundServer==true*/) // If message from Server(admin) OR from WEB
		{

				String formattedMessage = Utils.aggressiveStrip(message);

		}
		
		message = "[" + timestamp + "] [" + logEvent.getLevel() + "] "
				+ message;


		// Write to file
		writeToFile(message, fileName);



		//TCPClientThread tcp;
        // remove coloring
		message = Utils.aggressiveStrip(message);
		Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().add(message);
		


		return null;
	}

	/*
	 * private String getDataFolder() { null; }
	 */

	// Send message to VK with delay <3 request in second
	public void writeToFile(String message, String fileName) {
		try {
			File file = new File(fileName);
			FileWriter fr = new FileWriter(file, true);
			BufferedWriter br = new BufferedWriter(fr);
			PrintWriter pr = new PrintWriter(br);
			pr.println(message);
			pr.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Send message to VK with delay <3 request in second
	public void sendVK(String message, int chat_id, String access_token) throws Exception {
		String MessageEncoded = urlEncode(message);
		// getCfg();

		int peer_id = 2000000000 + chat_id; // 2000000000+chat_id
		String peer = String.valueOf(peer_id);

		String token = "access_token=" + access_token;
		String params = "&peer_id=" + peer + "&chat_id=" + chat_id + "&message=" + MessageEncoded
				+ "&dont_parse_links=0&disable_mentions=0";
		String method = "messages.send";

		URLConnection connection;
		try {
			connection = new URL("https://api.vk.com/method/" + method + "?" + params + "&v=5.68&" + token)
					.openConnection(); // v5.71
			InputStream is = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(is);
			char[] buffer = new char[256];
			int rc;
			StringBuilder sb = new StringBuilder();
			while ((rc = reader.read(buffer)) != -1)
				sb.append(buffer, 0, rc);
			reader.close();
			String response = getJson(sb.toString());
			if (response != null) {
				System.out.println("[Spigot2VK] Error: " + response);
				if (response.equals("Captcha needed")) {
					System.out.println(sb);
				}
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				System.out.println("got interrupted!");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getJson(String Json) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(Json);
		JSONObject jsonresponse = (JSONObject) jsonObj.get("error");
		if (jsonresponse != null) {
			return String.valueOf(jsonresponse.get("error_msg"));
		}
		return null;
	}

	public static String urlEncode(final String text) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return text;
		}
	}

	public static String getUnixTime(long l, String format) {
		SimpleDateFormat SDF = new SimpleDateFormat(format);
		String date = SDF.format(new Date(l));
		if (date != null) {
			return date;
		}
		return null;
	}

	@Override
	public State getState() {
		return null;
	}

	@Override
	public void initialize() {

	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isStarted() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return false;
	}
}