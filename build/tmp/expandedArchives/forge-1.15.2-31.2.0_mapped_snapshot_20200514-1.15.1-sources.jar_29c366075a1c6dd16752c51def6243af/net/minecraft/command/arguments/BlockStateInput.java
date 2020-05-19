package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockStateInput implements Predicate<CachedBlockInfo> {
   private final BlockState state;
   private final Set<IProperty<?>> properties;
   @Nullable
   private final CompoundNBT tag;

   public BlockStateInput(BlockState stateIn, Set<IProperty<?>> propertiesIn, @Nullable CompoundNBT nbtIn) {
      this.state = stateIn;
      this.properties = propertiesIn;
      this.tag = nbtIn;
   }

   public BlockState getState() {
      return this.state;
   }

   public boolean test(CachedBlockInfo p_test_1_) {
      BlockState blockstate = p_test_1_.getBlockState();
      if (blockstate.getBlock() != this.state.getBlock()) {
         return false;
      } else {
         for(IProperty<?> iproperty : this.properties) {
            if (blockstate.get(iproperty) != this.state.get(iproperty)) {
               return false;
            }
         }

         if (this.tag == null) {
            return true;
         } else {
            TileEntity tileentity = p_test_1_.getTileEntity();
            return tileentity != null && NBTUtil.areNBTEquals(this.tag, tileentity.write(new CompoundNBT()), true);
         }
      }
   }

   public boolean place(ServerWorld worldIn, BlockPos pos, int flags) {
      if (!worldIn.setBlockState(pos, this.state, flags)) {
         return false;
      } else {
         if (this.tag != null) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity != null) {
               CompoundNBT compoundnbt = this.tag.copy();
               compoundnbt.putInt("x", pos.getX());
               compoundnbt.putInt("y", pos.getY());
               compoundnbt.putInt("z", pos.getZ());
               tileentity.read(compoundnbt);
            }
         }

         return true;
      }
   }
}