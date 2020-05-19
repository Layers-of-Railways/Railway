package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<RealmsServer> servers;

   public static RealmsServerList parse(String p_parse_0_) {
      RealmsServerList realmsserverlist = new RealmsServerList();
      realmsserverlist.servers = Lists.newArrayList();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
         if (jsonobject.get("servers").isJsonArray()) {
            JsonArray jsonarray = jsonobject.get("servers").getAsJsonArray();
            Iterator<JsonElement> iterator = jsonarray.iterator();

            while(iterator.hasNext()) {
               realmsserverlist.servers.add(RealmsServer.parse(iterator.next().getAsJsonObject()));
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServerList: " + exception.getMessage());
      }

      return realmsserverlist;
   }
}