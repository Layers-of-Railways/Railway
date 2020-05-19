package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerActivity extends ValueObject {
   public String profileUuid;
   public long joinTime;
   public long leaveTime;

   public static ServerActivity parse(JsonObject p_parse_0_) {
      ServerActivity serveractivity = new ServerActivity();

      try {
         serveractivity.profileUuid = JsonUtils.func_225171_a("profileUuid", p_parse_0_, (String)null);
         serveractivity.joinTime = JsonUtils.func_225169_a("joinTime", p_parse_0_, Long.MIN_VALUE);
         serveractivity.leaveTime = JsonUtils.func_225169_a("leaveTime", p_parse_0_, Long.MIN_VALUE);
      } catch (Exception var3) {
         ;
      }

      return serveractivity;
   }
}