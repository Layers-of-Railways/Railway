package net.minecraft.client.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IShaderManager {
   int getProgram();

   void markDirty();

   ShaderLoader getVertexShaderLoader();

   ShaderLoader getFragmentShaderLoader();
}