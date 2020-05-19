package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UploadInfo extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   @Expose
   private boolean worldClosed;
   @Expose
   private String token = "";
   @Expose
   private String uploadEndpoint = "";
   private int port;

   public static UploadInfo parse(String p_parse_0_) {
      UploadInfo uploadinfo = new UploadInfo();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
         uploadinfo.worldClosed = JsonUtils.func_225170_a("worldClosed", jsonobject, false);
         uploadinfo.token = JsonUtils.func_225171_a("token", jsonobject, (String)null);
         uploadinfo.uploadEndpoint = JsonUtils.func_225171_a("uploadEndpoint", jsonobject, (String)null);
         uploadinfo.port = JsonUtils.func_225172_a("port", jsonobject, 8080);
      } catch (Exception exception) {
         LOGGER.error("Could not parse UploadInfo: " + exception.getMessage());
      }

      return uploadinfo;
   }

   public String getToken() {
      return this.token;
   }

   public String getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }

   public void setToken(String p_setToken_1_) {
      this.token = p_setToken_1_;
   }

   public int getPort() {
      return this.port;
   }
}