package net.minecraft.client.gui.overlay;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerTabOverlayGui extends AbstractGui {
   private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerTabOverlayGui.PlayerComparator());
   private final Minecraft mc;
   private final IngameGui guiIngame;
   private ITextComponent footer;
   private ITextComponent header;
   /** The last time the playerlist was opened (went from not being renderd, to being rendered) */
   private long lastTimeOpened;
   /** Weither or not the playerlist is currently being rendered */
   private boolean visible;

   public PlayerTabOverlayGui(Minecraft mcIn, IngameGui guiIngameIn) {
      this.mc = mcIn;
      this.guiIngame = guiIngameIn;
   }

   public ITextComponent getDisplayName(NetworkPlayerInfo p_200262_1_) {
      return p_200262_1_.getDisplayName() != null ? p_200262_1_.getDisplayName() : ScorePlayerTeam.formatMemberName(p_200262_1_.getPlayerTeam(), new StringTextComponent(p_200262_1_.getGameProfile().getName()));
   }

   /**
    * Called by GuiIngame to update the information stored in the playerlist, does not actually render the list,
    * however.
    */
   public void setVisible(boolean visible) {
      if (visible && !this.visible) {
         this.lastTimeOpened = Util.milliTime();
      }

      this.visible = visible;
   }

   /**
    * Renders the playerlist, its background, headers and footers.
    */
   public void render(int width, Scoreboard scoreboardIn, @Nullable ScoreObjective scoreObjectiveIn) {
      ClientPlayNetHandler clientplaynethandler = this.mc.player.connection;
      List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(clientplaynethandler.getPlayerInfoMap());
      int i = 0;
      int j = 0;

      for(NetworkPlayerInfo networkplayerinfo : list) {
         int k = this.mc.fontRenderer.getStringWidth(this.getDisplayName(networkplayerinfo).getFormattedText());
         i = Math.max(i, k);
         if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != ScoreCriteria.RenderType.HEARTS) {
            k = this.mc.fontRenderer.getStringWidth(" " + scoreboardIn.getOrCreateScore(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
            j = Math.max(j, k);
         }
      }

      list = list.subList(0, Math.min(list.size(), 80));
      int i4 = list.size();
      int j4 = i4;

      int k4;
      for(k4 = 1; j4 > 20; j4 = (i4 + k4 - 1) / k4) {
         ++k4;
      }

      boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getConnection().getNetworkManager().isEncrypted();
      int l;
      if (scoreObjectiveIn != null) {
         if (scoreObjectiveIn.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
            l = 90;
         } else {
            l = j;
         }
      } else {
         l = 0;
      }

      int i1 = Math.min(k4 * ((flag ? 9 : 0) + i + l + 13), width - 50) / k4;
      int j1 = width / 2 - (i1 * k4 + (k4 - 1) * 5) / 2;
      int k1 = 10;
      int l1 = i1 * k4 + (k4 - 1) * 5;
      List<String> list1 = null;
      if (this.header != null) {
         list1 = this.mc.fontRenderer.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

         for(String s : list1) {
            l1 = Math.max(l1, this.mc.fontRenderer.getStringWidth(s));
         }
      }

      List<String> list2 = null;
      if (this.footer != null) {
         list2 = this.mc.fontRenderer.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

         for(String s1 : list2) {
            l1 = Math.max(l1, this.mc.fontRenderer.getStringWidth(s1));
         }
      }

      if (list1 != null) {
         fill(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * 9, Integer.MIN_VALUE);

         for(String s2 : list1) {
            int i2 = this.mc.fontRenderer.getStringWidth(s2);
            this.mc.fontRenderer.drawStringWithShadow(s2, (float)(width / 2 - i2 / 2), (float)k1, -1);
            k1 += 9;
         }

         ++k1;
      }

      fill(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + j4 * 9, Integer.MIN_VALUE);
      int l4 = this.mc.gameSettings.getChatBackgroundColor(553648127);

      for(int i5 = 0; i5 < i4; ++i5) {
         int j5 = i5 / j4;
         int j2 = i5 % j4;
         int k2 = j1 + j5 * i1 + j5 * 5;
         int l2 = k1 + j2 * 9;
         fill(k2, l2, k2 + i1, l2 + 8, l4);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableAlphaTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (i5 < list.size()) {
            NetworkPlayerInfo networkplayerinfo1 = list.get(i5);
            GameProfile gameprofile = networkplayerinfo1.getGameProfile();
            if (flag) {
               PlayerEntity playerentity = this.mc.world.getPlayerByUuid(gameprofile.getId());
               boolean flag1 = playerentity != null && playerentity.isWearing(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
               this.mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
               int i3 = 8 + (flag1 ? 8 : 0);
               int j3 = 8 * (flag1 ? -1 : 1);
               AbstractGui.blit(k2, l2, 8, 8, 8.0F, (float)i3, 8, j3, 64, 64);
               if (playerentity != null && playerentity.isWearing(PlayerModelPart.HAT)) {
                  int k3 = 8 + (flag1 ? 8 : 0);
                  int l3 = 8 * (flag1 ? -1 : 1);
                  AbstractGui.blit(k2, l2, 8, 8, 40.0F, (float)k3, 8, l3, 64, 64);
               }

               k2 += 9;
            }

            String s4 = this.getDisplayName(networkplayerinfo1).getFormattedText();
            if (networkplayerinfo1.getGameType() == GameType.SPECTATOR) {
               this.mc.fontRenderer.drawStringWithShadow(TextFormatting.ITALIC + s4, (float)k2, (float)l2, -1862270977);
            } else {
               this.mc.fontRenderer.drawStringWithShadow(s4, (float)k2, (float)l2, -1);
            }

            if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != GameType.SPECTATOR) {
               int l5 = k2 + i + 1;
               int i6 = l5 + l;
               if (i6 - l5 > 5) {
                  this.drawScoreboardValues(scoreObjectiveIn, l2, gameprofile.getName(), l5, i6, networkplayerinfo1);
               }
            }

            this.drawPing(i1, k2 - (flag ? 9 : 0), l2, networkplayerinfo1);
         }
      }

      if (list2 != null) {
         k1 = k1 + j4 * 9 + 1;
         fill(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * 9, Integer.MIN_VALUE);

         for(String s3 : list2) {
            int k5 = this.mc.fontRenderer.getStringWidth(s3);
            this.mc.fontRenderer.drawStringWithShadow(s3, (float)(width / 2 - k5 / 2), (float)k1, -1);
            k1 += 9;
         }
      }

   }

   protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
      int i = 0;
      int j;
      if (networkPlayerInfoIn.getResponseTime() < 0) {
         j = 5;
      } else if (networkPlayerInfoIn.getResponseTime() < 150) {
         j = 0;
      } else if (networkPlayerInfoIn.getResponseTime() < 300) {
         j = 1;
      } else if (networkPlayerInfoIn.getResponseTime() < 600) {
         j = 2;
      } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
         j = 3;
      } else {
         j = 4;
      }

      this.setBlitOffset(this.getBlitOffset() + 100);
      this.blit(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0, 176 + j * 8, 10, 8);
      this.setBlitOffset(this.getBlitOffset() - 100);
   }

   private void drawScoreboardValues(ScoreObjective objective, int p_175247_2_, String name, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo info) {
      int i = objective.getScoreboard().getOrCreateScore(name, objective).getScorePoints();
      if (objective.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
         long j = Util.milliTime();
         if (this.lastTimeOpened == info.getRenderVisibilityId()) {
            if (i < info.getLastHealth()) {
               info.setLastHealthTime(j);
               info.setHealthBlinkTime((long)(this.guiIngame.getTicks() + 20));
            } else if (i > info.getLastHealth()) {
               info.setLastHealthTime(j);
               info.setHealthBlinkTime((long)(this.guiIngame.getTicks() + 10));
            }
         }

         if (j - info.getLastHealthTime() > 1000L || this.lastTimeOpened != info.getRenderVisibilityId()) {
            info.setLastHealth(i);
            info.setDisplayHealth(i);
            info.setLastHealthTime(j);
         }

         info.setRenderVisibilityId(this.lastTimeOpened);
         info.setLastHealth(i);
         int k = MathHelper.ceil((float)Math.max(i, info.getDisplayHealth()) / 2.0F);
         int l = Math.max(MathHelper.ceil((float)(i / 2)), Math.max(MathHelper.ceil((float)(info.getDisplayHealth() / 2)), 10));
         boolean flag = info.getHealthBlinkTime() > (long)this.guiIngame.getTicks() && (info.getHealthBlinkTime() - (long)this.guiIngame.getTicks()) / 3L % 2L == 1L;
         if (k > 0) {
            int i1 = MathHelper.floor(Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)l, 9.0F));
            if (i1 > 3) {
               for(int j1 = k; j1 < l; ++j1) {
                  this.blit(p_175247_4_ + j1 * i1, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
               }

               for(int l1 = 0; l1 < k; ++l1) {
                  this.blit(p_175247_4_ + l1 * i1, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                  if (flag) {
                     if (l1 * 2 + 1 < info.getDisplayHealth()) {
                        this.blit(p_175247_4_ + l1 * i1, p_175247_2_, 70, 0, 9, 9);
                     }

                     if (l1 * 2 + 1 == info.getDisplayHealth()) {
                        this.blit(p_175247_4_ + l1 * i1, p_175247_2_, 79, 0, 9, 9);
                     }
                  }

                  if (l1 * 2 + 1 < i) {
                     this.blit(p_175247_4_ + l1 * i1, p_175247_2_, l1 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (l1 * 2 + 1 == i) {
                     this.blit(p_175247_4_ + l1 * i1, p_175247_2_, l1 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float f = MathHelper.clamp((float)i / 20.0F, 0.0F, 1.0F);
               int k1 = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
               String s = "" + (float)i / 2.0F;
               if (p_175247_5_ - this.mc.fontRenderer.getStringWidth(s + "hp") >= p_175247_4_) {
                  s = s + "hp";
               }

               this.mc.fontRenderer.drawStringWithShadow(s, (float)((p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float)p_175247_2_, k1);
            }
         }
      } else {
         String s1 = TextFormatting.YELLOW + "" + i;
         this.mc.fontRenderer.drawStringWithShadow(s1, (float)(p_175247_5_ - this.mc.fontRenderer.getStringWidth(s1)), (float)p_175247_2_, 16777215);
      }

   }

   public void setFooter(@Nullable ITextComponent footerIn) {
      this.footer = footerIn;
   }

   public void setHeader(@Nullable ITextComponent headerIn) {
      this.header = headerIn;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   @OnlyIn(Dist.CLIENT)
   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
         ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
         ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName(), String::compareToIgnoreCase).result();
      }
   }
}