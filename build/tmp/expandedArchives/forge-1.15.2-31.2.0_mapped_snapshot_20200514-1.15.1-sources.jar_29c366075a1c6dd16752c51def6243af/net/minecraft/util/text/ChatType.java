package net.minecraft.util.text;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ChatType {
   CHAT((byte)0, false),
   SYSTEM((byte)1, true),
   GAME_INFO((byte)2, true);

   private final byte id;
   private final boolean field_218691_e;

   private ChatType(byte p_i50783_3_, boolean p_i50783_4_) {
      this.id = p_i50783_3_;
      this.field_218691_e = p_i50783_4_;
   }

   public byte getId() {
      return this.id;
   }

   public static ChatType byId(byte idIn) {
      for(ChatType chattype : values()) {
         if (idIn == chattype.id) {
            return chattype;
         }
      }

      return CHAT;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getInterrupts() {
      return this.field_218691_e;
   }
}