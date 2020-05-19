package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.FolderResourceIndex;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation userInfo;
   public final ScreenSize displayInfo;
   public final GameConfiguration.FolderInformation folderInfo;
   public final GameConfiguration.GameInformation gameInfo;
   public final GameConfiguration.ServerInformation serverInfo;

   public GameConfiguration(GameConfiguration.UserInformation p_i51071_1_, ScreenSize p_i51071_2_, GameConfiguration.FolderInformation p_i51071_3_, GameConfiguration.GameInformation p_i51071_4_, GameConfiguration.ServerInformation p_i51071_5_) {
      this.userInfo = p_i51071_1_;
      this.displayInfo = p_i51071_2_;
      this.folderInfo = p_i51071_3_;
      this.gameInfo = p_i51071_4_;
      this.serverInfo = p_i51071_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      public final File gameDir;
      public final File resourcePacksDir;
      public final File assetsDir;
      @Nullable
      public final String assetIndex;

      public FolderInformation(File mcDataDirIn, File resourcePacksDirIn, File assetsDirIn, @Nullable String assetIndexIn) {
         this.gameDir = mcDataDirIn;
         this.resourcePacksDir = resourcePacksDirIn;
         this.assetsDir = assetsDirIn;
         this.assetIndex = assetIndexIn;
      }

      public ResourceIndex getAssetsIndex() {
         return (ResourceIndex)(this.assetIndex == null ? new FolderResourceIndex(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean isDemo;
      public final String version;
      public final String versionType;

      public GameInformation(boolean demo, String versionIn, String versionTypeIn) {
         this.isDemo = demo;
         this.version = versionIn;
         this.versionType = versionTypeIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      @Nullable
      public final String serverName;
      public final int serverPort;

      public ServerInformation(@Nullable String serverNameIn, int serverPortIn) {
         this.serverName = serverNameIn;
         this.serverPort = serverPortIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session sessionIn, PropertyMap userPropertiesIn, PropertyMap profilePropertiesIn, Proxy proxyIn) {
         this.session = sessionIn;
         this.userProperties = userPropertiesIn;
         this.profileProperties = profilePropertiesIn;
         this.proxy = proxyIn;
      }
   }
}