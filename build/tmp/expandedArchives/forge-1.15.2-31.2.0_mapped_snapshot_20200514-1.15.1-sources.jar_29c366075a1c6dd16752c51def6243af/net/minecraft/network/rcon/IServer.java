package net.minecraft.network.rcon;

import net.minecraft.server.dedicated.ServerProperties;

public interface IServer {
   ServerProperties getServerProperties();

   /**
    * Returns the server's hostname.
    */
   String getHostname();

   /**
    * Never used, but "getServerPort" is already taken.
    */
   int getPort();

   /**
    * Returns the server message of the day
    */
   String getMotd();

   /**
    * Returns the server's Minecraft version as string.
    */
   String getMinecraftVersion();

   /**
    * Returns the number of players currently on the server.
    */
   int getCurrentPlayerCount();

   /**
    * Returns the maximum number of players allowed on the server.
    */
   int getMaxPlayers();

   /**
    * Returns an array of the usernames of all the connected players.
    */
   String[] getOnlinePlayerNames();

   String getFolderName();

   /**
    * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
    */
   String getPlugins();

   /**
    * Handle a command received by an RCon instance
    */
   String handleRConCommand(String command);

   /**
    * Returns true if debugging is enabled, false otherwise.
    */
   boolean isDebuggingEnabled();

   /**
    * Logs the message with a level of INFO.
    */
   void logInfo(String msg);

   /**
    * Logs the message with a level of WARN.
    */
   void logWarning(String msg);

   /**
    * Logs the error message with a level of SEVERE.
    */
   void logSevere(String msg);

   /**
    * If isDebuggingEnabled(), logs the message with a level of INFO.
    */
   void logDebug(String msg);
}