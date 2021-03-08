package com.railwayteam.railways.items;

import com.railwayteam.railways.Containers;
import com.railwayteam.railways.StationListContainer;
import com.railwayteam.railways.blocks.StationSensorRailTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class StationEditorItem extends Item implements INamedContainerProvider {
  public static final String NAME = "station_editor_tool";

  public StationEditorItem (Properties props) {
    super (props);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    if (!world.isRemote) {
      NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> {
        // ze goggles
      });
      return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    if (context.getWorld().getTileEntity(context.getPos()) instanceof StationSensorRailTileEntity) {
      return editStation(context);
    }
    else return super.onItemUse(context);
  }

  private ActionResultType editStation (ItemUseContext context) {
    PlayerEntity player = context.getPlayer();
    if (player != null) {
      World world = context.getWorld();
      BlockPos pos = context.getPos();
      if (world.isRemote()) return ActionResultType.SUCCESS;

    //  player.sendMessage(new StringTextComponent("opened menu? " + valid));
      StationSensorRailTileEntity te = (StationSensorRailTileEntity) world.getTileEntity(pos);
      String candidate = player.getDisplayName().getFormattedText();
      if (player.isSneaking()) {
        player.sendMessage(new StringTextComponent("cleared station"));
        te.setStation("");
      } else {
        if (te.getStation().equals(candidate)) {
          player.sendMessage(new StringTextComponent("station already assigned"));
        } else {
          player.sendMessage(new StringTextComponent("assigned station: " + candidate));
          te.setStation(candidate);
        }
      }
      return ActionResultType.SUCCESS;
    }
    return super.onItemUse(context);
  }

  @Override
  public ITextComponent getDisplayName () {
    return new StringTextComponent(getTranslationKey());
  }

  @Override
  public Container createMenu (int id, PlayerInventory inv, PlayerEntity player) {
  //  player.sendMessage(new StringTextComponent("Trying to create menu"));
    return new StationListContainer(Containers.SCHEDULE.type, id, inv);
  }
}
