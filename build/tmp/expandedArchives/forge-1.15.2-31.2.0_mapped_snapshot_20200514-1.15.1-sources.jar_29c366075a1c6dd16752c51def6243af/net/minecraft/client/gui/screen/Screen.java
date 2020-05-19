package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends FocusableGui implements IRenderable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
   protected final ITextComponent title;
   protected final List<IGuiEventListener> children = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   protected final List<Widget> buttons = Lists.newArrayList();
   public boolean passEvents;
   protected FontRenderer font;
   private URI clickedLink;

   protected Screen(ITextComponent titleIn) {
      this.title = titleIn;
   }

   public ITextComponent getTitle() {
      return this.title;
   }

   public String getNarrationMessage() {
      return this.getTitle().getString();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).render(p_render_1_, p_render_2_, p_render_3_);
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (p_keyPressed_1_ == 258) {
         boolean flag = !hasShiftDown();
         if (!this.changeFocus(flag)) {
            this.changeFocus(flag);
         }

         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.displayGuiScreen((Screen)null);
   }

   protected <T extends Widget> T addButton(T p_addButton_1_) {
      this.buttons.add(p_addButton_1_);
      this.children.add(p_addButton_1_);
      return p_addButton_1_;
   }

   protected void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      FontRenderer font = p_renderTooltip_1_.getItem().getFontRenderer(p_renderTooltip_1_);
      net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(p_renderTooltip_1_);
      this.renderTooltip(this.getTooltipFromItem(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_, (font == null ? this.font : font));
      net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
   }

   public List<String> getTooltipFromItem(ItemStack p_getTooltipFromItem_1_) {
      List<ITextComponent> list = p_getTooltipFromItem_1_.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
      List<String> list1 = Lists.newArrayList();

      for(ITextComponent itextcomponent : list) {
         list1.add(itextcomponent.getFormattedText());
      }

      return list1;
   }

   public void renderTooltip(String p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      this.renderTooltip(Arrays.asList(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, font);
   }
   public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_, FontRenderer font) {
      net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, width, height, -1, font);
      if (false && !p_renderTooltip_1_.isEmpty()) {
         RenderSystem.disableRescaleNormal();
         RenderSystem.disableDepthTest();
         int i = 0;

         for(String s : p_renderTooltip_1_) {
            int j = this.font.getStringWidth(s);
            if (j > i) {
               i = j;
            }
         }

         int l1 = p_renderTooltip_2_ + 12;
         int i2 = p_renderTooltip_3_ - 12;
         int k = 8;
         if (p_renderTooltip_1_.size() > 1) {
            k += 2 + (p_renderTooltip_1_.size() - 1) * 10;
         }

         if (l1 + i > this.width) {
            l1 -= 28 + i;
         }

         if (i2 + k + 6 > this.height) {
            i2 = this.height - k - 6;
         }

         this.setBlitOffset(300);
         this.itemRenderer.zLevel = 300.0F;
         int l = -267386864;
         this.fillGradient(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
         this.fillGradient(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
         this.fillGradient(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
         this.fillGradient(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
         this.fillGradient(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
         int i1 = 1347420415;
         int j1 = 1344798847;
         this.fillGradient(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
         this.fillGradient(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
         this.fillGradient(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
         this.fillGradient(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
         MatrixStack matrixstack = new MatrixStack();
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
         matrixstack.translate(0.0D, 0.0D, (double)this.itemRenderer.zLevel);
         Matrix4f matrix4f = matrixstack.getLast().getMatrix();

         for(int k1 = 0; k1 < p_renderTooltip_1_.size(); ++k1) {
            String s1 = p_renderTooltip_1_.get(k1);
            if (s1 != null) {
               this.font.renderString(s1, (float)l1, (float)i2, -1, true, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
            }

            if (k1 == 0) {
               i2 += 2;
            }

            i2 += 10;
         }

         irendertypebuffer$impl.finish();
         this.setBlitOffset(0);
         this.itemRenderer.zLevel = 0.0F;
         RenderSystem.enableDepthTest();
         RenderSystem.enableRescaleNormal();
      }
   }

   protected void renderComponentHoverEffect(ITextComponent p_renderComponentHoverEffect_1_, int p_renderComponentHoverEffect_2_, int p_renderComponentHoverEffect_3_) {
      if (p_renderComponentHoverEffect_1_ != null && p_renderComponentHoverEffect_1_.getStyle().getHoverEvent() != null) {
         HoverEvent hoverevent = p_renderComponentHoverEffect_1_.getStyle().getHoverEvent();
         if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemstack = ItemStack.EMPTY;

            try {
               INBT inbt = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
               if (inbt instanceof CompoundNBT) {
                  itemstack = ItemStack.read((CompoundNBT)inbt);
               }
            } catch (CommandSyntaxException var10) {
               ;
            }

            if (itemstack.isEmpty()) {
               this.renderTooltip(TextFormatting.RED + "Invalid Item!", p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
            } else {
               this.renderTooltip(itemstack, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.gameSettings.advancedItemTooltips) {
               try {
                  CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
                  List<String> list = Lists.newArrayList();
                  ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("name"));
                  if (itextcomponent != null) {
                     list.add(itextcomponent.getFormattedText());
                  }

                  if (compoundnbt.contains("type", 8)) {
                     String s = compoundnbt.getString("type");
                     list.add("Type: " + s);
                  }

                  list.add(compoundnbt.getString("id"));
                  this.renderTooltip(list, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
               } catch (CommandSyntaxException | JsonSyntaxException var9) {
                  this.renderTooltip(TextFormatting.RED + "Invalid Entity!", p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
               }
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.fontRenderer.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
         }

      }
   }

   protected void insertText(String p_insertText_1_, boolean p_insertText_2_) {
   }

   public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
      if (p_handleComponentClicked_1_ == null) {
         return false;
      } else {
         ClickEvent clickevent = p_handleComponentClicked_1_.getStyle().getClickEvent();
         if (hasShiftDown()) {
            if (p_handleComponentClicked_1_.getStyle().getInsertion() != null) {
               this.insertText(p_handleComponentClicked_1_.getStyle().getInsertion(), false);
            }
         } else if (clickevent != null) {
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.gameSettings.chatLinks) {
                  return false;
               }

               try {
                  URI uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.gameSettings.chatLinksPrompt) {
                     this.clickedLink = uri;
                     this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::confirmLink, clickevent.getValue(), false));
                  } else {
                     this.openLink(uri);
                  }
               } catch (URISyntaxException urisyntaxexception) {
                  LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI uri1 = (new File(clickevent.getValue())).toURI();
               this.openLink(uri1);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(clickevent.getValue(), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendMessage(clickevent.getValue(), false);
            } else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardListener.setClipboardString(clickevent.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", (Object)clickevent);
            }

            return true;
         }

         return false;
      }
   }

   public void sendMessage(String p_sendMessage_1_) {
      this.sendMessage(p_sendMessage_1_, true);
   }

   public void sendMessage(String p_sendMessage_1_, boolean p_sendMessage_2_) {
      p_sendMessage_1_ = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(p_sendMessage_1_);
      if (p_sendMessage_1_.isEmpty()) return;
      if (p_sendMessage_2_) {
         this.minecraft.ingameGUI.getChatGUI().addToSentMessages(p_sendMessage_1_);
      }
      //if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.player, msg) != 0) return; //Forge: TODO Client command re-write

      this.minecraft.player.sendChatMessage(p_sendMessage_1_);
   }

   public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
      this.minecraft = p_init_1_;
      this.itemRenderer = p_init_1_.getItemRenderer();
      this.font = p_init_1_.fontRenderer;
      this.width = p_init_2_;
      this.height = p_init_3_;
      java.util.function.Consumer<Widget> remove = (b) -> { buttons.remove(b); children.remove(b); };
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttons, this::addButton, remove))) {
      this.buttons.clear();
      this.children.clear();
      this.setFocused((IGuiEventListener)null);
      this.init();
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttons, this::addButton, remove));
   }

   public void setSize(int p_setSize_1_, int p_setSize_2_) {
      this.width = p_setSize_1_;
      this.height = p_setSize_2_;
   }

   public List<? extends IGuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground() {
      this.renderBackground(0);
   }

   public void renderBackground(int p_renderBackground_1_) {
      if (this.minecraft.world != null) {
         this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
      } else {
         this.renderDirtBackground(p_renderBackground_1_);
      }

   }

   public void renderDirtBackground(int p_renderDirtBackground_1_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0F, (float)this.height / 32.0F + (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
      tessellator.draw();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean p_confirmLink_1_) {
      if (p_confirmLink_1_) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.displayGuiScreen(this);
   }

   private void openLink(URI p_openLink_1_) {
      Util.getOSType().openURI(p_openLink_1_);
   }

   public static boolean hasControlDown() {
      if (Minecraft.IS_RUNNING_ON_MAC) {
         return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 343) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 347);
      } else {
         return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 341) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
   }

   public static boolean hasAltDown() {
      return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 346);
   }

   public static boolean isCut(int p_isCut_0_) {
      return p_isCut_0_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int p_isPaste_0_) {
      return p_isPaste_0_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int p_isCopy_0_) {
      return p_isCopy_0_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int p_isSelectAll_0_) {
      return p_isSelectAll_0_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
   }

   public static void wrapScreenError(Runnable p_wrapScreenError_0_, String p_wrapScreenError_1_, String p_wrapScreenError_2_) {
      try {
         p_wrapScreenError_0_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, p_wrapScreenError_1_);
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
         crashreportcategory.addDetail("Screen name", () -> {
            return p_wrapScreenError_2_;
         });
         throw new ReportedException(crashreport);
      }
   }

   protected boolean isValidCharacterForName(String p_isValidCharacterForName_1_, char p_isValidCharacterForName_2_, int p_isValidCharacterForName_3_) {
      int i = p_isValidCharacterForName_1_.indexOf(58);
      int j = p_isValidCharacterForName_1_.indexOf(47);
      if (p_isValidCharacterForName_2_ == ':') {
         return (j == -1 || p_isValidCharacterForName_3_ <= j) && i == -1;
      } else if (p_isValidCharacterForName_2_ == '/') {
         return p_isValidCharacterForName_3_ > i;
      } else {
         return p_isValidCharacterForName_2_ == '_' || p_isValidCharacterForName_2_ == '-' || p_isValidCharacterForName_2_ >= 'a' && p_isValidCharacterForName_2_ <= 'z' || p_isValidCharacterForName_2_ >= '0' && p_isValidCharacterForName_2_ <= '9' || p_isValidCharacterForName_2_ == '.';
      }
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return true;
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }
}