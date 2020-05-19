package net.minecraft.client.gui.widget.list;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AvailableResourcePackList extends AbstractResourcePackList {
   public AvailableResourcePackList(Minecraft p_i47649_1_, int p_i47649_2_, int p_i47649_3_) {
      super(p_i47649_1_, p_i47649_2_, p_i47649_3_, new TranslationTextComponent("resourcePack.available.title"));
   }
}