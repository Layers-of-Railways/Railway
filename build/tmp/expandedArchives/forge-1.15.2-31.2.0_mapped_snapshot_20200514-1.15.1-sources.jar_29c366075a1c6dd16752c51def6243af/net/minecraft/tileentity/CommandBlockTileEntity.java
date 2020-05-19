package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CommandBlockTileEntity extends TileEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private boolean sendToClient;
   private final CommandBlockLogic commandBlockLogic = new CommandBlockLogic() {
      /**
       * Sets the command.
       */
      public void setCommand(String command) {
         super.setCommand(command);
         CommandBlockTileEntity.this.markDirty();
      }

      public ServerWorld getWorld() {
         return (ServerWorld)CommandBlockTileEntity.this.world;
      }

      public void updateCommand() {
         BlockState blockstate = CommandBlockTileEntity.this.world.getBlockState(CommandBlockTileEntity.this.pos);
         this.getWorld().notifyBlockUpdate(CommandBlockTileEntity.this.pos, blockstate, blockstate, 3);
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPositionVector() {
         return new Vec3d((double)CommandBlockTileEntity.this.pos.getX() + 0.5D, (double)CommandBlockTileEntity.this.pos.getY() + 0.5D, (double)CommandBlockTileEntity.this.pos.getZ() + 0.5D);
      }

      public CommandSource getCommandSource() {
         return new CommandSource(this, new Vec3d((double)CommandBlockTileEntity.this.pos.getX() + 0.5D, (double)CommandBlockTileEntity.this.pos.getY() + 0.5D, (double)CommandBlockTileEntity.this.pos.getZ() + 0.5D), Vec2f.ZERO, this.getWorld(), 2, this.getName().getString(), this.getName(), this.getWorld().getServer(), (Entity)null);
      }
   };

   public CommandBlockTileEntity() {
      super(TileEntityType.COMMAND_BLOCK);
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      this.commandBlockLogic.write(compound);
      compound.putBoolean("powered", this.isPowered());
      compound.putBoolean("conditionMet", this.isConditionMet());
      compound.putBoolean("auto", this.isAuto());
      return compound;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.commandBlockLogic.read(compound);
      this.powered = compound.getBoolean("powered");
      this.conditionMet = compound.getBoolean("conditionMet");
      this.setAuto(compound.getBoolean("auto"));
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      if (this.isSendToClient()) {
         this.setSendToClient(false);
         CompoundNBT compoundnbt = this.write(new CompoundNBT());
         return new SUpdateTileEntityPacket(this.pos, 2, compoundnbt);
      } else {
         return null;
      }
   }

   /**
    * Checks if players can use this tile entity to access operator (permission level 2) commands either directly or
    * indirectly, such as give or setblock. A similar method exists for entities at {@link
    * net.minecraft.entity.Entity#ignoreItemEntityData()}.<p>For example, {@link
    * net.minecraft.tileentity.TileEntitySign#onlyOpsCanSetNbt() signs} (player right-clicking) and {@link
    * net.minecraft.tileentity.TileEntityCommandBlock#onlyOpsCanSetNbt() command blocks} are considered
    * accessible.</p>@return true if this block entity offers ways for unauthorized players to use restricted commands
    */
   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public CommandBlockLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public void setPowered(boolean poweredIn) {
      this.powered = poweredIn;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAuto() {
      return this.auto;
   }

   public void setAuto(boolean autoIn) {
      boolean flag = this.auto;
      this.auto = autoIn;
      if (!flag && autoIn && !this.powered && this.world != null && this.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
         this.func_226988_y_();
      }

   }

   public void func_226987_h_() {
      CommandBlockTileEntity.Mode commandblocktileentity$mode = this.getMode();
      if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.AUTO && (this.powered || this.auto) && this.world != null) {
         this.func_226988_y_();
      }

   }

   private void func_226988_y_() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof CommandBlockBlock) {
         this.setConditionMet();
         this.world.getPendingBlockTicks().scheduleTick(this.pos, block, block.tickRate(this.world));
      }

   }

   public boolean isConditionMet() {
      return this.conditionMet;
   }

   public boolean setConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos blockpos = this.pos.offset(this.world.getBlockState(this.pos).get(CommandBlockBlock.FACING).getOpposite());
         if (this.world.getBlockState(blockpos).getBlock() instanceof CommandBlockBlock) {
            TileEntity tileentity = this.world.getTileEntity(blockpos);
            this.conditionMet = tileentity instanceof CommandBlockTileEntity && ((CommandBlockTileEntity)tileentity).getCommandBlockLogic().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public boolean isSendToClient() {
      return this.sendToClient;
   }

   public void setSendToClient(boolean p_184252_1_) {
      this.sendToClient = p_184252_1_;
   }

   public CommandBlockTileEntity.Mode getMode() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.COMMAND_BLOCK) {
         return CommandBlockTileEntity.Mode.REDSTONE;
      } else if (block == Blocks.REPEATING_COMMAND_BLOCK) {
         return CommandBlockTileEntity.Mode.AUTO;
      } else {
         return block == Blocks.CHAIN_COMMAND_BLOCK ? CommandBlockTileEntity.Mode.SEQUENCE : CommandBlockTileEntity.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      BlockState blockstate = this.world.getBlockState(this.getPos());
      return blockstate.getBlock() instanceof CommandBlockBlock ? blockstate.get(CommandBlockBlock.CONDITIONAL) : false;
   }

   /**
    * validates a tile entity
    */
   public void validate() {
      this.updateContainingBlockInfo();
      super.validate();
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;
   }
}