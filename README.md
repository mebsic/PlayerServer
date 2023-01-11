## PlayerServer
An open-source spigot plugin that can automatically deploy and manage a private Survival Multiplayer (SMP) server in Minecraft
<br><br>
:warning: <b>Warning:</b> This plugin requires a BungeeCord network and MySQL database hosted on a VPS or dedicated server running Linux

### Features
#### Spigot
<ul>
  <li>
    MySQL database support
  </li>
  <li>
    Create an SMP from a template folder
  </li>
  <li>
    Start the server
  </li>
  <li>
    Delete the server (work in progress)
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
    Automatic updates for private servers (coming soon)
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
    /maintenance: Enable and disable maintenance mode
  </li>
  <li>
    /whitelist: Add or remove a player in the maintenance whitelist
  </li>
</ul>

### Screenshots
Coming soon

### Building
Clone the repository
<br>
`git clone https://github.com/mebsic/PlayerServer.git`
<br><br>
Run BuildTools
<br>
<a href="https://www.spigotmc.org/wiki/buildtools" target="_blank">
  https://www.spigotmc.org/wiki/buildtools
</a>
<br><br>
Compile using `mvn clean package`
<br><br>
Requires Java 8 or later and the latest version of Minecraft
