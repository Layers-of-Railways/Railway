package net.minecraft.client.gui.screen;

import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditMinecartCommandBlockScreen extends AbstractCommandBlockScreen {
   private final CommandBlockLogic commandBlockLogic;

   public EditMinecartCommandBlockScreen(CommandBlockLogic p_i46595_1_) {
      this.commandBlockLogic = p_i46595_1_;
   }

   public CommandBlockLogic getLogic() {
      return this.commandBlockLogic;
   }

   int func_195236_i() {
      return 150;
   }

   protected void init() {
      super.init();
      this.field_195238_s = this.getLogic().shouldTrackOutput();
      this.updateTrackOutput();
      this.commandTextField.setText(this.getLogic().getCommand());
   }

   protected void func_195235_a(CommandBlockLogic commandBlockLogicIn) {
      if (commandBlockLogicIn instanceof MinecartCommandBlockEntity.MinecartCommandLogic) {
         MinecartCommandBlockEntity.MinecartCommandLogic minecartcommandblockentity$minecartcommandlogic = (MinecartCommandBlockEntity.MinecartCommandLogic)commandBlockLogicIn;
         this.minecraft.getConnection().sendPacket(new CUpdateMinecartCommandBlockPacket(minecartcommandblockentity$minecartcommandlogic.getMinecart().getEntityId(), this.commandTextField.getText(), commandBlockLogicIn.shouldTrackOutput()));
      }

   }
}