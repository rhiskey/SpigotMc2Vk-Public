package com.kolovanja.Spigot2VKPlugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.kolovanja.Spigot2VKPlugin.Utils;
import com.kolovanja.auth.OneTimePassword;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.net.*;
import java.io.*;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.LogManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandSender.Spigot;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;



@SuppressWarnings("unused")
public class Spigot2VKPlugin extends JavaPlugin implements Listener {
	private File customConfigFile;
	private FileConfiguration customConfig;
	public static int port_tcp_chat_downlink = 8335;	//FROM GO TO JAVA
	public static int port_tcp_console_downlink = 8337; //From GO TO JAVA
	public static int port_tcp_downlink = 8340; 			//From BOT to Plugin
	private String access_token;
	private int chat_id;
	private int timer; //For schedule
	public static Spigot2VKPlugin plugin;
	private boolean isConsole;
    static FileConfiguration configyml;
    static File cfg;
    
	public static int timer2; //for group update
	public static List<Integer> Groups;
	public static String Token;
	public static int Record;
	public static long TimeRecord;
	public static String Status;
	public static String RecordFormatDate;
	public static Spigot splugin;
	public static int online;
	public static int TempRecord;
	public static long TempRecordTime;
	public static String TempFormatDate;
	public static long lastupdate;
    
	public static int delay = 1; //for sending messages
	public static String community_token; //for bot api
	
	private Queue<String> consoleMessageQueue = new LinkedList<>();
	private Queue<String> userMessageQueue = new LinkedList<>();


	// Threads READ
	TCPServerThread tcp = new TCPServerThread(port_tcp_chat_downlink,false, delay); // if false = user
	TCPServerThread tcp2 = new TCPServerThread(port_tcp_console_downlink,true, delay); //if true = admin
	// Threads SEND
	TCPClientThread tcp_c = new TCPClientThread(ConsoleFilter.port_tcp_console_uplink,true, delay);  //if true = admin Chat
	TCPClientThread tcp_u = new TCPClientThread(ConsoleFilter.port_tcp_chat_uplink,false, delay); // if false = user Community BOT

	TCPOtpReader tcp_otp_r = new TCPOtpReader(port_tcp_downlink,delay); 
	
	// Init Sending to Console
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	public static String dbHost;
	public static int dbPort;
	public static String dbName;
	public static String dbLogin;
	public static String dbPass;
	
	
	public static Spigot2VKPlugin getPlugin() {
		return getPlugin(Spigot2VKPlugin.class);
	}


	public void init() {
		Bukkit.getPluginManager().registerEvents(this, this);
		
		if(!getDataFolder().exists())
		{

			getDataFolder().mkdir();
		}


		//Execute commands as Player
		this.getCommand("sendmsg").setExecutor(new UserCommandSender());
		this.getCommand("reload").setExecutor(new UserCommandSender());

		this.getCommand("link").setExecutor(new UserCommandSender());
		
		// Load/Create Default config.yml file
		saveDefaultConfig();
		loadConfig();	       
		
		// Creates Listener for read new messages from chat
		getServer().getPluginManager().registerEvents(new MessageListener(), this);
		// Creates Listener for Join and leave Events
		getServer().getPluginManager().registerEvents(new JoinLeaveEvent(), this);
		

		//Print to Console	
		print("Created by KOLOVANJA Servers");
		//Say That Server Is Successfully RUN
		Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().add("[ВНИМАНИЕ] Сервер успешно запущен!");

	}


	// Fired when plugin is first enabled
	@Override
	public void onEnable() {
		init();

		plugin = this;

		////Log Console
		ConsoleFilter filter = new ConsoleFilter();
		((org.apache.logging.log4j.core.Logger)LogManager.getRootLogger()).addFilter(filter);

		//Get Messages from VK and send IT to MC Server
		try {
			//Server Threads - To READ  Messages
			tcp.start();
			tcp2.start();
			//Client Threads - To SEND MEssages
			tcp_c.start();
			tcp_u.start();
			//OTP
			tcp_otp_r.start();

		} catch (Exception e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}


	}


	// Send Message To Console
	public void print(String text) {
		console.sendMessage(ChatColor.AQUA+text);
	}

   
	// Fired when plugin is disabled
	@Override
	public void onDisable() {	

		//Say That Server Is Shutting Down
		Spigot2VKPlugin.getPlugin().getConsoleMessageQueue().add("[ВНИМАНИЕ] Сервер выключается...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Finish Threads
		if(tcp!=null)
			tcp.interrupt();
		if(tcp2!=null)
			tcp2.interrupt();
		if(tcp_c!=null)
			tcp_c.interrupt();
		
		String fileName = ConsoleFilter.fileName;
		File file = new File(fileName);
		file.delete();
		
		//Send Status VK Offline Server
	
		plugin.getPluginLoader().disablePlugin(plugin);

		
	}

	//Load Config from yml if exists. 
	//TODO Pass values to Confole Filter
	public void loadConfig() {	
		File cfg = new File(getDataFolder(),"config.yml");

		if(!cfg.exists()) {		//Create Default config file inside plugin folder
			try {
				cfg.createNewFile();
				saveDefaultConfig();


				this.getConfig().addDefault("Chat-id", 1);
				this.getConfig().addDefault("Access-token", "abcde");
				this.getConfig().addDefault("Timer", 10);
				this.getConfig().addDefault("Console", false);

				this.getConfig().addDefault("Timer2", 60);
				this.getConfig().addDefault("Token", "abcde");
				
				List<Integer> groupList = new ArrayList<>();
				groupList.add(123);
				this.getConfig().addDefault("Groups", groupList);
				
				this.getConfig().addDefault("Record.number", 0);
				
				this.getConfig().addDefault("Record.time", 1581626095755L);
				
				this.getConfig().addDefault("Record.FormatDate", "dd.MM.yyyy HH:mm");
				this.getConfig().addDefault("TempRecord.number",0);
				this.getConfig().addDefault("TempRecord.time",1587502804071L);	
				this.getConfig().addDefault("TempRecord.FormatDate","dd.MM.yyyy");
				this.getConfig().addDefault("TempRecord.lastupdate",1587502804072L);
				this.getConfig().addDefault("Status","\'Онлайн: %online | Суточный рекорд: %temprecord, зафиксирован: %temprectime | Рекорд: %record, зафиксирован: %rectime | Игроков: %maxplayers \'");
				
				//DB settings
				this.getConfig().addDefault("dbhost","192.168.0.0");
				this.getConfig().addDefault("dbport", 3306);
				this.getConfig().addDefault("dbname", "authme");
				this.getConfig().addDefault("dblogin", "login");
				this.getConfig().addDefault("dbpass", "password");
				// The configuration saver of the API will ignore all of the default values unless we specify to "copy the defaults". Defaults never override the settings of the user.
				this.getConfig().options().copyDefaults(true);

				this.saveConfig();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else //Read values from cfg
		{
	  
		}
		configyml = YamlConfiguration.loadConfiguration(cfg);
		//Read values from cfg anyway
		access_token = getConfig().getString("Access-token"); 
		chat_id = getConfig().getInt("Chat-id"); 
		timer = getConfig().getInt("Timer");
		isConsole = getConfig().getBoolean("Console");

		//Pass Config values to ConsoleFilter and MessageListener to switch ChatLogs (console or player chat)
		ConsoleFilter.setCfg(access_token,chat_id,isConsole);
		MessageListener.setCfg(isConsole);
		
		timer2 = this.getConfig().getInt("Timer2");
		Groups = this.getConfig().getIntegerList("Groups");
		Token = this.getConfig().getString("Token");
		Record = this.getConfig().getInt("Record.number");
		TimeRecord = this.getConfig().getLong("Record.time");
		Status = this.getConfig().getString("Status");
		RecordFormatDate = this.getConfig().getString("Record.FormatDate");
		TempRecord = this.getConfig().getInt("TempRecord.number");
		TempRecordTime = this.getConfig().getLong("TempRecord.time");
		TempFormatDate = this.getConfig().getString("TempRecord.FormatDate");
		lastupdate = this.getConfig().getLong("TempRecord.lastupdate");
		dbHost = this.getConfig().getString("DataBase.dbhost");
		dbPort = this.getConfig().getInt("DataBase.dbport");
		dbName = this.getConfig().getString("DataBase.dbname");
		dbLogin = this.getConfig().getString("DataBase.dblogin");
		dbPass = this.getConfig().getString("DataBase.dbpass");
		MysqlCon.MysqlConSet(dbHost, dbPort, dbName, dbLogin, dbPass);

	}
	
	
    public static FileConfiguration getCFG() {
        return configyml;
    }

    public static void saveCFG() {
        try {
            configyml.save(cfg);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
        }
    }
    
    public static void reloadCFG() {
    	configyml = YamlConfiguration.loadConfiguration(cfg);
    }

	public FileConfiguration getCustomConfig() {
		return this.customConfig;
	}

	private void createCustomConfig() {
		customConfigFile = new File(getDataFolder(), "config.yml");
		if (!customConfigFile.exists()) {
			customConfigFile.getParentFile().mkdirs();
			saveResource("config.yml", false);
		}

		customConfig= new YamlConfiguration();
		try {
			customConfig.load(customConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void Scheduler() {
		//Check for updates from SPIGOT every day
		
		//Send Message to console and URL for new version
		
		online = Bukkit.getOnlinePlayers().size();
		if(online > Record) {
			Record = online;
			plugin.getConfig().set("Record.number", online);
			TimeRecord = System.currentTimeMillis();
			plugin.getConfig().set("Record.time", TimeRecord);
			plugin.saveConfig();
		}
		if(!Utils.getUnixTime(System.currentTimeMillis(), TempFormatDate).equals(Utils.getUnixTime(lastupdate, TempFormatDate))) {
			TempRecord = online;
			plugin.getConfig().set("TempRecord.number", online);
			TempRecordTime = System.currentTimeMillis();
			plugin.getConfig().set("TempRecord.time", TempRecordTime);
			lastupdate = System.currentTimeMillis();
			plugin.getConfig().set("TempRecord.lastupdate", lastupdate);
			plugin.saveConfig();
		} else {
			if(online > TempRecord) {
				TempRecord = online;
				plugin.getConfig().set("TempRecord.number", online);
				TempRecordTime = System.currentTimeMillis();
				plugin.getConfig().set("TempRecord.time", TempRecordTime);
				plugin.saveConfig();
			}
		}
		
		int count = MysqlCon.getUserCount();
		String msg = Status.replace("%online", ""+online).replace("%temprecord", ""+TempRecord).replace("%temprectime", ""+Utils.getUnixTime(TempRecordTime, TempFormatDate)).replace("%record", ""+Record).replace("%rectime", Utils.getUnixTime(TimeRecord, RecordFormatDate)).replace("%maxplayers", ""+count);
		//msg = msg +"Всего игроков: "+count;
		for(int r:Groups) {
			try {
				Utils.setStatus(msg, r, Token);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("ERROR Setting status to VK Group");
			}
		}
	}

	public void ExecCommand(String decodedMessage){
		new BukkitRunnable() {
			//int seconds = 4;
			public void run() {


				String commandToExec = decodedMessage; 
				Bukkit.dispatchCommand(console, commandToExec);
			}
		}.runTask(plugin);
	}

	public Queue<String> getConsoleMessageQueue() {
		return consoleMessageQueue;
	}

	public void setConsoleMessageQueue(Queue<String> consoleMessageQueue) {
		this.consoleMessageQueue = consoleMessageQueue;
	}

	public Queue<String> getUserMessageQueue() {
		return userMessageQueue;
	}
	public void setUserMessageQueue(Queue<String>userMessageQueue) {
		this.userMessageQueue = userMessageQueue;
	}
	

	
}