package net.minecraft.client.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatLine {
   private final int updateCounterCreated;
   private final ITextComponent lineString;
   private final int chatLineID;

   public ChatLine(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn) {
      this.lineString = lineStringIn;
      this.updateCounterCreated = updateCounterCreatedIn;
      this.chatLineID = chatLineIDIn;
   }

   public ITextComponent getChatComponent() {
      return this.lineString;
   }

   public int getUpdatedCounter() {
      return this.updateCounterCreated;
   }

   public int getChatLineID() {
      return this.chatLineID;
   }
}