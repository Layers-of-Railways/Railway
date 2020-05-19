package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PendingInvite extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String invitationId;
   public String worldName;
   public String worldOwnerName;
   public String worldOwnerUuid;
   public Date date;

   public static PendingInvite parse(JsonObject p_parse_0_) {
      PendingInvite pendinginvite = new PendingInvite();

      try {
         pendinginvite.invitationId = JsonUtils.func_225171_a("invitationId", p_parse_0_, "");
         pendinginvite.worldName = JsonUtils.func_225171_a("worldName", p_parse_0_, "");
         pendinginvite.worldOwnerName = JsonUtils.func_225171_a("worldOwnerName", p_parse_0_, "");
         pendinginvite.worldOwnerUuid = JsonUtils.func_225171_a("worldOwnerUuid", p_parse_0_, "");
         pendinginvite.date = JsonUtils.func_225173_a("date", p_parse_0_);
      } catch (Exception exception) {
         LOGGER.error("Could not parse PendingInvite: " + exception.getMessage());
      }

      return pendinginvite;
   }
}