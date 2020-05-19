package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class OpList extends UserList<GameProfile, OpEntry> {
   public OpList(File saveFile) {
      super(saveFile);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new OpEntry(entryData);
   }

   public String[] getKeys() {
      String[] astring = new String[this.getEntries().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.getEntries()) {
         astring[i++] = userlistentry.getValue().getName();
      }

      return astring;
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      OpEntry opentry = this.getEntry(profile);
      return opentry != null ? opentry.bypassesPlayerLimit() : false;
   }

   /**
    * Gets the key value for the given object
    */
   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }
}