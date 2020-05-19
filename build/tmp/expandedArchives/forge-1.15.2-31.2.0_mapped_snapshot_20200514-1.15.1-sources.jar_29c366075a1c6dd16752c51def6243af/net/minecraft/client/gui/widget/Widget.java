package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Widget extends AbstractGui implements IRenderable, IGuiEventListener {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private static final int NARRATE_DELAY_MOUSE = 750;
   private static final int NARRATE_DELAY_FOCUS = 200;
   protected int width;
   protected int height;
   public int x;
   public int y;
   private String message;
   private boolean wasHovered;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   protected long nextNarration = Long.MAX_VALUE;
   private boolean focused;

   public Widget(int xIn, int yIn, String msg) {
      this(xIn, yIn, 200, 20, msg);
   }

   public Widget(int xIn, int yIn, int widthIn, int heightIn, String msg) {
      this.x = xIn;
      this.y = yIn;
      this.width = widthIn;
      this.height = heightIn;
      this.message = msg;
   }

   protected int getYImage(boolean p_getYImage_1_) {
      int i = 1;
      if (!this.active) {
         i = 0;
      } else if (p_getYImage_1_) {
         i = 2;
      }

      return i;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.visible) {
         this.isHovered = p_render_1_ >= this.x && p_render_2_ >= this.y && p_render_1_ < this.x + this.width && p_render_2_ < this.y + this.height;
         if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
               if (this.focused) {
                  this.queueNarration(200);
               } else {
                  this.queueNarration(750);
               }
            } else {
               this.nextNarration = Long.MAX_VALUE;
            }
         }

         if (this.visible) {
            this.renderButton(p_render_1_, p_render_2_, p_render_3_);
         }

         this.narrate();
         this.wasHovered = this.isHovered();
      }
   }

   protected void narrate() {
      if (this.active && this.isHovered() && Util.milliTime() > this.nextNarration) {
         String s = this.getNarrationMessage();
         if (!s.isEmpty()) {
            NarratorChatListener.INSTANCE.say(s);
            this.nextNarration = Long.MAX_VALUE;
         }
      }

   }

   protected String getNarrationMessage() {
      return this.getMessage().isEmpty() ? "" : I18n.format("gui.narrate.button", this.getMessage());
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      Minecraft minecraft = Minecraft.getInstance();
      FontRenderer fontrenderer = minecraft.fontRenderer;
      minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      int i = this.getYImage(this.isHovered());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
      this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
      this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
      int j = getFGColor();
      this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_) {
   }

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
   }

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
   }

   protected void onDrag(double p_onDrag_1_, double p_onDrag_3_, double p_onDrag_5_, double p_onDrag_7_) {
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(p_mouseClicked_5_)) {
            boolean flag = this.clicked(p_mouseClicked_1_, p_mouseClicked_3_);
            if (flag) {
               this.playDownSound(Minecraft.getInstance().getSoundHandler());
               this.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (this.isValidClickButton(p_mouseReleased_5_)) {
         this.onRelease(p_mouseReleased_1_, p_mouseReleased_3_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int p_isValidClickButton_1_) {
      return p_isValidClickButton_1_ == 0;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.isValidClickButton(p_mouseDragged_5_)) {
         this.onDrag(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_6_, p_mouseDragged_8_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double p_clicked_1_, double p_clicked_3_) {
      return this.active && this.visible && p_clicked_1_ >= (double)this.x && p_clicked_3_ >= (double)this.y && p_clicked_1_ < (double)(this.x + this.width) && p_clicked_3_ < (double)(this.y + this.height);
   }

   public boolean isHovered() {
      return this.isHovered || this.focused;
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      if (this.active && this.visible) {
         this.focused = !this.focused;
         this.onFocusedChanged(this.focused);
         return this.focused;
      } else {
         return false;
      }
   }

   protected void onFocusedChanged(boolean p_onFocusedChanged_1_) {
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return this.active && this.visible && p_isMouseOver_1_ >= (double)this.x && p_isMouseOver_3_ >= (double)this.y && p_isMouseOver_1_ < (double)(this.x + this.width) && p_isMouseOver_3_ < (double)(this.y + this.height);
   }

   public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
   }

   public void playDownSound(SoundHandler p_playDownSound_1_) {
      p_playDownSound_1_.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int p_setWidth_1_) {
      this.width = p_setWidth_1_;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int value) {
      this.height = value;
   }

   public void setAlpha(float p_setAlpha_1_) {
      this.alpha = p_setAlpha_1_;
   }

   public void setMessage(String p_setMessage_1_) {
      if (!Objects.equals(p_setMessage_1_, this.message)) {
         this.queueNarration(250);
      }

      this.message = p_setMessage_1_;
   }

   public void queueNarration(int p_queueNarration_1_) {
      this.nextNarration = Util.milliTime() + (long)p_queueNarration_1_;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   protected void setFocused(boolean p_setFocused_1_) {
      this.focused = p_setFocused_1_;
   }

   public static final int UNSET_FG_COLOR = -1;
   protected int packedFGColor = UNSET_FG_COLOR;
   public int getFGColor() {
      if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
      return this.active ? 16777215 : 10526880; // White : Light Grey
   }
   public void setFGColor(int color) {
      this.packedFGColor = color;
   }
   public void clearFGColor() {
      this.packedFGColor = UNSET_FG_COLOR;
   }
}