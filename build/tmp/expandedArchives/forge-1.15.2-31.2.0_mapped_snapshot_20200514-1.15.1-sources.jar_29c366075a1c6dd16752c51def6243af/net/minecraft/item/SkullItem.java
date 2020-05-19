package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallOrFloorItem {
   public SkullItem(Block floorBlockIn, Block wallBlockIn, Item.Properties builder) {
      super(floorBlockIn, wallBlockIn, builder);
   }

   public ITextComponent getDisplayName(ItemStack stack) {
      if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
         String s = null;
         CompoundNBT compoundnbt = stack.getTag();
         if (compoundnbt.contains("SkullOwner", 8)) {
            s = compoundnbt.getString("SkullOwner");
         } else if (compoundnbt.contains("SkullOwner", 10)) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("SkullOwner");
            if (compoundnbt1.contains("Name", 8)) {
               s = compoundnbt1.getString("Name");
            }
         }

         if (s != null) {
            return new TranslationTextComponent(this.getTranslationKey() + ".named", s);
         }
      }

      return super.getDisplayName(stack);
   }

   /**
    * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
    */
   public boolean updateItemStackNBT(CompoundNBT nbt) {
      super.updateItemStackNBT(nbt);
      if (nbt.contains("SkullOwner", 8) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
         GameProfile gameprofile = new GameProfile((UUID)null, nbt.getString("SkullOwner"));
         gameprofile = SkullTileEntity.updateGameProfile(gameprofile);
         nbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
         return true;
      } else {
         return false;
      }
   }
}