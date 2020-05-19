package net.minecraft.client.tutorial;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CraftPlanksStep implements ITutorialStep {
   private static final ITextComponent TITLE = new TranslationTextComponent("tutorial.craft_planks.title");
   private static final ITextComponent DESCRIPTION = new TranslationTextComponent("tutorial.craft_planks.description");
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public CraftPlanksStep(Tutorial tutorial) {
      this.tutorial = tutorial;
   }

   public void tick() {
      ++this.timeWaiting;
      if (this.tutorial.getGameType() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            ClientPlayerEntity clientplayerentity = this.tutorial.getMinecraft().player;
            if (clientplayerentity != null) {
               if (clientplayerentity.inventory.hasTag(ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }

               if (hasCrafted(clientplayerentity, ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }
            }
         }

         if (this.timeWaiting >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.WOODEN_PLANKS, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToastGui().add(this.toast);
         }

      }
   }

   public void onStop() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   /**
    * Called when the player pick up an ItemStack
    */
   public void handleSetSlot(ItemStack stack) {
      Item item = stack.getItem();
      if (ItemTags.PLANKS.contains(item)) {
         this.tutorial.setStep(TutorialSteps.NONE);
      }

   }

   public static boolean hasCrafted(ClientPlayerEntity player, Tag<Item> itemsIn) {
      for(Item item : itemsIn.getAllElements()) {
         if (player.getStats().getValue(Stats.ITEM_CRAFTED.get(item)) > 0) {
            return true;
         }
      }

      return false;
   }
}