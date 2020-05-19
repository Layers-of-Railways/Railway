package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class BanList extends UserList<GameProfile, ProfileBanEntry> {
   public BanList(File bansFile) {
      super(bansFile);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new ProfileBanEntry(entryData);
   }

   public boolean isBanned(GameProfile profile) {
      return this.hasEntry(profile);
   }

   public String[] getKeys() {
      String[] astring = new String[this.getEntries().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.getEntries()) {
         astring[i++] = userlistentry.getValue().getName();
      }

      return astring;
   }

   /**
    * Gets the key value for the given object
    */
   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }
}