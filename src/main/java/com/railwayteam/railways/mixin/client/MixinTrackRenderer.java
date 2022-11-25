package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_tracks.models.ReScaledSpriteCoordinateExpander;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.content.custom_tracks.models.SpriteCopyingBakedModel;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackRenderer;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.simibubi.create.AllBlockPartials.TRACK_TIE;
import static com.simibubi.create.AllBlockPartials.TRACK_SEGMENT_LEFT;
import static com.simibubi.create.AllBlockPartials.TRACK_SEGMENT_RIGHT;

@Mixin(value = TrackRenderer.class, remap = false)
public class MixinTrackRenderer {
  @Nullable
  private static BezierConnection bezierConnection = null;

  @Inject(method = "renderBezierTurn", at = @At("HEAD"), remap = false)
  private static void storeBezierConnection(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
    bezierConnection = bc;
  }

  @Inject(method = "renderBezierTurn", at = @At("RETURN"))
  private static void clearBezierConnection(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
    bezierConnection = null;
  }

  @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lcom/simibubi/create/AllBlockPartials;TRACK_TIE:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
  private static PartialModel replaceTie() {
    if (bezierConnection != null) {
      TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
      if (material.isCustom()) {
        return CRBlockPartials.TRACK_PARTS.get(material).tie;
      }
    }
    return TRACK_TIE;
  }

  @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_LEFT:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
  private static PartialModel replaceSegLeft() {
    if (bezierConnection != null) {
      TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
      if (material.isCustom()) {
        return CRBlockPartials.TRACK_PARTS.get(material).segment_left;
      }
    }
    return TRACK_SEGMENT_LEFT;
  }

  @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_RIGHT:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
  private static PartialModel replaceSegRight() {
    if (bezierConnection != null) {
      TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
      if (material.isCustom()) {
        return CRBlockPartials.TRACK_PARTS.get(material).segment_right;
      }
    }
    return TRACK_SEGMENT_RIGHT;
  }

  @Inject(method = "renderSafe(Lcom/simibubi/create/content/logistics/trains/track/TrackTileEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
      at = @At("HEAD"), remap = false)
  private void renderCasing(TrackTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
    SlabBlock casingBlock = ((IHasTrackCasing) te).getTrackCasing();
    if (casingBlock != null) {
      ms.pushPose();
      BlockState air = Blocks.AIR.defaultBlockState();
      PartialModel model = CRBlockPartials.TRACK_CASING;
      int x = 0;
      int y = 0;
      int z = 0;
      int yaw = 0;
      switch (te.getBlockState().getValue(TrackBlock.SHAPE)) {
        case XO -> {
          yaw = 90;
          x = -1;
        }
      }
      BakedModel slabModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(casingBlock.defaultBlockState());
      List<BakedQuad> quads = slabModel.getQuads(casingBlock.defaultBlockState(), null, te.getLevel().getRandom(), EmptyModelData.INSTANCE);
      TextureAtlasSprite sprite = quads.get(0).getSprite();
      TextureAtlasSprite targetSprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(casingBlock.defaultBlockState()).getParticleIcon(EmptyModelData.INSTANCE);

      TextureAtlasSprite originSprite = model.get().getParticleIcon(EmptyModelData.INSTANCE);

      BakedModel debugModel = model.get();

      ReScaledSpriteCoordinateExpander.renderMultilineDebugText(ms, buffer, light, 1.0D,
          "Casing: "+casingBlock.getRegistryName().toString(),
          "null quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), null, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size(),
          "North quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), Direction.NORTH, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size(),
          "South quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), Direction.SOUTH, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size(),
          "East quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), Direction.EAST, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size(),
          "West quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), Direction.WEST, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size(),
          "Up quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), Direction.UP, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size(),
          "Down quad size: "+debugModel.getQuads(casingBlock.defaultBlockState(), Direction.DOWN, te.getLevel().getRandom(), EmptyModelData.INSTANCE).size()
          );

      VertexConsumer wrapped = buffer.getBuffer(ItemBlockRenderTypes.getRenderType(casingBlock.defaultBlockState(), false));
      Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(ms.last(), wrapped, null, new SpriteCopyingBakedModel(model.get(), slabModel),
          1, 1, 1, light, overlay, EmptyModelData.INSTANCE);

      /*CachedBufferer.partial(model, air)
          .light(light)
          .rotateY(yaw)
          .translate(x, y, z)
          .renderInto(ms, targetSprite.wrap(buffer.getBuffer(RenderType.solid())));*/
      ms.popPose();
    }
  }
}
