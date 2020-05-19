package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft minecraft = Minecraft.getInstance();

      for(ScorePlayerTeam scoreplayerteam : minecraft.world.getScoreboard().getTeams()) {
         this.items.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
      }

   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return new TranslationTextComponent("spectatorMenu.team_teleport.prompt");
   }

   public void selectItem(SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   public ITextComponent getSpectatorName() {
      return new TranslationTextComponent("spectatorMenu.team_teleport");
   }

   public void renderIcon(float brightness, int alpha) {
      Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
      AbstractGui.blit(0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      for(ISpectatorMenuObject ispectatormenuobject : this.items) {
         if (ispectatormenuobject.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam team;
      private final ResourceLocation location;
      private final List<NetworkPlayerInfo> players;

      public TeamSelectionObject(ScorePlayerTeam teamIn) {
         this.team = teamIn;
         this.players = Lists.newArrayList();

         for(String s : teamIn.getMembershipCollection()) {
            NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(s);
            if (networkplayerinfo != null) {
               this.players.add(networkplayerinfo);
            }
         }

         if (this.players.isEmpty()) {
            this.location = DefaultPlayerSkin.getDefaultSkinLegacy();
         } else {
            String s1 = this.players.get((new Random()).nextInt(this.players.size())).getGameProfile().getName();
            this.location = AbstractClientPlayerEntity.getLocationSkin(s1);
            AbstractClientPlayerEntity.getDownloadImageSkin(this.location, s1);
         }

      }

      public void selectItem(SpectatorMenu menu) {
         menu.selectCategory(new TeleportToPlayer(this.players));
      }

      public ITextComponent getSpectatorName() {
         return this.team.getDisplayName();
      }

      public void renderIcon(float brightness, int alpha) {
         Integer integer = this.team.getColor().getColor();
         if (integer != null) {
            float f = (float)(integer >> 16 & 255) / 255.0F;
            float f1 = (float)(integer >> 8 & 255) / 255.0F;
            float f2 = (float)(integer & 255) / 255.0F;
            AbstractGui.fill(1, 1, 15, 15, MathHelper.rgb(f * brightness, f1 * brightness, f2 * brightness) | alpha << 24);
         }

         Minecraft.getInstance().getTextureManager().bindTexture(this.location);
         RenderSystem.color4f(brightness, brightness, brightness, (float)alpha / 255.0F);
         AbstractGui.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
         AbstractGui.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
      }

      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}