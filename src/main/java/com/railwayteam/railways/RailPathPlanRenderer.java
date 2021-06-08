package com.railwayteam.railways;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.railwayteam.railways.blocks.WayPointBlock;
import com.railwayteam.railways.items.WayPointToolItem;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber
public class RailPathPlanRenderer {

  private static final float X0 = 0.5f;
  private static final float Y0 = 0.5f;
  private static final float Z0 = 0.5f;
  private static final Color   VALID = Color.GREEN;
  private static final Color INVALID = Color.RED;

  @SubscribeEvent
  public static void onDrawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
    if ( !(event.getInfo().getRenderViewEntity() instanceof PlayerEntity) ) return;
    PlayerEntity viewer = (PlayerEntity)event.getInfo().getRenderViewEntity();
    World world = viewer.world;
    BlockPos eventPos = event.getTarget().getPos();
    if ( !(world.getBlockState(eventPos).getBlock() instanceof WayPointBlock) ) return;
    if ( !viewer.getHeldItem(Hand.MAIN_HAND).getItem().getName().equals(ModSetup.R_ITEM_WAYPOINT_TOOL.get().getName()) ) return;
    ItemStack flag = viewer.getHeldItem(Hand.MAIN_HAND);

    MatrixStack ms       = event.getMatrix();
    Matrix4f m4f         = ms.peek().getModel(); //ms.getLast().getMatrix();
    VoxelShape vs        = world.getBlockState(eventPos).getShape(world, eventPos);
    ActiveRenderInfo ari = event.getInfo();
    IVertexBuilder vb    = event.getBuffers().getBuffer(RenderType.getLines());
    double eyeX = ari.getProjectedView().getX();
    double eyeY = ari.getProjectedView().getY();
    double eyeZ = ari.getProjectedView().getZ();

    BlockPos pos1 = new BlockPos(eventPos.getX(),eventPos.getY(),eventPos.getZ());
    if (flag.hasTag() && flag.getTag().contains(WayPointToolItem.selectTag)) {
      BlockPos first = NBTUtil.readBlockPos(flag.getTag().getCompound(WayPointToolItem.selectTag));
      BlockPos pos2 = new BlockPos (first.getX(), first.getY(), first.getZ());

      //vs.forEachBox((x0, y0, z0, x1, y1, z1) -> {
        Color line = (WayPointToolItem.isValid(pos1, pos2)) ? VALID : INVALID;
        vb.vertex(m4f, (float) (X0 + pos1.getX() - eyeX), (float) (Y0 + pos1.getY() - eyeY), (float) (Z0 + pos1.getZ() - eyeZ))
        .color(line.getRed()/255f, line.getGreen()/255f, line.getBlue()/255f, 1f).endVertex();
        vb.vertex(m4f, (float) (X0 + pos2.getX() - eyeX), (float) (Y0 + pos2.getY() - eyeY), (float) (Z0 + pos2.getZ() - eyeZ))
        .color(line.getRed()/255f, line.getGreen()/255f, line.getBlue()/255f, 1f).endVertex();
      //});
    }
    else {
      vs.forEachEdge((x0, y0, z0, x1, y1, z1) -> {
        vb.vertex(m4f, (float) (x0 + eventPos.getX() - eyeX), (float) (y0 + eventPos.getY() - eyeY), (float) (z0 + eventPos.getZ() - eyeZ))
        .color(0f, 1f, 0f, 0.5f).endVertex();
        vb.vertex(m4f, (float) (x1 + eventPos.getX() - eyeX), (float) (y1 + eventPos.getY() - eyeY), (float) (z1 + eventPos.getZ() - eyeZ))
        .color(0f, 1f, 0f, 0.5f).endVertex();
      });
    }
  }
}
