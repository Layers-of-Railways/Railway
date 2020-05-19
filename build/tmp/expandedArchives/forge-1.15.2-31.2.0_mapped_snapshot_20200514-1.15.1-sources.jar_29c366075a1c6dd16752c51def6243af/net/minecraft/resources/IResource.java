package net.minecraft.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResource extends Closeable {
   @OnlyIn(Dist.CLIENT)
   ResourceLocation getLocation();

   InputStream getInputStream();

   @Nullable
   @OnlyIn(Dist.CLIENT)
   <T> T getMetadata(IMetadataSectionSerializer<T> serializer);

   String getPackName();
}