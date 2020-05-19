package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MerchantScreen extends ContainerScreen<MerchantContainer> {
   /** The GUI texture for the villager merchant GUI. */
   private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager2.png");
   /** The integer value corresponding to the currently selected merchant recipe. */
   private int selectedMerchantRecipe;
   private final MerchantScreen.TradeButton[] field_214138_m = new MerchantScreen.TradeButton[7];
   private int field_214139_n;
   private boolean field_214140_o;

   public MerchantScreen(MerchantContainer p_i51080_1_, PlayerInventory p_i51080_2_, ITextComponent p_i51080_3_) {
      super(p_i51080_1_, p_i51080_2_, p_i51080_3_);
      this.xSize = 276;
   }

   private void func_195391_j() {
      this.container.setCurrentRecipeIndex(this.selectedMerchantRecipe);
      this.container.func_217046_g(this.selectedMerchantRecipe);
      this.minecraft.getConnection().sendPacket(new CSelectTradePacket(this.selectedMerchantRecipe));
   }

   protected void init() {
      super.init();
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      int k = j + 16 + 2;

      for(int l = 0; l < 7; ++l) {
         this.field_214138_m[l] = this.addButton(new MerchantScreen.TradeButton(i + 5, k, l, (p_214132_1_) -> {
            if (p_214132_1_ instanceof MerchantScreen.TradeButton) {
               this.selectedMerchantRecipe = ((MerchantScreen.TradeButton)p_214132_1_).func_212937_a() + this.field_214139_n;
               this.func_195391_j();
            }

         }));
         k += 20;
      }

   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      int i = this.container.getMerchantLevel();
      int j = this.ySize - 94;
      if (i > 0 && i <= 5 && this.container.func_217042_i()) {
         String s2 = this.title.getFormattedText();
         String s1 = "- " + I18n.format("merchant.level." + i);
         int k = this.font.getStringWidth(s2);
         int l = this.font.getStringWidth(s1);
         int i1 = k + l + 3;
         int j1 = 49 + this.xSize / 2 - i1 / 2;
         this.font.drawString(s2, (float)j1, 6.0F, 4210752);
         this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, (float)j, 4210752);
         this.font.drawString(s1, (float)(j1 + k + 3), 6.0F, 4210752);
      } else {
         String s = this.title.getFormattedText();
         this.font.drawString(s, (float)(49 + this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
         this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, (float)j, 4210752);
      }

      String s3 = I18n.format("merchant.trades");
      int k1 = this.font.getStringWidth(s3);
      this.font.drawString(s3, (float)(5 - k1 / 2 + 48), 6.0F, 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      blit(i, j, this.getBlitOffset(), 0.0F, 0.0F, this.xSize, this.ySize, 256, 512);
      MerchantOffers merchantoffers = this.container.getOffers();
      if (!merchantoffers.isEmpty()) {
         int k = this.selectedMerchantRecipe;
         if (k < 0 || k >= merchantoffers.size()) {
            return;
         }

         MerchantOffer merchantoffer = merchantoffers.get(k);
         if (merchantoffer.hasNoUsesLeft()) {
            this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blit(this.guiLeft + 83 + 99, this.guiTop + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
         }
      }

   }

   private void func_214130_a(int p_214130_1_, int p_214130_2_, MerchantOffer p_214130_3_) {
      this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int i = this.container.getMerchantLevel();
      int j = this.container.getXp();
      if (i < 5) {
         blit(p_214130_1_ + 136, p_214130_2_ + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
         int k = VillagerData.func_221133_b(i);
         if (j >= k && VillagerData.func_221128_d(i)) {
            int l = 100;
            float f = (float)(100 / (VillagerData.func_221127_c(i) - k));
            int i1 = Math.min(MathHelper.floor(f * (float)(j - k)), 100);
            blit(p_214130_1_ + 136, p_214130_2_ + 16, this.getBlitOffset(), 0.0F, 191.0F, i1 + 1, 5, 256, 512);
            int j1 = this.container.getPendingExp();
            if (j1 > 0) {
               int k1 = Math.min(MathHelper.floor((float)j1 * f), 100 - i1);
               blit(p_214130_1_ + 136 + i1 + 1, p_214130_2_ + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, k1, 3, 256, 512);
            }

         }
      }
   }

   private void func_214129_a(int p_214129_1_, int p_214129_2_, MerchantOffers p_214129_3_) {
      int i = p_214129_3_.size() + 1 - 7;
      if (i > 1) {
         int j = 139 - (27 + (i - 1) * 139 / i);
         int k = 1 + j / i + 139 / i;
         int l = 113;
         int i1 = Math.min(113, this.field_214139_n * k);
         if (this.field_214139_n == i - 1) {
            i1 = 113;
         }

         blit(p_214129_1_ + 94, p_214129_2_ + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
      } else {
         blit(p_214129_1_ + 94, p_214129_2_ + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      MerchantOffers merchantoffers = this.container.getOffers();
      if (!merchantoffers.isEmpty()) {
         int i = (this.width - this.xSize) / 2;
         int j = (this.height - this.ySize) / 2;
         int k = j + 16 + 1;
         int l = i + 5 + 5;
         RenderSystem.pushMatrix();
         RenderSystem.enableRescaleNormal();
         this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
         this.func_214129_a(i, j, merchantoffers);
         int i1 = 0;

         for(MerchantOffer merchantoffer : merchantoffers) {
            if (this.func_214135_a(merchantoffers.size()) && (i1 < this.field_214139_n || i1 >= 7 + this.field_214139_n)) {
               ++i1;
            } else {
               ItemStack itemstack = merchantoffer.getBuyingStackFirst();
               ItemStack itemstack1 = merchantoffer.func_222205_b();
               ItemStack itemstack2 = merchantoffer.getBuyingStackSecond();
               ItemStack itemstack3 = merchantoffer.getSellingStack();
               this.itemRenderer.zLevel = 100.0F;
               int j1 = k + 2;
               this.func_214137_a(itemstack1, itemstack, l, j1);
               if (!itemstack2.isEmpty()) {
                  this.itemRenderer.renderItemAndEffectIntoGUI(itemstack2, i + 5 + 35, j1);
                  this.itemRenderer.renderItemOverlays(this.font, itemstack2, i + 5 + 35, j1);
               }

               this.func_214134_a(merchantoffer, i, j1);
               this.itemRenderer.renderItemAndEffectIntoGUI(itemstack3, i + 5 + 68, j1);
               this.itemRenderer.renderItemOverlays(this.font, itemstack3, i + 5 + 68, j1);
               this.itemRenderer.zLevel = 0.0F;
               k += 20;
               ++i1;
            }
         }

         int k1 = this.selectedMerchantRecipe;
         MerchantOffer merchantoffer1 = merchantoffers.get(k1);
         if (this.container.func_217042_i()) {
            this.func_214130_a(i, j, merchantoffer1);
         }

         if (merchantoffer1.hasNoUsesLeft() && this.isPointInRegion(186, 35, 22, 21, (double)p_render_1_, (double)p_render_2_) && this.container.func_223432_h()) {
            this.renderTooltip(I18n.format("merchant.deprecated"), p_render_1_, p_render_2_);
         }

         for(MerchantScreen.TradeButton merchantscreen$tradebutton : this.field_214138_m) {
            if (merchantscreen$tradebutton.isHovered()) {
               merchantscreen$tradebutton.renderToolTip(p_render_1_, p_render_2_);
            }

            merchantscreen$tradebutton.visible = merchantscreen$tradebutton.field_212938_a < this.container.getOffers().size();
         }

         RenderSystem.popMatrix();
         RenderSystem.enableDepthTest();
      }

      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   private void func_214134_a(MerchantOffer p_214134_1_, int p_214134_2_, int p_214134_3_) {
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      if (p_214134_1_.hasNoUsesLeft()) {
         blit(p_214134_2_ + 5 + 35 + 20, p_214134_3_ + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
      } else {
         blit(p_214134_2_ + 5 + 35 + 20, p_214134_3_ + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
      }

   }

   private void func_214137_a(ItemStack p_214137_1_, ItemStack p_214137_2_, int p_214137_3_, int p_214137_4_) {
      this.itemRenderer.renderItemAndEffectIntoGUI(p_214137_1_, p_214137_3_, p_214137_4_);
      if (p_214137_2_.getCount() == p_214137_1_.getCount()) {
         this.itemRenderer.renderItemOverlays(this.font, p_214137_1_, p_214137_3_, p_214137_4_);
      } else {
         this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_214137_2_, p_214137_3_, p_214137_4_, p_214137_2_.getCount() == 1 ? "1" : null);
         this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_214137_1_, p_214137_3_ + 14, p_214137_4_, p_214137_1_.getCount() == 1 ? "1" : null);
         this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
         this.setBlitOffset(this.getBlitOffset() + 300);
         blit(p_214137_3_ + 7, p_214137_4_ + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
         this.setBlitOffset(this.getBlitOffset() - 300);
      }

   }

   private boolean func_214135_a(int p_214135_1_) {
      return p_214135_1_ > 7;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      int i = this.container.getOffers().size();
      if (this.func_214135_a(i)) {
         int j = i - 7;
         this.field_214139_n = (int)((double)this.field_214139_n - p_mouseScrolled_5_);
         this.field_214139_n = MathHelper.clamp(this.field_214139_n, 0, j);
      }

      return true;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      int i = this.container.getOffers().size();
      if (this.field_214140_o) {
         int j = this.guiTop + 18;
         int k = j + 139;
         int l = i - 7;
         float f = ((float)p_mouseDragged_3_ - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
         f = f * (float)l + 0.5F;
         this.field_214139_n = MathHelper.clamp((int)f, 0, l);
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.field_214140_o = false;
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      if (this.func_214135_a(this.container.getOffers().size()) && p_mouseClicked_1_ > (double)(i + 94) && p_mouseClicked_1_ < (double)(i + 94 + 6) && p_mouseClicked_3_ > (double)(j + 18) && p_mouseClicked_3_ <= (double)(j + 18 + 139 + 1)) {
         this.field_214140_o = true;
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   @OnlyIn(Dist.CLIENT)
   class TradeButton extends Button {
      final int field_212938_a;

      public TradeButton(int p_i50601_2_, int p_i50601_3_, int p_i50601_4_, Button.IPressable p_i50601_5_) {
         super(p_i50601_2_, p_i50601_3_, 89, 20, "", p_i50601_5_);
         this.field_212938_a = p_i50601_4_;
         this.visible = false;
      }

      public int func_212937_a() {
         return this.field_212938_a;
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         if (this.isHovered && MerchantScreen.this.container.getOffers().size() > this.field_212938_a + MerchantScreen.this.field_214139_n) {
            if (p_renderToolTip_1_ < this.x + 20) {
               ItemStack itemstack = MerchantScreen.this.container.getOffers().get(this.field_212938_a + MerchantScreen.this.field_214139_n).func_222205_b();
               MerchantScreen.this.renderTooltip(itemstack, p_renderToolTip_1_, p_renderToolTip_2_);
            } else if (p_renderToolTip_1_ < this.x + 50 && p_renderToolTip_1_ > this.x + 30) {
               ItemStack itemstack2 = MerchantScreen.this.container.getOffers().get(this.field_212938_a + MerchantScreen.this.field_214139_n).getBuyingStackSecond();
               if (!itemstack2.isEmpty()) {
                  MerchantScreen.this.renderTooltip(itemstack2, p_renderToolTip_1_, p_renderToolTip_2_);
               }
            } else if (p_renderToolTip_1_ > this.x + 65) {
               ItemStack itemstack1 = MerchantScreen.this.container.getOffers().get(this.field_212938_a + MerchantScreen.this.field_214139_n).getSellingStack();
               MerchantScreen.this.renderTooltip(itemstack1, p_renderToolTip_1_, p_renderToolTip_2_);
            }
         }

      }
   }
}