package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackRenderer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
