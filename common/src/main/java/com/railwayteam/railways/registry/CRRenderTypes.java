package com.railwayteam.railways.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Consumer;

public class CRRenderTypes extends RenderType {

    public static class CRRenderStateShards extends RenderStateShard {

        @Nullable
        private static ShaderInstance depthClearShader;

        @ApiStatus.Internal
        public static void onRegisterShaders(ResourceManager resourceManager, ShaderRegistrar registrar) throws IOException {
            registrar.register(
                // Create already depends on this form of ResourceLocations working on fabric, so we don't need a separate mixin for that
                new ShaderInstance(resourceManager, Railways.asResource("depth_clear").toString(), DefaultVertexFormat.POSITION_COLOR),
                shader -> depthClearShader = shader
            );
        }

        @FunctionalInterface
        public interface ShaderRegistrar {
            void register(ShaderInstance instance, Consumer<ShaderInstance> onRegister);
        }

        private static final ShaderStateShard DEPTH_CLEAR_SHADER = new ShaderStateShard(() -> depthClearShader);

        // Access protected inner classes
        private CRRenderStateShards(String name, Runnable setupState, Runnable clearState) {
            super(name, setupState, clearState);
        }
    }

    public static final RenderType DEPTH_CLEAR = create(
        "depth_clear",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        256,
        false,
        false,
        RenderType.CompositeState.builder()
            .setShaderState(CRRenderStateShards.DEPTH_CLEAR_SHADER)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .setDepthTestState(NO_DEPTH_TEST)
            .createCompositeState(false)
    );


    // Release the protected fields!
    private CRRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}
