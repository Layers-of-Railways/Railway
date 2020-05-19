package net.minecraft.realms;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RealmsAbstractButtonProxy<T extends AbstractRealmsButton<?>> {
   T getButton();

   boolean active();

   void active(boolean p_active_1_);

   boolean isVisible();

   void setVisible(boolean p_setVisible_1_);
}