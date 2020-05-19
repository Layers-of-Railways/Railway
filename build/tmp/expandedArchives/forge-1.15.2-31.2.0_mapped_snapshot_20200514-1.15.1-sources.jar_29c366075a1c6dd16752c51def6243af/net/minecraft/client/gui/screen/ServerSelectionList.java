package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerSelectionList extends ExtendedList<ServerSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor field_214358_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   private static final ResourceLocation field_214359_c = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation field_214360_d = new ResourceLocation("textures/gui/server_selection.png");
   private final MultiplayerScreen owner;
   private final List<ServerSelectionList.NormalEntry> serverListInternet = Lists.newArrayList();
   private final ServerSelectionList.Entry lanScanEntry = new ServerSelectionList.LanScanEntry();
   private final List<ServerSelectionList.LanDetectedEntry> serverListLan = Lists.newArrayList();

   public ServerSelectionList(MultiplayerScreen ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.owner = ownerIn;
   }

   private void func_195094_h() {
      this.clearEntries();
      this.serverListInternet.forEach(this::addEntry);
      this.addEntry(this.lanScanEntry);
      this.serverListLan.forEach(this::addEntry);
   }

   public void setSelected(ServerSelectionList.Entry p_setSelected_1_) {
      super.setSelected(p_setSelected_1_);
      if (this.getSelected() instanceof ServerSelectionList.NormalEntry) {
         NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", ((ServerSelectionList.NormalEntry)this.getSelected()).server.serverName)).getString());
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      ServerSelectionList.Entry serverselectionlist$entry = this.getSelected();
      return serverselectionlist$entry != null && serverselectionlist$entry.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   protected void moveSelection(int p_moveSelection_1_) {
      int i = this.children().indexOf(this.getSelected());
      int j = MathHelper.clamp(i + p_moveSelection_1_, 0, this.getItemCount() - 1);
      ServerSelectionList.Entry serverselectionlist$entry = this.children().get(j);
      if (serverselectionlist$entry instanceof ServerSelectionList.LanScanEntry) {
         j = MathHelper.clamp(j + (p_moveSelection_1_ > 0 ? 1 : -1), 0, this.getItemCount() - 1);
         serverselectionlist$entry = this.children().get(j);
      }

      super.setSelected(serverselectionlist$entry);
      this.ensureVisible(serverselectionlist$entry);
      this.owner.func_214295_b();
   }

   public void updateOnlineServers(ServerList p_148195_1_) {
      this.serverListInternet.clear();

      for(int i = 0; i < p_148195_1_.countServers(); ++i) {
         this.serverListInternet.add(new ServerSelectionList.NormalEntry(this.owner, p_148195_1_.getServerData(i)));
      }

      this.func_195094_h();
   }

   public void updateNetworkServers(List<LanServerInfo> p_148194_1_) {
      this.serverListLan.clear();

      for(LanServerInfo lanserverinfo : p_148194_1_) {
         this.serverListLan.add(new ServerSelectionList.LanDetectedEntry(this.owner, lanserverinfo));
      }

      this.func_195094_h();
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }

   protected boolean isFocused() {
      return this.owner.getFocused() == this;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends ExtendedList.AbstractListEntry<ServerSelectionList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanDetectedEntry extends ServerSelectionList.Entry {
      private final MultiplayerScreen screen;
      protected final Minecraft mc;
      protected final LanServerInfo serverData;
      private long lastClickTime;

      protected LanDetectedEntry(MultiplayerScreen p_i47141_1_, LanServerInfo p_i47141_2_) {
         this.screen = p_i47141_1_;
         this.serverData = p_i47141_2_;
         this.mc = Minecraft.getInstance();
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.mc.fontRenderer.drawString(I18n.format("lanServer.title"), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         this.mc.fontRenderer.drawString(this.serverData.getServerMotd(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12), 8421504);
         if (this.mc.gameSettings.hideServerAddress) {
            this.mc.fontRenderer.drawString(I18n.format("selectServer.hiddenAddress"), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12 + 11), 3158064);
         } else {
            this.mc.fontRenderer.drawString(this.serverData.getServerIpPort(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12 + 11), 3158064);
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         this.screen.func_214287_a(this);
         if (Util.milliTime() - this.lastClickTime < 250L) {
            this.screen.connectToSelected();
         }

         this.lastClickTime = Util.milliTime();
         return false;
      }

      public LanServerInfo getServerData() {
         return this.serverData;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanScanEntry extends ServerSelectionList.Entry {
      private final Minecraft mc = Minecraft.getInstance();

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         int i = p_render_2_ + p_render_5_ / 2 - 9 / 2;
         this.mc.fontRenderer.drawString(I18n.format("lanServer.scanning"), (float)(this.mc.currentScreen.width / 2 - this.mc.fontRenderer.getStringWidth(I18n.format("lanServer.scanning")) / 2), (float)i, 16777215);
         String s;
         switch((int)(Util.milliTime() / 300L % 4L)) {
         case 0:
         default:
            s = "O o o";
            break;
         case 1:
         case 3:
            s = "o O o";
            break;
         case 2:
            s = "o o O";
         }

         this.mc.fontRenderer.drawString(s, (float)(this.mc.currentScreen.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float)(i + 9), 8421504);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class NormalEntry extends ServerSelectionList.Entry {
      private final MultiplayerScreen owner;
      private final Minecraft mc;
      private final ServerData server;
      private final ResourceLocation serverIcon;
      private String lastIconB64;
      private DynamicTexture icon;
      private long lastClickTime;

      protected NormalEntry(MultiplayerScreen p_i50669_2_, ServerData p_i50669_3_) {
         this.owner = p_i50669_2_;
         this.server = p_i50669_3_;
         this.mc = Minecraft.getInstance();
         this.serverIcon = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(p_i50669_3_.serverIP) + "/icon");
         this.icon = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         if (!this.server.pinged) {
            this.server.pinged = true;
            this.server.pingToServer = -2L;
            this.server.serverMOTD = "";
            this.server.populationInfo = "";
            ServerSelectionList.field_214358_b.submit(() -> {
               try {
                  this.owner.getOldServerPinger().ping(this.server);
               } catch (UnknownHostException var2) {
                  this.server.pingToServer = -1L;
                  this.server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_resolve");
               } catch (Exception var3) {
                  this.server.pingToServer = -1L;
                  this.server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
               }

            });
         }

         boolean flag = this.server.version > SharedConstants.getVersion().getProtocolVersion();
         boolean flag1 = this.server.version < SharedConstants.getVersion().getProtocolVersion();
         boolean flag2 = flag || flag1;
         this.mc.fontRenderer.drawString(this.server.serverName, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         List<String> list = this.mc.fontRenderer.listFormattedStringToWidth(this.server.serverMOTD, p_render_4_ - 32 - 2);

         for(int i = 0; i < Math.min(list.size(), 2); ++i) {
            this.mc.fontRenderer.drawString(list.get(i), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12 + 9 * i), 8421504);
         }

         String s2 = flag2 ? TextFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
         int j = this.mc.fontRenderer.getStringWidth(s2);
         this.mc.fontRenderer.drawString(s2, (float)(p_render_3_ + p_render_4_ - j - 15 - 2), (float)(p_render_2_ + 1), 8421504);
         int k = 0;
         String s = null;
         int l;
         String s1;
         if (flag2) {
            l = 5;
            s1 = I18n.format(flag ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
            s = this.server.playerList;
         } else if (this.server.pinged && this.server.pingToServer != -2L) {
            if (this.server.pingToServer < 0L) {
               l = 5;
            } else if (this.server.pingToServer < 150L) {
               l = 0;
            } else if (this.server.pingToServer < 300L) {
               l = 1;
            } else if (this.server.pingToServer < 600L) {
               l = 2;
            } else if (this.server.pingToServer < 1000L) {
               l = 3;
            } else {
               l = 4;
            }

            if (this.server.pingToServer < 0L) {
               s1 = I18n.format("multiplayer.status.no_connection");
            } else {
               s1 = this.server.pingToServer + "ms";
               s = this.server.playerList;
            }
         } else {
            k = 1;
            l = (int)(Util.milliTime() / 100L + (long)(p_render_1_ * 2) & 7L);
            if (l > 4) {
               l = 8 - l;
            }

            s1 = I18n.format("multiplayer.status.pinging");
         }

         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
         AbstractGui.blit(p_render_3_ + p_render_4_ - 15, p_render_2_, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
         if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.lastIconB64)) {
            this.lastIconB64 = this.server.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.owner.getServerList().saveServerList();
         }

         if (this.icon != null) {
            this.drawTextureAt(p_render_3_, p_render_2_, this.serverIcon);
         } else {
            this.drawTextureAt(p_render_3_, p_render_2_, ServerSelectionList.field_214359_c);
         }

         int i1 = p_render_6_ - p_render_3_;
         int j1 = p_render_7_ - p_render_2_;
         if (i1 >= p_render_4_ - 15 && i1 <= p_render_4_ - 5 && j1 >= 0 && j1 <= 8) {
            this.owner.setHoveringText(s1);
         } else if (i1 >= p_render_4_ - j - 15 - 2 && i1 <= p_render_4_ - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.owner.setHoveringText(s);
         }

         net.minecraftforge.fml.client.ClientHooks.drawForgePingInfo(this.owner, server, p_render_3_, p_render_2_, p_render_4_, i1, j1);

         if (this.mc.gameSettings.touchscreen || p_render_8_) {
            this.mc.getTextureManager().bindTexture(ServerSelectionList.field_214360_d);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = p_render_6_ - p_render_3_;
            int l1 = p_render_7_ - p_render_2_;
            if (this.canJoin()) {
               if (k1 < 32 && k1 > 16) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_render_1_ > 0) {
               if (k1 < 16 && l1 < 16) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_render_1_ < this.owner.getServerList().countServers() - 1) {
               if (k1 < 16 && l1 > 16) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 0.0F, 32, 32, 256, 256);
               }
            }
         }

      }

      protected void drawTextureAt(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
         this.mc.getTextureManager().bindTexture(p_178012_3_);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private void prepareServerIcon() {
         String s = this.server.getBase64EncodedIconData();
         if (s == null) {
            this.mc.getTextureManager().deleteTexture(this.serverIcon);
            if (this.icon != null && this.icon.getTextureData() != null) {
               this.icon.getTextureData().close();
            }

            this.icon = null;
         } else {
            try {
               NativeImage nativeimage = NativeImage.readBase64(s);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               if (this.icon == null) {
                  this.icon = new DynamicTexture(nativeimage);
               } else {
                  this.icon.setTextureData(nativeimage);
                  this.icon.updateDynamicTexture();
               }

               this.mc.getTextureManager().loadTexture(this.serverIcon, this.icon);
            } catch (Throwable throwable) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.server.serverName, this.server.serverIP, throwable);
               this.server.setBase64EncodedIconData((String)null);
            }
         }

      }

      public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
         if (Screen.hasShiftDown()) {
            ServerSelectionList serverselectionlist = this.owner.serverListSelector;
            int i = serverselectionlist.children().indexOf(this);
            if (p_keyPressed_1_ == 264 && i < this.owner.getServerList().countServers() - 1 || p_keyPressed_1_ == 265 && i > 0) {
               this.func_228196_a_(i, p_keyPressed_1_ == 264 ? i + 1 : i - 1);
               return true;
            }
         }

         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }

      private void func_228196_a_(int p_228196_1_, int p_228196_2_) {
         this.owner.getServerList().swapServers(p_228196_1_, p_228196_2_);
         this.owner.serverListSelector.updateOnlineServers(this.owner.getServerList());
         ServerSelectionList.Entry serverselectionlist$entry = this.owner.serverListSelector.children().get(p_228196_2_);
         this.owner.serverListSelector.setSelected(serverselectionlist$entry);
         ServerSelectionList.this.ensureVisible(serverselectionlist$entry);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         double d0 = p_mouseClicked_1_ - (double)ServerSelectionList.this.getRowLeft();
         double d1 = p_mouseClicked_3_ - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
         if (d0 <= 32.0D) {
            if (d0 < 32.0D && d0 > 16.0D && this.canJoin()) {
               this.owner.func_214287_a(this);
               this.owner.connectToSelected();
               return true;
            }

            int i = this.owner.serverListSelector.children().indexOf(this);
            if (d0 < 16.0D && d1 < 16.0D && i > 0) {
               this.func_228196_a_(i, i - 1);
               return true;
            }

            if (d0 < 16.0D && d1 > 16.0D && i < this.owner.getServerList().countServers() - 1) {
               this.func_228196_a_(i, i + 1);
               return true;
            }
         }

         this.owner.func_214287_a(this);
         if (Util.milliTime() - this.lastClickTime < 250L) {
            this.owner.connectToSelected();
         }

         this.lastClickTime = Util.milliTime();
         return false;
      }

      public ServerData getServerData() {
         return this.server;
      }
   }
}