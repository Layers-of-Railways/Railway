package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryScreen extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener {
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   /** The old x position of the mouse pointer */
   private float oldMouseX;
   /** The old y position of the mouse pointer */
   private float oldMouseY;
   private final RecipeBookGui recipeBookGui = new RecipeBookGui();
   private boolean removeRecipeBookGui;
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(PlayerEntity player) {
      super(player.container, player.inventory, new TranslationTextComponent("container.crafting"));
      this.passEvents = true;
   }

   public void tick() {
      if (this.minecraft.playerController.isInCreativeMode()) {
         this.minecraft.displayGuiScreen(new CreativeScreen(this.minecraft.player));
      } else {
         this.recipeBookGui.tick();
      }
   }

   protected void init() {
      if (this.minecraft.playerController.isInCreativeMode()) {
         this.minecraft.displayGuiScreen(new CreativeScreen(this.minecraft.player));
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
         this.removeRecipeBookGui = true;
         this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
         this.children.add(this.recipeBookGui);
         this.setFocusedDefault(this.recipeBookGui);
         this.addButton(new ImageButton(this.guiLeft + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (p_214086_1_) -> {
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            ((ImageButton)p_214086_1_).setPosition(this.guiLeft + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
      }
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.title.getFormattedText(), 97.0F, 8.0F, 4210752);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.hasActivePotionEffects = !this.recipeBookGui.isVisible();
      if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
         this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
         this.recipeBookGui.render(p_render_1_, p_render_2_, p_render_3_);
      } else {
         this.recipeBookGui.render(p_render_1_, p_render_2_, p_render_3_);
         super.render(p_render_1_, p_render_2_, p_render_3_);
         this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, p_render_3_);
      }

      this.renderHoveredToolTip(p_render_1_, p_render_2_);
      this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, p_render_1_, p_render_2_);
      this.oldMouseX = (float)p_render_1_;
      this.oldMouseY = (float)p_render_2_;
      this.func_212932_b(this.recipeBookGui);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.minecraft.player);
   }

   public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity p_228187_5_) {
      float f = (float)Math.atan((double)(mouseX / 40.0F));
      float f1 = (float)Math.atan((double)(mouseY / 40.0F));
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.translate(0.0D, 0.0D, 1000.0D);
      matrixstack.scale((float)scale, (float)scale, (float)scale);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
      quaternion.multiply(quaternion1);
      matrixstack.rotate(quaternion);
      float f2 = p_228187_5_.renderYawOffset;
      float f3 = p_228187_5_.rotationYaw;
      float f4 = p_228187_5_.rotationPitch;
      float f5 = p_228187_5_.prevRotationYawHead;
      float f6 = p_228187_5_.rotationYawHead;
      p_228187_5_.renderYawOffset = 180.0F + f * 20.0F;
      p_228187_5_.rotationYaw = 180.0F + f * 40.0F;
      p_228187_5_.rotationPitch = -f1 * 20.0F;
      p_228187_5_.rotationYawHead = p_228187_5_.rotationYaw;
      p_228187_5_.prevRotationYawHead = p_228187_5_.rotationYaw;
      EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
      quaternion1.conjugate();
      entityrenderermanager.setCameraOrientation(quaternion1);
      entityrenderermanager.setRenderShadow(false);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
      entityrenderermanager.renderEntityStatic(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
      irendertypebuffer$impl.finish();
      entityrenderermanager.setRenderShadow(true);
      p_228187_5_.renderYawOffset = f2;
      p_228187_5_.rotationYaw = f3;
      p_228187_5_.rotationPitch = f4;
      p_228187_5_.prevRotationYawHead = f5;
      p_228187_5_.rotationYawHead = f6;
      RenderSystem.popMatrix();
   }

   protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
      return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(x, y, width, height, mouseX, mouseY);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookGui.isVisible() ? false : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }

   protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
      boolean flag = mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
      return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
      super.handleMouseClick(slotIn, slotId, mouseButton, type);
      this.recipeBookGui.slotClicked(slotIn);
   }

   public void recipesUpdated() {
      this.recipeBookGui.recipesUpdated();
   }

   public void removed() {
      if (this.removeRecipeBookGui) {
         this.recipeBookGui.removed();
      }

      super.removed();
   }

   public RecipeBookGui getRecipeGui() {
      return this.recipeBookGui;
   }
}