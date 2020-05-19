package net.minecraft.entity.player;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum PlayerModelPart {
   CAPE(0, "cape"),
   JACKET(1, "jacket"),
   LEFT_SLEEVE(2, "left_sleeve"),
   RIGHT_SLEEVE(3, "right_sleeve"),
   LEFT_PANTS_LEG(4, "left_pants_leg"),
   RIGHT_PANTS_LEG(5, "right_pants_leg"),
   HAT(6, "hat");

   private final int partId;
   private final int partMask;
   private final String partName;
   private final ITextComponent name;

   private PlayerModelPart(int partIdIn, String partNameIn) {
      this.partId = partIdIn;
      this.partMask = 1 << partIdIn;
      this.partName = partNameIn;
      this.name = new TranslationTextComponent("options.modelPart." + partNameIn);
   }

   public int getPartMask() {
      return this.partMask;
   }

   public String getPartName() {
      return this.partName;
   }

   public ITextComponent getName() {
      return this.name;
   }
}