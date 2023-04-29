## PlayerServer
An open-source spigot plugin that automatically deploys and manages private servers in Minecraft. This plugin creates a Survival Multiplayer (SMP) Minecraft server from a template server folder
<br><br>
:warning: <b>Warning:</b><br>
This plugin requires a BungeeCord network and MySQL database hosted on a VPS or dedicated server running Linux

### Features
#### Spigot
<ul>
  <li>
    MySQL database support
  </li>
  <li>
    Create a server from a template folder
  </li>
  <li>
    Start the server
  </li>
  <li>
    Delete the server (Work in progress)
  </li>
  <li>
    Server shutdown after 1 hour if no players online
  </li>
  <li>
    Invite system
  </li>
</ul>

#### BungeeCord
<ul>
  <li>
    Servers are deployed and deleted without restarting the proxy
  </li>
  <li>
    Change the message of the day (MOTD) that displays in the server list 
  </li>
  <li>
    Automatic updates for private servers (Coming soon)
  </li>
  <li>
    Maintenance mode
  </li>
  <li>
    Whitelist system
  </li>
</ul>

### Commands
<ul>
  <li>
    /smp: Create, start, and join the server
  </li>
  <li>
    /delete: Delete the server
  </li>
  <li>
    /invite: Invite a player
  </li>
  <li>
    /remove: Remove an invited player
  </li>
  <li>
    /opme: Allow server owner to give themselves operator permissions
  </li>
  <li>
    /credits: Fun command to play the game end credits for a player 
  </li>
</ul>

#### Console commands (BungeeCord only)
<ul>
  <li>
    maintenance: Enable and disable maintenance mode
  </li>
  <li>
    whitelist: Add or remove a player in the maintenance whitelist
  </li>
</ul>

### Screenshots
#### Start the server:
![image](https://user-images.githubusercontent.com/39607018/212556292-417d2861-b182-4364-bf1b-dd1b882eedb1.png)

#### Join the server when online:
![image](https://user-images.githubusercontent.com/39607018/212556295-d19bf035-9d20-4c38-b020-d314b4fb4cf2.png)

#### Connection attempts when the server is not done loading:
![image](https://user-images.githubusercontent.com/39607018/212557194-492dd466-35c3-44d2-8f49-43b154197e83.png)

#### Server is already online:
![image](https://user-images.githubusercontent.com/39607018/212556624-4f631010-de89-4076-b744-10a907a5479f.png)
![image](https://user-images.githubusercontent.com/39607018/212557038-f82fbdca-e8c2-4ab9-add8-b46ffac333be.png)

#### Failed to join the server error message:
![image](https://user-images.githubusercontent.com/39607018/212557498-bb2b9fbf-6161-4079-991f-4c024fc4544d.png)

#### /smp command messages during server start:
![image](https://user-images.githubusercontent.com/39607018/212555689-79f75242-98d2-4419-9e37-2075022e99e1.png)

#### /opme command:
![image](https://user-images.githubusercontent.com/39607018/212555669-67785bca-a0c4-4608-a643-7ef1bcb0a4c1.png)

#### Invite system:
![image](https://user-images.githubusercontent.com/39607018/212555603-27d9613f-a2a1-4b56-80bb-3dfc25a27617.png)
![image](https://user-images.githubusercontent.com/39607018/212555638-875eac94-d87d-44e3-920a-d49a952068b3.png)

#### Maintenance mode (MOTD):
![image](https://user-images.githubusercontent.com/39607018/212554680-6809a144-f80f-44fe-945b-2e812739bde7.png)

#### Maintenance whitelist system messages:
![image](https://user-images.githubusercontent.com/39607018/212554800-6f2a960c-7e0c-4756-b9d9-b3cd29291964.png)

#### Maintenance mode kicked from the server messages:
![image](https://user-images.githubusercontent.com/39607018/212554871-77496173-a200-4029-8b5e-b083db1ebfed.png)
![image](https://user-images.githubusercontent.com/39607018/212554850-28ee5ab6-140b-446b-9cac-8273f97bad11.png)
![image](https://user-images.githubusercontent.com/39607018/212554825-4fe4b33f-653f-4e60-8107-5fa5ad53796e.png)

### Building
Clone the repository
<br>
`git clone https://github.com/mebsic/PlayerServer.git`
<br><br>
Run BuildTools
<br>
https://www.spigotmc.org/wiki/buildtools
<br><br>
Compile using `mvn clean package`
<br><br>
Requires Java 8 or later and the latest version of Minecraft
