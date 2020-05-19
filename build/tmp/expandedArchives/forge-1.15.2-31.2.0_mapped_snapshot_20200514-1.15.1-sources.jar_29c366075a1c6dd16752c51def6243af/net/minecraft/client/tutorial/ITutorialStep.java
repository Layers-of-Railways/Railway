package net.minecraft.client.tutorial;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITutorialStep {
   default void onStop() {
   }

   default void tick() {
   }

   /**
    * Handles the player movement
    */
   default void handleMovement(MovementInput input) {
   }

   default void onMouseMove(double velocityX, double velocityY) {
   }

   /**
    * Handles blocks and entities hovering
    */
   default void onMouseHover(ClientWorld worldIn, RayTraceResult result) {
   }

   /**
    * Called when a player hits block to destroy it.
    */
   default void onHitBlock(ClientWorld worldIn, BlockPos pos, BlockState state, float diggingStage) {
   }

   /**
    * Called when the player opens his inventory
    */
   default void openInventory() {
   }

   /**
    * Called when the player pick up an ItemStack
    */
   default void handleSetSlot(ItemStack stack) {
   }
}