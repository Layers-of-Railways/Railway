package net.minecraft.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;

public interface IResourceManager {
   Set<String> getResourceNamespaces();

   IResource getResource(ResourceLocation resourceLocationIn) throws IOException;

   boolean hasResource(ResourceLocation p_219533_1_);

   List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException;

   Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter);
}