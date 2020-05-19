package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProgressUpdate {
   void displaySavingString(ITextComponent component);

   @OnlyIn(Dist.CLIENT)
   void resetProgressAndMessage(ITextComponent component);

   void displayLoadingString(ITextComponent component);

   /**
    * Updates the progress bar on the loading screen to the specified amount.
    */
   void setLoadingProgress(int progress);

   @OnlyIn(Dist.CLIENT)
   void setDoneWorking();
}