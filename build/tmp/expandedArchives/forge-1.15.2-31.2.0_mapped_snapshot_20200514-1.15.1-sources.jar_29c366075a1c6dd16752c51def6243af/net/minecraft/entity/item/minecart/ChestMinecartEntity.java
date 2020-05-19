package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ChestMinecartEntity extends ContainerMinecartEntity {
   public ChestMinecartEntity(EntityType<? extends ChestMinecartEntity> p_i50124_1_, World p_i50124_2_) {
      super(p_i50124_1_, p_i50124_2_);
   }

   public ChestMinecartEntity(World worldIn, double x, double y, double z) {
      super(EntityType.CHEST_MINECART, x, y, z, worldIn);
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.entityDropItem(Blocks.CHEST);
      }

   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return 27;
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.CHEST;
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH);
   }

   public int getDefaultDisplayTileOffset() {
      return 8;
   }

   public Container createContainer(int id, PlayerInventory playerInventoryIn) {
      return ChestContainer.createGeneric9X3(id, playerInventoryIn, this);
   }
}