package com.mojang.realmsclient.util;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class RealmsPersistence {
   public static RealmsPersistence.RealmsPersistenceData func_225188_a() {
      File file1 = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
      Gson gson = new Gson();

      try {
         return gson.fromJson(FileUtils.readFileToString(file1), RealmsPersistence.RealmsPersistenceData.class);
      } catch (IOException var3) {
         return new RealmsPersistence.RealmsPersistenceData();
      }
   }

   public static void func_225187_a(RealmsPersistence.RealmsPersistenceData p_225187_0_) {
      File file1 = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
      Gson gson = new Gson();
      String s = gson.toJson(p_225187_0_);

      try {
         FileUtils.writeStringToFile(file1, s);
      } catch (IOException var5) {
         ;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsPersistenceData {
      public String field_225185_a;
      public boolean field_225186_b;

      private RealmsPersistenceData() {
      }
   }
}