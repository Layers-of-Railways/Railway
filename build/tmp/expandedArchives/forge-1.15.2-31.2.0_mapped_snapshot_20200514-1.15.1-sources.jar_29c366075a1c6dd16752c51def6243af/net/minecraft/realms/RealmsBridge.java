package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBridge extends RealmsScreen {
   private Screen previousScreen;

   public void switchToRealms(Screen p_switchToRealms_1_) {
      this.previousScreen = p_switchToRealms_1_;
      Realms.setScreen(new RealmsMainScreen(this));
   }

   @Nullable
   public RealmsScreenProxy getNotificationScreen(Screen p_getNotificationScreen_1_) {
      this.previousScreen = p_getNotificationScreen_1_;
      return (new RealmsNotificationsScreen(this)).getProxy();
   }

   public void init() {
      Minecraft.getInstance().displayGuiScreen(this.previousScreen);
   }
}