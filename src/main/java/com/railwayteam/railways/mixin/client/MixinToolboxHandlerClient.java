package com.railwayteam.railways.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.toolbox.CustomRadialToolboxMenu;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxHolder;
import com.railwayteam.railways.mixin_interfaces.IMountedToolboxHandler;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandlerClient;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.simibubi.create.content.curiosities.toolbox.RadialToolboxMenu.State;
import static com.simibubi.create.foundation.gui.AllGuiTextures.*;

@Mixin(ToolboxHandlerClient.class)
public class MixinToolboxHandlerClient {

  @Shadow(remap = false)
  static int COOLDOWN;

  /**
   * @author Slimeist
   * @reason feat:(Mounted Conductor Toolboxes), Complex enough that mixins for every line would be basically necessary
   */
  @Overwrite(remap = false)
  public static void onKeyInput(int key, boolean pressed) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
      return;

    if (key != AllKeys.TOOLBELT.getBoundCode())
      return;
    if (COOLDOWN > 0)
      return;
    LocalPlayer player = mc.player;
    if (player == null)
      return;
    Level level = player.level;

    //List<ToolboxTileEntity> toolboxes = ToolboxHandler.getNearest(player.level, player, 8);
    List<Object> toolbox_like = IMountedToolboxHandler.getNearest(player.level, player, 8); //NOTE: changed here
    List<ToolboxTileEntity> toolboxes = toolbox_like.stream()
        .filter((obj) -> obj.getClass() == ToolboxTileEntity.class)
        .map((obj) -> ((ToolboxTileEntity) obj))
        .collect(Collectors.toList());

    List<ConductorEntity> conductors = toolbox_like.stream()
        .filter((obj) -> obj.getClass() == ConductorEntity.class && ((ConductorEntity) obj).isCarryingToolbox())
        .map((obj) -> ((ConductorEntity) obj))
        .toList();

    toolbox_like.sort(Comparator.comparing((obj) -> {
      if (obj.getClass() == ToolboxTileEntity.class) {
        return ((ToolboxTileEntity) obj).getUniqueId();
      } else if (obj.getClass() == ConductorEntity.class) {
        ConductorEntity ce = (ConductorEntity) obj;
        if (ce.isCarryingToolbox()) {
          return ce.getToolboxHolder().getUniqueId();
        }
      }
      return UUID.randomUUID();
    })); //NOTE: changed here

    CompoundTag compound = player.getPersistentData()
        .getCompound("CreateToolboxData");

    String slotKey = String.valueOf(player.getInventory().selected);
    boolean equipped = compound.contains(slotKey);

    if (equipped) {
      if (compound.getCompound(slotKey).hasUUID("EntityUUID")) { //NOTE: changed here
        UUID uuid = compound.getCompound(slotKey).getUUID("EntityUUID");
        double max = IMountedToolboxHandler.getMaxRange(player);
        List<ConductorEntity> entities = level.getEntitiesOfClass(ConductorEntity.class, AABB.ofSize(player.position(), 19, 19, 19))
            .stream()
            .filter((entity) -> entity != null && entity.getUUID().equals(uuid)).toList();
        ConductorEntity ce;
        if (!entities.isEmpty() && (ce = entities.get(0)).isCarryingToolbox()) {
          boolean canReachToolbox = IMountedToolboxHandler.distance(player.position(), ce.position()) < max * max;
          if (canReachToolbox) {
            MountedToolboxHolder holder = ce.getToolboxHolder();
            CustomRadialToolboxMenu screen = new CustomRadialToolboxMenu(toolboxes, conductors,
                State.SELECT_ITEM_UNEQUIP, (holder == null ? null : holder.getParent()));
            screen.prevSlot(compound.getCompound(slotKey)
                .getInt("Slot"));
            ScreenOpener.open(screen);
            return;
          }
        }

        ScreenOpener.open(new CustomRadialToolboxMenu(ImmutableList.of(), State.DETACH, null));
        return;
      } else {
        BlockPos pos = NbtUtils.readBlockPos(compound.getCompound(slotKey)
            .getCompound("Pos"));
        double max = ToolboxHandler.getMaxRange(player);
        boolean canReachToolbox = ToolboxHandler.distance(player.position(), pos) < max * max;

        if (canReachToolbox) {
          BlockEntity blockEntity = level.getBlockEntity(pos);
          if (blockEntity instanceof ToolboxTileEntity) {
            CustomRadialToolboxMenu screen = new CustomRadialToolboxMenu(toolboxes, conductors,
                State.SELECT_ITEM_UNEQUIP, (ToolboxTileEntity) blockEntity);
            screen.prevSlot(compound.getCompound(slotKey)
                .getInt("Slot"));
            ScreenOpener.open(screen);
            return;
          }
        }

        ScreenOpener.open(new CustomRadialToolboxMenu(ImmutableList.of(), State.DETACH, null));
        return;
      }
    }

    if (toolbox_like.isEmpty())
      return;

    if (toolboxes.size() == 1 && conductors.size() == 0)
      ScreenOpener.open(new CustomRadialToolboxMenu(toolboxes, conductors, State.SELECT_ITEM, toolboxes.get(0)));
    else if (toolboxes.size() == 0 && conductors.size() == 1)
      ScreenOpener.open(new CustomRadialToolboxMenu(toolboxes, conductors, State.SELECT_ITEM, conductors.get(0)));
    else
      ScreenOpener.open(new CustomRadialToolboxMenu(toolboxes, conductors, State.SELECT_BOX, (ToolboxTileEntity) null));
  }

  @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", remap = true), //remap=true is ESSENTIAL
      method = "renderOverlay", locals = LocalCapture.CAPTURE_FAILHARD, remap = false, cancellable = true, require = 1)
  private static void inj$renderOverlay(ForgeGui gui, PoseStack poseStack, float partialTicks, int width, int height, CallbackInfo ci, Minecraft mc, int x, int y, Player player, CompoundTag persistentData, CompoundTag compound) {
    poseStack.pushPose();
    for (int slot = 0; slot < 9; slot++) {
      String key = String.valueOf(slot);
      if (!compound.contains(key))
        continue;
      if (compound.getCompound(key).hasUUID("EntityUUID")) {
        double max = IMountedToolboxHandler.getMaxRange(player);
        boolean selected = player.getInventory().selected == slot;
        int offset = selected ? 1 : 0;

        UUID uuid = compound.getCompound(key).getUUID("EntityUUID");
        List<ConductorEntity> entities = player.level.getEntitiesOfClass(ConductorEntity.class, AABB.ofSize(player.position(), 19, 19, 19))
            .stream()
            .filter((entity) -> entity != null && entity.getUUID().equals(uuid)).toList();
        ConductorEntity ce;
        boolean inRange = false;
        if (!entities.isEmpty() && (ce = entities.get(0)).isCarryingToolbox()) {
          inRange = IMountedToolboxHandler.distance(player.position(), ce.position()) < max * max;
        }
        AllGuiTextures texture = inRange
            ? selected ? TOOLBELT_SELECTED_ON : TOOLBELT_HOTBAR_ON
            : selected ? TOOLBELT_SELECTED_OFF : TOOLBELT_HOTBAR_OFF;
        texture.render(poseStack, x + 20 * slot - offset, y + offset);
      } else {
        BlockPos pos = NbtUtils.readBlockPos(compound.getCompound(key)
            .getCompound("Pos"));
        double max = ToolboxHandler.getMaxRange(player);
        boolean selected = player.getInventory().selected == slot;
        int offset = selected ? 1 : 0;
        AllGuiTextures texture = ToolboxHandler.distance(player.position(), pos) < max * max
            ? selected ? TOOLBELT_SELECTED_ON : TOOLBELT_HOTBAR_ON
            : selected ? TOOLBELT_SELECTED_OFF : TOOLBELT_HOTBAR_OFF;
        texture.render(poseStack, x + 20 * slot - offset, y + offset);
      }
    }
    poseStack.popPose();
    ci.cancel();
  }
}
