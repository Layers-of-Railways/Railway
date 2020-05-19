package net.minecraft.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.Template;

public class StructureUpdater implements SNBTToNBTConverter.ITransformer {
   public CompoundNBT func_225371_a(String p_225371_1_, CompoundNBT p_225371_2_) {
      return p_225371_1_.startsWith("data/minecraft/structures/") ? func_225373_b(func_225372_a(p_225371_2_)) : p_225371_2_;
   }

   private static CompoundNBT func_225372_a(CompoundNBT p_225372_0_) {
      if (!p_225372_0_.contains("DataVersion", 99)) {
         p_225372_0_.putInt("DataVersion", 500);
      }

      return p_225372_0_;
   }

   private static CompoundNBT func_225373_b(CompoundNBT p_225373_0_) {
      Template template = new Template();
      template.read(NBTUtil.update(DataFixesManager.getDataFixer(), DefaultTypeReferences.STRUCTURE, p_225373_0_, p_225373_0_.getInt("DataVersion")));
      return template.writeToNBT(new CompoundNBT());
   }
}