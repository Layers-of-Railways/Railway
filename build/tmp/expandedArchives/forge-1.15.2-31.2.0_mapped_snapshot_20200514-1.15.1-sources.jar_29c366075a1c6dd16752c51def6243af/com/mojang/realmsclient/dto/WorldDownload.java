package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldDownload extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String downloadLink;
   public String resourcePackUrl;
   public String resourcePackHash;

   public static WorldDownload parse(String p_parse_0_) {
      JsonParser jsonparser = new JsonParser();
      JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
      WorldDownload worlddownload = new WorldDownload();

      try {
         worlddownload.downloadLink = JsonUtils.func_225171_a("downloadLink", jsonobject, "");
         worlddownload.resourcePackUrl = JsonUtils.func_225171_a("resourcePackUrl", jsonobject, "");
         worlddownload.resourcePackHash = JsonUtils.func_225171_a("resourcePackHash", jsonobject, "");
      } catch (Exception exception) {
         LOGGER.error("Could not parse WorldDownload: " + exception.getMessage());
      }

      return worlddownload;
   }
}