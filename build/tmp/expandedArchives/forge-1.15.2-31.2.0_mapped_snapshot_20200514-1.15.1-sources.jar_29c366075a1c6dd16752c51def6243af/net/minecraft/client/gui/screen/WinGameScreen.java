package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WinGameScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation field_194401_g = new ResourceLocation("textures/gui/title/edition.png");
   private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
   private final boolean poem;
   private final Runnable onFinished;
   private float time;
   private List<String> lines;
   private int totalScrollLength;
   private float scrollSpeed = 0.5F;

   public WinGameScreen(boolean poemIn, Runnable onFinishedIn) {
      super(NarratorChatListener.EMPTY);
      this.poem = poemIn;
      this.onFinished = onFinishedIn;
      if (!poemIn) {
         this.scrollSpeed = 0.75F;
      }

   }

   public void tick() {
      this.minecraft.getMusicTicker().tick();
      this.minecraft.getSoundHandler().tick(false);
      float f = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      if (this.time > f) {
         this.sendRespawnPacket();
      }

   }

   public void onClose() {
      this.sendRespawnPacket();
   }

   private void sendRespawnPacket() {
      this.onFinished.run();
      this.minecraft.displayGuiScreen((Screen)null);
   }

   protected void init() {
      if (this.lines == null) {
         this.lines = Lists.newArrayList();
         IResource iresource = null;

         try {
            String s = "" + TextFormatting.WHITE + TextFormatting.OBFUSCATED + TextFormatting.GREEN + TextFormatting.AQUA;
            int i = 274;
            if (this.poem) {
               iresource = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
               InputStream inputstream = iresource.getInputStream();
               BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
               Random random = new Random(8124371L);

               String s1;
               while((s1 = bufferedreader.readLine()) != null) {
                  String s2;
                  String s3;
                  for(s1 = s1.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername()); s1.contains(s); s1 = s2 + TextFormatting.WHITE + TextFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + s3) {
                     int j = s1.indexOf(s);
                     s2 = s1.substring(0, j);
                     s3 = s1.substring(j + s.length());
                  }

                  this.lines.addAll(this.minecraft.fontRenderer.listFormattedStringToWidth(s1, 274));
                  this.lines.add("");
               }

               inputstream.close();

               for(int k = 0; k < 8; ++k) {
                  this.lines.add("");
               }
            }

            InputStream inputstream1 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(inputstream1, StandardCharsets.UTF_8));

            String s4;
            while((s4 = bufferedreader1.readLine()) != null) {
               s4 = s4.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
               s4 = s4.replaceAll("\t", "    ");
               this.lines.addAll(this.minecraft.fontRenderer.listFormattedStringToWidth(s4, 274));
               this.lines.add("");
            }

            inputstream1.close();
            this.totalScrollLength = this.lines.size() * 12;
         } catch (Exception exception) {
            LOGGER.error("Couldn't load credits", (Throwable)exception);
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
         }

      }
   }

   private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_) {
      this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
      int i = this.width;
      float f = -this.time * 0.5F * this.scrollSpeed;
      float f1 = (float)this.height - this.time * 0.5F * this.scrollSpeed;
      float f2 = 0.015625F;
      float f3 = this.time * 0.02F;
      float f4 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      float f5 = (f4 - 20.0F - this.time) * 0.005F;
      if (f5 < f3) {
         f3 = f5;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      f3 = f3 * f3;
      f3 = f3 * 96.0F / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)this.height, (double)this.getBlitOffset()).tex(0.0F, f * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos((double)i, (double)this.height, (double)this.getBlitOffset()).tex((float)i * 0.015625F, f * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos((double)i, 0.0D, (double)this.getBlitOffset()).tex((float)i * 0.015625F, f1 * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.getBlitOffset()).tex(0.0F, f1 * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
      tessellator.draw();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.drawWinGameScreen(p_render_1_, p_render_2_, p_render_3_);
      int i = 274;
      int j = this.width / 2 - 137;
      int k = this.height + 50;
      this.time += p_render_3_;
      float f = -this.time * this.scrollSpeed;
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, f, 0.0F);
      this.minecraft.getTextureManager().bindTexture(MINECRAFT_LOGO);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableAlphaTest();
      this.blit(j, k, 0, 0, 155, 44);
      this.blit(j + 155, k, 0, 45, 155, 44);
      this.minecraft.getTextureManager().bindTexture(field_194401_g);
      blit(j + 88, k + 37, 0.0F, 0.0F, 98, 14, 128, 16);
      RenderSystem.disableAlphaTest();
      int l = k + 100;

      for(int i1 = 0; i1 < this.lines.size(); ++i1) {
         if (i1 == this.lines.size() - 1) {
            float f1 = (float)l + f - (float)(this.height / 2 - 6);
            if (f1 < 0.0F) {
               RenderSystem.translatef(0.0F, -f1, 0.0F);
            }
         }

         if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height) {
            String s = this.lines.get(i1);
            if (s.startsWith("[C]")) {
               this.font.drawStringWithShadow(s.substring(3), (float)(j + (274 - this.font.getStringWidth(s.substring(3))) / 2), (float)l, 16777215);
            } else {
               this.font.random.setSeed((long)((float)((long)i1 * 4238972211L) + this.time / 4.0F));
               this.font.drawStringWithShadow(s, (float)j, (float)l, 16777215);
            }
         }

         l += 12;
      }

      RenderSystem.popMatrix();
      this.minecraft.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      int j1 = this.width;
      int k1 = this.height;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)k1, (double)this.getBlitOffset()).tex(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)j1, (double)k1, (double)this.getBlitOffset()).tex(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)j1, 0.0D, (double)this.getBlitOffset()).tex(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.getBlitOffset()).tex(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
      RenderSystem.disableBlend();
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}