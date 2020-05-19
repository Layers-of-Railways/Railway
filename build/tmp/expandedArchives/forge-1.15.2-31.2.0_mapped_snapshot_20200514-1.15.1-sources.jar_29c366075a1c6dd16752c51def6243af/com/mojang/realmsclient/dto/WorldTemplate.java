package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldTemplate extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String id;
   public String name;
   public String version;
   public String author;
   public String link;
   public String image;
   public String trailer;
   public String recommendedPlayers;
   public WorldTemplate.Type type;

   public static WorldTemplate parse(JsonObject p_parse_0_) {
      WorldTemplate worldtemplate = new WorldTemplate();

      try {
         worldtemplate.id = JsonUtils.func_225171_a("id", p_parse_0_, "");
         worldtemplate.name = JsonUtils.func_225171_a("name", p_parse_0_, "");
         worldtemplate.version = JsonUtils.func_225171_a("version", p_parse_0_, "");
         worldtemplate.author = JsonUtils.func_225171_a("author", p_parse_0_, "");
         worldtemplate.link = JsonUtils.func_225171_a("link", p_parse_0_, "");
         worldtemplate.image = JsonUtils.func_225171_a("image", p_parse_0_, (String)null);
         worldtemplate.trailer = JsonUtils.func_225171_a("trailer", p_parse_0_, "");
         worldtemplate.recommendedPlayers = JsonUtils.func_225171_a("recommendedPlayers", p_parse_0_, "");
         worldtemplate.type = WorldTemplate.Type.valueOf(JsonUtils.func_225171_a("type", p_parse_0_, WorldTemplate.Type.WORLD_TEMPLATE.name()));
      } catch (Exception exception) {
         LOGGER.error("Could not parse WorldTemplate: " + exception.getMessage());
      }

      return worldtemplate;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}