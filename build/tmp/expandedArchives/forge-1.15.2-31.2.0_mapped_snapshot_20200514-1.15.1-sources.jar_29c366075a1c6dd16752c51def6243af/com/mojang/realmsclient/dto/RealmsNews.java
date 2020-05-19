package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsNews extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String newsLink;

   public static RealmsNews parse(String p_parse_0_) {
      RealmsNews realmsnews = new RealmsNews();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
         realmsnews.newsLink = JsonUtils.func_225171_a("newsLink", jsonobject, (String)null);
      } catch (Exception exception) {
         LOGGER.error("Could not parse RealmsNews: " + exception.getMessage());
      }

      return realmsnews;
   }
}