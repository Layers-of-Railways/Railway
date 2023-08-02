package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MonoBogeyBlock extends AbstractMonoBogeyBlock<MonoBogeyBlockEntity> {
    public MonoBogeyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return CRBogeyStyles.MONOBOGEY;
    }

    /*@Override
    @Environment(EnvType.CLIENT)
    public void render(@Nullable BlockState state, float wheelAngle, PoseStack ms, float partialTicks, MultiBufferSource buffers,
                       int light, int overlay) {
        if (state != null) {
            ms.translate(.5f, .5f, .5f);
            if (state.getValue(AXIS) == Direction.Axis.X)
                ms.mulPose(Vector3f.YP.rotationDegrees(90));
        }

        ms.translate(0, (-1.5 - 1 / 128f) * (upsideDown ? (state == null ? 1 : -1) : 1), 0);

        VertexConsumer vb = buffers.getBuffer(RenderType.cutoutMipped());
        BlockState air = Blocks.AIR.defaultBlockState();

        renderBogey(wheelAngle, ms, light, vb, air, state != null && upsideDown);
    }

    private void renderBogey(float wheelAngle, PoseStack ms, int light, VertexConsumer vb, BlockState air, boolean renderUpsideDown) {
        CachedBufferer.partial(CRBlockPartials.MONOBOGEY_FRAME, air)
            .rotateZ(renderUpsideDown ? 180 : 0)
            .scale(1 - 1 / 512f)
            .light(light)
            .renderInto(ms, vb);

//        wheelAngle = (Minecraft.getInstance().level.getGameTime() % 40) / 40f * 360;

        for (boolean left : Iterate.trueAndFalse) {
            for (int front : Iterate.positiveAndNegative) {
                ms.pushPose();
                CachedBufferer.partial(CRBlockPartials.MONOBOGEY_WHEEL, air)
                    .translate(left ? -12 / 16f : 12 / 16f, renderUpsideDown ? -13 /16f : 3 / 16f, front * 15 / 16f) //base position
                    .rotateY(left ? wheelAngle : -wheelAngle)
                    .translate(15/16f, 0, 0/16f)
                    .light(light)
                    .renderInto(ms, vb);
                ms.popPose();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public BogeyInstance createInstance(MaterialManager materialManager, CarriageBogey bogey) {
        return ((IBogeyFrameCanBeMonorail<BogeyInstance.Frame>) (Object) new BogeyInstance.Frame(bogey, materialManager))
            .setMonorail(
                upsideDown,
                IPotentiallyUpsideDownBogeyBlock.isUpsideDown(((AccessorCarriageBogey)bogey.carriage.leadingBogey()).getType())
            );
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return switch (pRotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> pState.cycle(AXIS);
            default -> pState;
        };
    }*/

    @Override
    public Class<MonoBogeyBlockEntity> getBlockEntityClass() {
        return MonoBogeyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MonoBogeyBlockEntity> getBlockEntityType() {
        return CRBlockEntities.MONO_BOGEY.get();
    }
}
