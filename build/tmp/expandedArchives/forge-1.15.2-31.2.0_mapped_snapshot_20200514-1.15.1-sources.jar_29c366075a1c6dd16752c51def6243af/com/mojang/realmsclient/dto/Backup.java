package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Backup extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String backupId;
   public Date lastModifiedDate;
   public long size;
   private boolean uploadedVersion;
   public Map<String, String> metadata = Maps.newHashMap();
   public Map<String, String> changeList = Maps.newHashMap();

   public static Backup parse(JsonElement p_parse_0_) {
      JsonObject jsonobject = p_parse_0_.getAsJsonObject();
      Backup backup = new Backup();

      try {
         backup.backupId = JsonUtils.func_225171_a("backupId", jsonobject, "");
         backup.lastModifiedDate = JsonUtils.func_225173_a("lastModifiedDate", jsonobject);
         backup.size = JsonUtils.func_225169_a("size", jsonobject, 0L);
         if (jsonobject.has("metadata")) {
            JsonObject jsonobject1 = jsonobject.getAsJsonObject("metadata");

            for(Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
               if (!entry.getValue().isJsonNull()) {
                  backup.metadata.put(format(entry.getKey()), entry.getValue().getAsString());
               }
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Could not parse Backup: " + exception.getMessage());
      }

      return backup;
   }

   private static String format(String p_format_0_) {
      String[] astring = p_format_0_.split("_");
      StringBuilder stringbuilder = new StringBuilder();

      for(String s : astring) {
         if (s != null && s.length() >= 1) {
            if ("of".equals(s)) {
               stringbuilder.append(s).append(" ");
            } else {
               char c0 = Character.toUpperCase(s.charAt(0));
               stringbuilder.append(c0).append(s.substring(1, s.length())).append(" ");
            }
         }
      }

      return stringbuilder.toString();
   }

   public boolean isUploadedVersion() {
      return this.uploadedVersion;
   }

   public void setUploadedVersion(boolean p_setUploadedVersion_1_) {
      this.uploadedVersion = p_setUploadedVersion_1_;
   }
}