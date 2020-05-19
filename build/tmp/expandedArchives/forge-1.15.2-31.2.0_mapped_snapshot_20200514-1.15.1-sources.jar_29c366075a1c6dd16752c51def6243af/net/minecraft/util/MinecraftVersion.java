package net.minecraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion implements GameVersion {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String id;
   private final String name;
   private final boolean stable;
   private final int worldVersion;
   private final int protocolVersion;
   private final int packVersion;
   private final Date buildTime;
   private final String releaseTarget;

   public MinecraftVersion() {
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.15.2";
      this.stable = true;
      this.worldVersion = 2230;
      this.protocolVersion = 578;
      this.packVersion = 5;
      this.buildTime = new Date();
      this.releaseTarget = "1.15.2";
   }

   protected MinecraftVersion(JsonObject p_i51407_1_) {
      this.id = JSONUtils.getString(p_i51407_1_, "id");
      this.name = JSONUtils.getString(p_i51407_1_, "name");
      this.releaseTarget = JSONUtils.getString(p_i51407_1_, "release_target");
      this.stable = JSONUtils.getBoolean(p_i51407_1_, "stable");
      this.worldVersion = JSONUtils.getInt(p_i51407_1_, "world_version");
      this.protocolVersion = JSONUtils.getInt(p_i51407_1_, "protocol_version");
      this.packVersion = JSONUtils.getInt(p_i51407_1_, "pack_version");
      this.buildTime = Date.from(ZonedDateTime.parse(JSONUtils.getString(p_i51407_1_, "build_time")).toInstant());
   }

   /**
    * Creates a new instance containing game version data from version.json (or fallback data if necessary).
    *  
    * For getting data, use {@link SharedConstants#getVersion} instead, as that is cached.
    */
   public static GameVersion load() {
      try (InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json")) {
         if (inputstream == null) {
            LOGGER.warn("Missing version information!");
            MinecraftVersion minecraftversion1 = new MinecraftVersion();
            return minecraftversion1;
         } else {
            MinecraftVersion minecraftversion;
            try (InputStreamReader inputstreamreader = new InputStreamReader(inputstream)) {
               minecraftversion = new MinecraftVersion(JSONUtils.fromJson(inputstreamreader));
            }

            return minecraftversion;
         }
      } catch (JsonParseException | IOException ioexception) {
         throw new IllegalStateException("Game version information is corrupt", ioexception);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getReleaseTarget() {
      return this.releaseTarget;
   }

   public int getWorldVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion() {
      return this.packVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}