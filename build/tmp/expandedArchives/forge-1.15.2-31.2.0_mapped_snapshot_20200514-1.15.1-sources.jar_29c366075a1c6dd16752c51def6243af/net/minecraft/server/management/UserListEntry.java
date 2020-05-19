package net.minecraft.server.management;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class UserListEntry<T> {
   @Nullable
   private final T value;

   public UserListEntry(T valueIn) {
      this.value = valueIn;
   }

   protected UserListEntry(@Nullable T valueIn, JsonObject json) {
      this.value = valueIn;
   }

   @Nullable
   T getValue() {
      return this.value;
   }

   boolean hasBanExpired() {
      return false;
   }

   protected void onSerialization(JsonObject data) {
   }
}