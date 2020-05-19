package net.minecraft.client.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int version = SharedConstants.getVersion().getProtocolVersion();
   public String gameVersion = SharedConstants.getVersion().getName();
   public boolean pinged;
   public String playerList;
   private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
   private String serverIcon;
   /** True if the server is a LAN server */
   private boolean lanServer;
   public net.minecraftforge.fml.client.ExtendedServerListData forgeData = null;

   public ServerData(String name, String ip, boolean isLan) {
      this.serverName = name;
      this.serverIP = ip;
      this.lanServer = isLan;
   }

   /**
    * Returns an NBTTagCompound with the server's name, IP and maybe acceptTextures.
    */
   public CompoundNBT getNBTCompound() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("name", this.serverName);
      compoundnbt.putString("ip", this.serverIP);
      if (this.serverIcon != null) {
         compoundnbt.putString("icon", this.serverIcon);
      }

      if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
         compoundnbt.putBoolean("acceptTextures", true);
      } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
         compoundnbt.putBoolean("acceptTextures", false);
      }

      return compoundnbt;
   }

   public ServerData.ServerResourceMode getResourceMode() {
      return this.resourceMode;
   }

   public void setResourceMode(ServerData.ServerResourceMode mode) {
      this.resourceMode = mode;
   }

   /**
    * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData instance.
    */
   public static ServerData getServerDataFromNBTCompound(CompoundNBT nbtCompound) {
      ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);
      if (nbtCompound.contains("icon", 8)) {
         serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
      }

      if (nbtCompound.contains("acceptTextures", 1)) {
         if (nbtCompound.getBoolean("acceptTextures")) {
            serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
         } else {
            serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
      }

      return serverdata;
   }

   /**
    * Returns the base-64 encoded representation of the server's icon, or null if not available
    */
   @Nullable
   public String getBase64EncodedIconData() {
      return this.serverIcon;
   }

   public void setBase64EncodedIconData(@Nullable String icon) {
      this.serverIcon = icon;
   }

   /**
    * Return true if the server is a LAN server
    */
   public boolean isOnLAN() {
      return this.lanServer;
   }

   public void copyFrom(ServerData serverDataIn) {
      this.serverIP = serverDataIn.serverIP;
      this.serverName = serverDataIn.serverName;
      this.setResourceMode(serverDataIn.getResourceMode());
      this.serverIcon = serverDataIn.serverIcon;
      this.lanServer = serverDataIn.lanServer;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final ITextComponent motd;

      private ServerResourceMode(String name) {
         this.motd = new TranslationTextComponent("addServer.resourcePack." + name);
      }

      public ITextComponent getMotd() {
         return this.motd;
      }
   }
}