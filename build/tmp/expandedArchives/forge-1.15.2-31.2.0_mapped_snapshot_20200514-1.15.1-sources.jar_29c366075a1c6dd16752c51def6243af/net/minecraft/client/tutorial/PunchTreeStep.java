package net.minecraft.client.tutorial;

import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PunchTreeStep implements ITutorialStep {
   private static final ITextComponent TITLE = new TranslationTextComponent("tutorial.punch_tree.title");
   private static final ITextComponent DESCRIPTION = new TranslationTextComponent("tutorial.punch_tree.description", Tutorial.createKeybindComponent("attack"));
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;
   private int resetCount;

   public PunchTreeStep(Tutorial tutorial) {
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
               if (clientplayerentity.inventory.hasTag(ItemTags.LOGS)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }

               if (FindTreeStep.hasPunchedTreesPreviously(clientplayerentity)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
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
    * Called when a player hits block to destroy it.
    */
   public void onHitBlock(ClientWorld worldIn, BlockPos pos, BlockState state, float diggingStage) {
      boolean flag = state.isIn(BlockTags.LOGS);
      if (flag && diggingStage > 0.0F) {
         if (this.toast != null) {
            this.toast.setProgress(diggingStage);
         }

         if (diggingStage >= 1.0F) {
            this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
         }
      } else if (this.toast != null) {
         this.toast.setProgress(0.0F);
      } else if (flag) {
         ++this.resetCount;
      }

   }

   /**
    * Called when the player pick up an ItemStack
    */
   public void handleSetSlot(ItemStack stack) {
      if (ItemTags.LOGS.contains(stack.getItem())) {
         this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
      }
   }
}