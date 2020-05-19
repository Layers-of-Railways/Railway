package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NeighborsUpdateDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborsUpdateDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void addUpdate(long worldTime, BlockPos pos) {
      Map<BlockPos, Integer> map = this.lastUpdate.get(worldTime);
      if (map == null) {
         map = Maps.newHashMap();
         this.lastUpdate.put(worldTime, map);
      }

      Integer integer = map.get(pos);
      if (integer == null) {
         integer = 0;
      }

      map.put(pos, integer + 1);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      long i = this.minecraft.world.getGameTime();
      int j = 200;
      double d0 = 0.0025D;
      Set<BlockPos> set = Sets.newHashSet();
      Map<BlockPos, Integer> map = Maps.newHashMap();
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLines());
      Iterator<Entry<Long, Map<BlockPos, Integer>>> iterator = this.lastUpdate.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<Long, Map<BlockPos, Integer>> entry = iterator.next();
         Long olong = entry.getKey();
         Map<BlockPos, Integer> map1 = entry.getValue();
         long k = i - olong;
         if (k > 200L) {
            iterator.remove();
         } else {
            for(Entry<BlockPos, Integer> entry1 : map1.entrySet()) {
               BlockPos blockpos = entry1.getKey();
               Integer integer = entry1.getValue();
               if (set.add(blockpos)) {
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(BlockPos.ZERO)).grow(0.002D).shrink(0.0025D * (double)k).offset((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()).offset(-camX, -camY, -camZ);
                  WorldRenderer.drawBoundingBox(ivertexbuilder, axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
                  map.put(blockpos, integer);
               }
            }
         }
      }

      for(Entry<BlockPos, Integer> entry2 : map.entrySet()) {
         BlockPos blockpos1 = entry2.getKey();
         Integer integer1 = entry2.getValue();
         DebugRenderer.renderText(String.valueOf((Object)integer1), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), -1);
      }

   }
}