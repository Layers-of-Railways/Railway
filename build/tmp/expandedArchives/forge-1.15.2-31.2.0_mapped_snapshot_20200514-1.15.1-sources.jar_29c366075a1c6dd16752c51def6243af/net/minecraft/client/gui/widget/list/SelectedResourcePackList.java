package net.minecraft.client.gui.widget.list;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SelectedResourcePackList extends AbstractResourcePackList {
   public SelectedResourcePackList(Minecraft p_i47647_1_, int p_i47647_2_, int p_i47647_3_) {
      super(p_i47647_1_, p_i47647_2_, p_i47647_3_, new TranslationTextComponent("resourcePack.selected.title"));
   }
}