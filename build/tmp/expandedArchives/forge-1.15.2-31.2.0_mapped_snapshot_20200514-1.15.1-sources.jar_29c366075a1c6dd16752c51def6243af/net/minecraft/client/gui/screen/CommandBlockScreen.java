package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandBlockScreen extends AbstractCommandBlockScreen {
   private final CommandBlockTileEntity commandBlock;
   private Button modeBtn;
   private Button conditionalBtn;
   private Button autoExecBtn;
   private CommandBlockTileEntity.Mode commandBlockMode = CommandBlockTileEntity.Mode.REDSTONE;
   private boolean conditional;
   private boolean automatic;

   public CommandBlockScreen(CommandBlockTileEntity commandBlockIn) {
      this.commandBlock = commandBlockIn;
   }

   CommandBlockLogic getLogic() {
      return this.commandBlock.getCommandBlockLogic();
   }

   int func_195236_i() {
      return 135;
   }

   protected void init() {
      super.init();
      this.modeBtn = this.addButton(new Button(this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.format("advMode.mode.sequence"), (p_214191_1_) -> {
         this.nextMode();
         this.updateMode();
      }));
      this.conditionalBtn = this.addButton(new Button(this.width / 2 - 50, 165, 100, 20, I18n.format("advMode.mode.unconditional"), (p_214190_1_) -> {
         this.conditional = !this.conditional;
         this.updateConditional();
      }));
      this.autoExecBtn = this.addButton(new Button(this.width / 2 + 50 + 4, 165, 100, 20, I18n.format("advMode.mode.redstoneTriggered"), (p_214189_1_) -> {
         this.automatic = !this.automatic;
         this.updateAutoExec();
      }));
      this.doneButton.active = false;
      this.trackOutputButton.active = false;
      this.modeBtn.active = false;
      this.conditionalBtn.active = false;
      this.autoExecBtn.active = false;
   }

   public void updateGui() {
      CommandBlockLogic commandblocklogic = this.commandBlock.getCommandBlockLogic();
      this.commandTextField.setText(commandblocklogic.getCommand());
      this.field_195238_s = commandblocklogic.shouldTrackOutput();
      this.commandBlockMode = this.commandBlock.getMode();
      this.conditional = this.commandBlock.isConditional();
      this.automatic = this.commandBlock.isAuto();
      this.updateTrackOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoExec();
      this.doneButton.active = true;
      this.trackOutputButton.active = true;
      this.modeBtn.active = true;
      this.conditionalBtn.active = true;
      this.autoExecBtn.active = true;
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
      this.updateTrackOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoExec();
      this.doneButton.active = true;
      this.trackOutputButton.active = true;
      this.modeBtn.active = true;
      this.conditionalBtn.active = true;
      this.autoExecBtn.active = true;
   }

   protected void func_195235_a(CommandBlockLogic commandBlockLogicIn) {
      this.minecraft.getConnection().sendPacket(new CUpdateCommandBlockPacket(new BlockPos(commandBlockLogicIn.getPositionVector()), this.commandTextField.getText(), this.commandBlockMode, commandBlockLogicIn.shouldTrackOutput(), this.conditional, this.automatic));
   }

   private void updateMode() {
      switch(this.commandBlockMode) {
      case SEQUENCE:
         this.modeBtn.setMessage(I18n.format("advMode.mode.sequence"));
         break;
      case AUTO:
         this.modeBtn.setMessage(I18n.format("advMode.mode.auto"));
         break;
      case REDSTONE:
         this.modeBtn.setMessage(I18n.format("advMode.mode.redstone"));
      }

   }

   private void nextMode() {
      switch(this.commandBlockMode) {
      case SEQUENCE:
         this.commandBlockMode = CommandBlockTileEntity.Mode.AUTO;
         break;
      case AUTO:
         this.commandBlockMode = CommandBlockTileEntity.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.commandBlockMode = CommandBlockTileEntity.Mode.SEQUENCE;
      }

   }

   private void updateConditional() {
      if (this.conditional) {
         this.conditionalBtn.setMessage(I18n.format("advMode.mode.conditional"));
      } else {
         this.conditionalBtn.setMessage(I18n.format("advMode.mode.unconditional"));
      }

   }

   private void updateAutoExec() {
      if (this.automatic) {
         this.autoExecBtn.setMessage(I18n.format("advMode.mode.autoexec.bat"));
      } else {
         this.autoExecBtn.setMessage(I18n.format("advMode.mode.redstoneTriggered"));
      }

   }
}