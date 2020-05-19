package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SkullPlayerBlock extends SkullBlock {
   protected SkullPlayerBlock(Block.Properties properties) {
      super(SkullBlock.Types.PLAYER, properties);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof SkullTileEntity) {
         SkullTileEntity skulltileentity = (SkullTileEntity)tileentity;
         GameProfile gameprofile = null;
         if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            if (compoundnbt.contains("SkullOwner", 10)) {
               gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
            } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundnbt.getString("SkullOwner"))) {
               gameprofile = new GameProfile((UUID)null, compoundnbt.getString("SkullOwner"));
            }
         }

         skulltileentity.setPlayerProfile(gameprofile);
      }

   }
}