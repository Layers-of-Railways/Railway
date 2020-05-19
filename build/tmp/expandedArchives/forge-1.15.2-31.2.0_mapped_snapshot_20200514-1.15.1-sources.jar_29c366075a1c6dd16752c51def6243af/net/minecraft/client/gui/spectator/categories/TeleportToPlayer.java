package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject {
   private static final Ordering<NetworkPlayerInfo> PROFILE_ORDER = Ordering.from((p_210243_0_, p_210243_1_) -> {
      return ComparisonChain.start().compare(p_210243_0_.getGameProfile().getId(), p_210243_1_.getGameProfile().getId()).result();
   });
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public TeleportToPlayer() {
      this(PROFILE_ORDER.sortedCopy(Minecraft.getInstance().getConnection().getPlayerInfoMap()));
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> profiles) {
      for(NetworkPlayerInfo networkplayerinfo : PROFILE_ORDER.sortedCopy(profiles)) {
         if (networkplayerinfo.getGameType() != GameType.SPECTATOR) {
            this.items.add(new PlayerMenuObject(networkplayerinfo.getGameProfile()));
         }
      }

   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return new TranslationTextComponent("spectatorMenu.teleport.prompt");
   }

   public void selectItem(SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   public ITextComponent getSpectatorName() {
      return new TranslationTextComponent("spectatorMenu.teleport");
   }

   public void renderIcon(float brightness, int alpha) {
      Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
      AbstractGui.blit(0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}