# SpigotMc2Vk
This plugin reads Console output from Spigot/Bukkit Minecraft Server and logs it into Console.log.
Can execute commands remotely.
Also this plugin resends console output to chat at vk.com (Social Network). So from VK chat you can execute commands in console.
### Download
Spigot Plugin | Relay Server
------------ | -------------
[JAR](https://github.com/rhiskey/SpigotMc2Vk/tree/master/SpigotMc2VK/server/plugins) | [WINDOWS](https://github.com/rhiskey/SpigotMc2Vk/raw/master/SpigotMc2VK/server/spigot2vk.exe)
 [KOLOVANJA Servers](https://vk.com/kolovanja)| [Linux](https://github.com/rhiskey/SpigotMc2Vk/tree/master/SpigotMc2VK/server)
 
### Installing Plugin and Relay Server
To install workspace correctly, use Eclipse and (for dependencies):
~~1) Unzip ExtractHere.rar in directory, so in folder apache-log4j-2.13.0-bin should be 2 .jar files~~

2) Launch Relay Server \SpigotMc2Vk\SpigotMc2VK\server\GO2VK.exe
3) Run Minecraft server by executing start.bat
~~4) Change value "access-token" in \SpigotMc2Vk\SpigotMc2VK\server\plugins\SpigotMc2VKPlugin\config.yml of your's User Access Token: https://vkhost.github.io/~~
Invite User/or yourself in Chat and change "chat-id"

![Bot API Image VK](https://i.ibb.co/0qB1YjV/screen-api.png)

- [ ] 5) Change "access-token" in ~~GOLang~~ Relay Server config on your's Community Access Token (Allow access to community messages)
You can obtain it here: https://vk.com/dev/access_token?f=2.%20%D0%9A%D0%BB%D1%8E%D1%87%20%D0%B4%D0%BE%D1%81%D1%82%D1%83%D0%BF%D0%B0%20%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D1%81%D1%82%D0%B2%D0%B0
6) Restart Your Server
7) Done

[KOLOVANJA Servers](https://vk.com/kolovanja)

### TODO
- [x] Resending Console to VK
- [ ] Bot for Community VK @GrigorySazanov
- [ ] Relay Server Config
