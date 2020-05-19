package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JigsawScreen extends Screen {
   private final JigsawTileEntity field_214259_a;
   private TextFieldWidget field_214260_b;
   private TextFieldWidget field_214261_c;
   private TextFieldWidget field_214262_d;
   private Button field_214263_e;

   public JigsawScreen(JigsawTileEntity p_i51083_1_) {
      super(NarratorChatListener.EMPTY);
      this.field_214259_a = p_i51083_1_;
   }

   public void tick() {
      this.field_214260_b.tick();
      this.field_214261_c.tick();
      this.field_214262_d.tick();
   }

   private void func_214256_b() {
      this.func_214258_d();
      this.minecraft.displayGuiScreen((Screen)null);
   }

   private void func_214257_c() {
      this.minecraft.displayGuiScreen((Screen)null);
   }

   private void func_214258_d() {
      this.minecraft.getConnection().sendPacket(new CUpdateJigsawBlockPacket(this.field_214259_a.getPos(), new ResourceLocation(this.field_214260_b.getText()), new ResourceLocation(this.field_214261_c.getText()), this.field_214262_d.getText()));
   }

   public void onClose() {
      this.func_214257_c();
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.field_214263_e = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.format("gui.done"), (p_214255_1_) -> {
         this.func_214256_b();
      }));
      this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.format("gui.cancel"), (p_214252_1_) -> {
         this.func_214257_c();
      }));
      this.field_214261_c = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.format("jigsaw_block.target_pool"));
      this.field_214261_c.setMaxStringLength(128);
      this.field_214261_c.setText(this.field_214259_a.getTargetPool().toString());
      this.field_214261_c.setResponder((p_214254_1_) -> {
         this.func_214253_a();
      });
      this.children.add(this.field_214261_c);
      this.field_214260_b = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 300, 20, I18n.format("jigsaw_block.attachement_type"));
      this.field_214260_b.setMaxStringLength(128);
      this.field_214260_b.setText(this.field_214259_a.getAttachmentType().toString());
      this.field_214260_b.setResponder((p_214251_1_) -> {
         this.func_214253_a();
      });
      this.children.add(this.field_214260_b);
      this.field_214262_d = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 300, 20, I18n.format("jigsaw_block.final_state"));
      this.field_214262_d.setMaxStringLength(256);
      this.field_214262_d.setText(this.field_214259_a.getFinalState());
      this.children.add(this.field_214262_d);
      this.setFocusedDefault(this.field_214261_c);
      this.func_214253_a();
   }

   protected void func_214253_a() {
      this.field_214263_e.active = ResourceLocation.isResouceNameValid(this.field_214260_b.getText()) & ResourceLocation.isResouceNameValid(this.field_214261_c.getText());
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.field_214260_b.getText();
      String s1 = this.field_214261_c.getText();
      String s2 = this.field_214262_d.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.field_214260_b.setText(s);
      this.field_214261_c.setText(s1);
      this.field_214262_d.setText(s2);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (!this.field_214263_e.active || p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.func_214256_b();
         return true;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawString(this.font, I18n.format("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
      this.field_214261_c.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawString(this.font, I18n.format("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
      this.field_214260_b.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawString(this.font, I18n.format("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
      this.field_214262_d.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}