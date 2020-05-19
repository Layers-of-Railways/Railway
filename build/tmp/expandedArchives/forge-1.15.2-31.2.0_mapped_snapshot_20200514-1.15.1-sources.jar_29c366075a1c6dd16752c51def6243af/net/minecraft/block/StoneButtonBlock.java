package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class StoneButtonBlock extends AbstractButtonBlock {
   protected StoneButtonBlock(Block.Properties properties) {
      super(false, properties);
   }

   protected SoundEvent getSoundEvent(boolean p_196369_1_) {
      return p_196369_1_ ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
   }
}