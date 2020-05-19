package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class WoodButtonBlock extends AbstractButtonBlock {
   protected WoodButtonBlock(Block.Properties properties) {
      super(true, properties);
   }

   protected SoundEvent getSoundEvent(boolean p_196369_1_) {
      return p_196369_1_ ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
   }
}