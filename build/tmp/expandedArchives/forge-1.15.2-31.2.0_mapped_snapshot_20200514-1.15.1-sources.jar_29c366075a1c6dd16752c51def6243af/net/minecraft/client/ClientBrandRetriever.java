package net.minecraft.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBrandRetriever {
   public static String getClientModName() {
      return net.minecraftforge.fml.BrandingControl.getClientBranding();
   }
}