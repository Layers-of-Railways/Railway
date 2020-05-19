package net.minecraft.client.gui.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayer;
import net.minecraft.client.gui.spectator.categories.TeleportToTeam;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseSpectatorGroup implements ISpectatorMenuView {
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public BaseSpectatorGroup() {
      this.items.add(new TeleportToPlayer());
      this.items.add(new TeleportToTeam());
   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return new TranslationTextComponent("spectatorMenu.root.prompt");
   }
}