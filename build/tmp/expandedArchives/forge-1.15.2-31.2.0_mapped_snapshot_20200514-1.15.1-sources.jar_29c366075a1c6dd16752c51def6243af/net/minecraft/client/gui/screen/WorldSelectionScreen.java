package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionScreen extends Screen {
   protected final Screen prevScreen;
   private String worldVersTooltip;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   protected TextFieldWidget field_212352_g;
   private WorldSelectionList selectionList;

   public WorldSelectionScreen(Screen screenIn) {
      super(new TranslationTextComponent("selectWorld.title"));
      this.prevScreen = screenIn;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public void tick() {
      this.field_212352_g.tick();
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.field_212352_g = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.field_212352_g, I18n.format("selectWorld.search"));
      this.field_212352_g.setResponder((p_214329_1_) -> {
         this.selectionList.func_212330_a(() -> {
            return p_214329_1_;
         }, false);
      });
      this.selectionList = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.field_212352_g.getText();
      }, this.selectionList);
      this.children.add(this.field_212352_g);
      this.children.add(this.selectionList);
      this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select"), (p_214325_1_) -> {
         this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214438_a);
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create"), (p_214326_1_) -> {
         this.minecraft.displayGuiScreen(new CreateWorldScreen(this));
      }));
      this.renameButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.edit"), (p_214323_1_) -> {
         this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214444_c);
      }));
      this.deleteButton = this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete"), (p_214330_1_) -> {
         this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214442_b);
      }));
      this.copyButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate"), (p_214328_1_) -> {
         this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214445_d);
      }));
      this.addButton(new Button(this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel"), (p_214327_1_) -> {
         this.minecraft.displayGuiScreen(this.prevScreen);
      }));
      this.func_214324_a(false);
      this.setFocusedDefault(this.field_212352_g);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.field_212352_g.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public void onClose() {
      this.minecraft.displayGuiScreen(this.prevScreen);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.field_212352_g.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.worldVersTooltip = null;
      this.selectionList.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_212352_g.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.worldVersTooltip != null) {
         this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), p_render_1_, p_render_2_);
      }

   }

   /**
    * Called back by selectionList when we call its drawScreen method, from ours.
    */
   public void setVersionTooltip(String p_184861_1_) {
      this.worldVersTooltip = p_184861_1_;
   }

   public void func_214324_a(boolean p_214324_1_) {
      this.selectButton.active = p_214324_1_;
      this.deleteButton.active = p_214324_1_;
      this.renameButton.active = p_214324_1_;
      this.copyButton.active = p_214324_1_;
   }

   public void removed() {
      if (this.selectionList != null) {
         this.selectionList.children().forEach(WorldSelectionList.Entry::close);
      }

   }
}