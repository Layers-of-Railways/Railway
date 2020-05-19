package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Subscription extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long startDate;
   public int daysLeft;
   public Subscription.Type type = Subscription.Type.NORMAL;

   public static Subscription parse(String p_parse_0_) {
      Subscription subscription = new Subscription();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
         subscription.startDate = JsonUtils.func_225169_a("startDate", jsonobject, 0L);
         subscription.daysLeft = JsonUtils.func_225172_a("daysLeft", jsonobject, 0);
         subscription.type = typeFrom(JsonUtils.func_225171_a("subscriptionType", jsonobject, Subscription.Type.NORMAL.name()));
      } catch (Exception exception) {
         LOGGER.error("Could not parse Subscription: " + exception.getMessage());
      }

      return subscription;
   }

   private static Subscription.Type typeFrom(String p_typeFrom_0_) {
      try {
         return Subscription.Type.valueOf(p_typeFrom_0_);
      } catch (Exception var2) {
         return Subscription.Type.NORMAL;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      NORMAL,
      RECURRING;
   }
}