package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerAddress extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String address;
   public String resourcePackUrl;
   public String resourcePackHash;

   public static RealmsServerAddress parse(String p_parse_0_) {
      JsonParser jsonparser = new JsonParser();
      RealmsServerAddress realmsserveraddress = new RealmsServerAddress();

      try {
         JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
         realmsserveraddress.address = JsonUtils.func_225171_a("address", jsonobject, (String)null);
         realmsserveraddress.resourcePackUrl = JsonUtils.func_225171_a("resourcePackUrl", jsonobject, (String)null);
         realmsserveraddress.resourcePackHash = JsonUtils.func_225171_a("resourcePackHash", jsonobject, (String)null);
      } catch (Exception exception) {
         LOGGER.error("Could not parse RealmsServerAddress: " + exception.getMessage());
      }

      return realmsserveraddress;
   }
}