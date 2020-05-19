package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer> {
   private static final ResourceLocation field_214113_k = new ResourceLocation("textures/gui/container/loom.png");
   private static final int field_214114_l = (BannerPattern.field_222480_O - 5 - 1 + 4 - 1) / 4;
   private final ModelRenderer field_228188_m_;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> field_230155_n_;
   private ItemStack field_214119_q = ItemStack.EMPTY;
   private ItemStack field_214120_r = ItemStack.EMPTY;
   private ItemStack field_214121_s = ItemStack.EMPTY;
   private boolean field_214123_u;
   private boolean field_214124_v;
   private boolean field_214125_w;
   private float field_214126_x;
   private boolean field_214127_y;
   private int field_214128_z = 1;

   public LoomScreen(LoomContainer p_i51081_1_, PlayerInventory p_i51081_2_, ITextComponent p_i51081_3_) {
      super(p_i51081_1_, p_i51081_2_, p_i51081_3_);
      this.field_228188_m_ = BannerTileEntityRenderer.func_228836_a_();
      p_i51081_1_.func_217020_a(this::func_214111_b);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 4.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.renderBackground();
      this.minecraft.getTextureManager().bindTexture(field_214113_k);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
      Slot slot = this.container.getBannerSlot();
      Slot slot1 = this.container.getDyeSlot();
      Slot slot2 = this.container.getPatternSlot();
      Slot slot3 = this.container.getOutputSlot();
      if (!slot.getHasStack()) {
         this.blit(i + slot.xPos, j + slot.yPos, this.xSize, 0, 16, 16);
      }

      if (!slot1.getHasStack()) {
         this.blit(i + slot1.xPos, j + slot1.yPos, this.xSize + 16, 0, 16, 16);
      }

      if (!slot2.getHasStack()) {
         this.blit(i + slot2.xPos, j + slot2.yPos, this.xSize + 32, 0, 16, 16);
      }

      int k = (int)(41.0F * this.field_214126_x);
      this.blit(i + 119, j + 13 + k, 232 + (this.field_214123_u ? 0 : 12), 0, 12, 15);
      RenderHelper.setupGuiFlatDiffuseLighting();
      if (this.field_230155_n_ != null && !this.field_214125_w) {
         IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.getRenderTypeBuffers().getBufferSource();
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.translate((double)(i + 139), (double)(j + 52), 0.0D);
         matrixstack.scale(24.0F, -24.0F, 1.0F);
         matrixstack.translate(0.5D, 0.5D, 0.5D);
         float f = 0.6666667F;
         matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
         this.field_228188_m_.rotateAngleX = 0.0F;
         this.field_228188_m_.rotationPointY = -32.0F;
         BannerTileEntityRenderer.func_230180_a_(matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.field_228188_m_, ModelBakery.LOCATION_BANNER_BASE, true, this.field_230155_n_);
         irendertypebuffer$impl.finish();
      } else if (this.field_214125_w) {
         this.blit(i + slot3.xPos - 2, j + slot3.yPos - 2, this.xSize, 17, 17, 16);
      }

      if (this.field_214123_u) {
         int i2 = i + 60;
         int k2 = j + 13;
         int i3 = this.field_214128_z + 16;

         for(int l = this.field_214128_z; l < i3 && l < BannerPattern.field_222480_O - 5; ++l) {
            int i1 = l - this.field_214128_z;
            int j1 = i2 + i1 % 4 * 14;
            int k1 = k2 + i1 / 4 * 14;
            this.minecraft.getTextureManager().bindTexture(field_214113_k);
            int l1 = this.ySize;
            if (l == this.container.func_217023_e()) {
               l1 += 14;
            } else if (mouseX >= j1 && mouseY >= k1 && mouseX < j1 + 14 && mouseY < k1 + 14) {
               l1 += 28;
            }

            this.blit(j1, k1, 0, l1, 14, 14);
            this.func_228190_b_(l, j1, k1);
         }
      } else if (this.field_214124_v) {
         int j2 = i + 60;
         int l2 = j + 13;
         this.minecraft.getTextureManager().bindTexture(field_214113_k);
         this.blit(j2, l2, 0, this.ySize, 14, 14);
         int j3 = this.container.func_217023_e();
         this.func_228190_b_(j3, j2, l2);
      }

      RenderHelper.setupGui3DDiffuseLighting();
   }

   private void func_228190_b_(int p_228190_1_, int p_228190_2_, int p_228190_3_) {
      ItemStack itemstack = new ItemStack(Items.GRAY_BANNER);
      CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("BlockEntityTag");
      ListNBT listnbt = (new BannerPattern.Builder()).setPatternWithColor(BannerPattern.BASE, DyeColor.GRAY).setPatternWithColor(BannerPattern.values()[p_228190_1_], DyeColor.WHITE).func_222476_a();
      compoundnbt.put("Patterns", listnbt);
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.push();
      matrixstack.translate((double)((float)p_228190_2_ + 0.5F), (double)(p_228190_3_ + 16), 0.0D);
      matrixstack.scale(6.0F, -6.0F, 1.0F);
      matrixstack.translate(0.5D, 0.5D, 0.0D);
      matrixstack.translate(0.5D, 0.5D, 0.5D);
      float f = 0.6666667F;
      matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.getRenderTypeBuffers().getBufferSource();
      this.field_228188_m_.rotateAngleX = 0.0F;
      this.field_228188_m_.rotationPointY = -32.0F;
      List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.func_230138_a_(DyeColor.GRAY, BannerTileEntity.func_230139_a_(itemstack));
      BannerTileEntityRenderer.func_230180_a_(matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.field_228188_m_, ModelBakery.LOCATION_BANNER_BASE, true, list);
      matrixstack.pop();
      irendertypebuffer$impl.finish();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.field_214127_y = false;
      if (this.field_214123_u) {
         int i = this.guiLeft + 60;
         int j = this.guiTop + 13;
         int k = this.field_214128_z + 16;

         for(int l = this.field_214128_z; l < k; ++l) {
            int i1 = l - this.field_214128_z;
            double d0 = p_mouseClicked_1_ - (double)(i + i1 % 4 * 14);
            double d1 = p_mouseClicked_3_ - (double)(j + i1 / 4 * 14);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 14.0D && d1 < 14.0D && this.container.enchantItem(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
               this.minecraft.playerController.sendEnchantPacket((this.container).windowId, l);
               return true;
            }
         }

         i = this.guiLeft + 119;
         j = this.guiTop + 9;
         if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ < (double)(i + 12) && p_mouseClicked_3_ >= (double)j && p_mouseClicked_3_ < (double)(j + 56)) {
            this.field_214127_y = true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.field_214127_y && this.field_214123_u) {
         int i = this.guiTop + 13;
         int j = i + 56;
         this.field_214126_x = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
         int k = field_214114_l - 4;
         int l = (int)((double)(this.field_214126_x * (float)k) + 0.5D);
         if (l < 0) {
            l = 0;
         }

         this.field_214128_z = 1 + l * 4;
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (this.field_214123_u) {
         int i = field_214114_l - 4;
         this.field_214126_x = (float)((double)this.field_214126_x - p_mouseScrolled_5_ / (double)i);
         this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
         this.field_214128_z = 1 + (int)((double)(this.field_214126_x * (float)i) + 0.5D) * 4;
      }

      return true;
   }

   protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
      return mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
   }

   private void func_214111_b() {
      ItemStack itemstack = this.container.getOutputSlot().getStack();
      if (itemstack.isEmpty()) {
         this.field_230155_n_ = null;
      } else {
         this.field_230155_n_ = BannerTileEntity.func_230138_a_(((BannerItem)itemstack.getItem()).getColor(), BannerTileEntity.func_230139_a_(itemstack));
      }

      ItemStack itemstack1 = this.container.getBannerSlot().getStack();
      ItemStack itemstack2 = this.container.getDyeSlot().getStack();
      ItemStack itemstack3 = this.container.getPatternSlot().getStack();
      CompoundNBT compoundnbt = itemstack1.getOrCreateChildTag("BlockEntityTag");
      this.field_214125_w = compoundnbt.contains("Patterns", 9) && !itemstack1.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;
      if (this.field_214125_w) {
         this.field_230155_n_ = null;
      }

      if (!ItemStack.areItemStacksEqual(itemstack1, this.field_214119_q) || !ItemStack.areItemStacksEqual(itemstack2, this.field_214120_r) || !ItemStack.areItemStacksEqual(itemstack3, this.field_214121_s)) {
         this.field_214123_u = !itemstack1.isEmpty() && !itemstack2.isEmpty() && itemstack3.isEmpty() && !this.field_214125_w;
         this.field_214124_v = !this.field_214125_w && !itemstack3.isEmpty() && !itemstack1.isEmpty() && !itemstack2.isEmpty();
      }

      this.field_214119_q = itemstack1.copy();
      this.field_214120_r = itemstack2.copy();
      this.field_214121_s = itemstack3.copy();
   }
}