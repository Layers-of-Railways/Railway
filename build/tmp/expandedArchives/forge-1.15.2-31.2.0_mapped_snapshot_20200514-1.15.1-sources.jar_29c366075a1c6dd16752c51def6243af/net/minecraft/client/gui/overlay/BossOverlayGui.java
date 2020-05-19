package net.minecraft.client.gui.overlay;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossOverlayGui extends AbstractGui {
   private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft client;
   private final Map<UUID, ClientBossInfo> mapBossInfos = Maps.newLinkedHashMap();

   public BossOverlayGui(Minecraft clientIn) {
      this.client = clientIn;
   }

   public void render() {
      if (!this.mapBossInfos.isEmpty()) {
         int i = this.client.getMainWindow().getScaledWidth();
         int j = 12;

         for(ClientBossInfo clientbossinfo : this.mapBossInfos.values()) {
            int k = i / 2 - 91;
            net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event =
               net.minecraftforge.client.ForgeHooksClient.bossBarRenderPre(this.client.getMainWindow(), clientbossinfo, k, j, 10 + this.client.fontRenderer.FONT_HEIGHT);
            if (!event.isCanceled()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
            this.render(k, j, clientbossinfo);
            String s = clientbossinfo.getName().getFormattedText();
            int l = this.client.fontRenderer.getStringWidth(s);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            this.client.fontRenderer.drawStringWithShadow(s, (float)i1, (float)j1, 16777215);
            }
            j += event.getIncrement();
            net.minecraftforge.client.ForgeHooksClient.bossBarRenderPost(this.client.getMainWindow());
            if (j >= this.client.getMainWindow().getScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void render(int x, int y, BossInfo info) {
      this.blit(x, y, 0, info.getColor().ordinal() * 5 * 2, 182, 5);
      if (info.getOverlay() != BossInfo.Overlay.PROGRESS) {
         this.blit(x, y, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int i = (int)(info.getPercent() * 183.0F);
      if (i > 0) {
         this.blit(x, y, 0, info.getColor().ordinal() * 5 * 2 + 5, i, 5);
         if (info.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.blit(x, y, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
         }
      }

   }

   public void read(SUpdateBossInfoPacket packetIn) {
      if (packetIn.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
         this.mapBossInfos.put(packetIn.getUniqueId(), new ClientBossInfo(packetIn));
      } else if (packetIn.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
         this.mapBossInfos.remove(packetIn.getUniqueId());
      } else {
         this.mapBossInfos.get(packetIn.getUniqueId()).updateFromPacket(packetIn);
      }

   }

   public void clearBossInfos() {
      this.mapBossInfos.clear();
   }

   public boolean shouldPlayEndBossMusic() {
      if (!this.mapBossInfos.isEmpty()) {
         for(BossInfo bossinfo : this.mapBossInfos.values()) {
            if (bossinfo.shouldPlayEndBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenSky() {
      if (!this.mapBossInfos.isEmpty()) {
         for(BossInfo bossinfo : this.mapBossInfos.values()) {
            if (bossinfo.shouldDarkenSky()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateFog() {
      if (!this.mapBossInfos.isEmpty()) {
         for(BossInfo bossinfo : this.mapBossInfos.values()) {
            if (bossinfo.shouldCreateFog()) {
               return true;
            }
         }
      }

      return false;
   }
}