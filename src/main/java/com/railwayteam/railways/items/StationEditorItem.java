package com.railwayteam.railways.items;

import com.railwayteam.railways.Containers;
import com.railwayteam.railways.StationListContainer;
import com.railwayteam.railways.blocks.StationSensorRailTileEntity;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;

public class StationEditorItem extends Item implements INamedContainerProvider {
  public static final String NAME = "station_editor_tool";

  private static final StringTextComponent MSG_ADD_SUCCESS = new StringTextComponent("added station to list");
  private static final StringTextComponent MSG_ADD_EXISTS  = new StringTextComponent("station already in list");

  private ArrayList<StationLocation> list;

  public StationEditorItem (Properties props) {
    super (props);
    list = new ArrayList<StationLocation>();
  }

  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    if (!world.isRemote) {
    //  player.sendMessage(new StringTextComponent("opened menu from nothing"));
      NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> {
        if (list.isEmpty()) {
          buf.writeInt(0);
        } else {
          buf.writeInt(list.size());
          for (StationLocation loc : list) buf.writeString(loc.name);
        }
      });
      return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
    return super.onItemRightClick(world, player, hand);
  } // */

  /*
  @SubscribeEvent
  public void handleInteractionWithMinecart (PlayerInteractEvent.EntityInteract ei) {
    ei.getPlayer().sendMessage(new StringTextComponent("opened menu from minecart"));
    if (ei.getWorld().isRemote) return;
    NetworkHooks.openGui((ServerPlayerEntity)ei.getPlayer(), this, buf -> {
      ei.getTarget().getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
        buf.writeInt( ((StationListCapability)capability).length() );
        Iterator<String> list = ((StationListCapability)capability).iterate();
        while (list.hasNext()) buf.writeString(list.next());
      });
    });
  } // */

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    if (context.getWorld().isRemote()) return ActionResultType.PASS;
    if (context.getWorld().getTileEntity(context.getPos()) instanceof StationSensorRailTileEntity) {
      boolean found = false;
      for (StationLocation loc : list) {
        if (loc.isAt(context.getPos())) {
          found = true;
          context.getPlayer().sendMessage(MSG_ADD_EXISTS);
          break;
        }
      }
      if (!found) {
        list.add(new StationLocation(context.getPos()));
        context.getPlayer().sendMessage(MSG_ADD_SUCCESS);
      }
      return ActionResultType.CONSUME;
    }
    else return super.onItemUse(context);
  } // */

  /*
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
  } // */

  public void updateStationList (ArrayList<String> updatedList) {
    ArrayList<StationLocation> update = new ArrayList<StationLocation>();
    for (StationLocation loc : list) {
      if (updatedList.contains(loc.printCoords())) {
        update.add(loc);
      }
    }
    //list = update;
  }

  @Override
  public ITextComponent getDisplayName () {
    return new StringTextComponent(getTranslationKey());
  }

  @Override
  public Container createMenu (int id, PlayerInventory inv, PlayerEntity player) {
  //  player.sendMessage(new StringTextComponent("Trying to create menu"));
    return new StationListContainer(id, inv, list); //, inv);
  }

}
