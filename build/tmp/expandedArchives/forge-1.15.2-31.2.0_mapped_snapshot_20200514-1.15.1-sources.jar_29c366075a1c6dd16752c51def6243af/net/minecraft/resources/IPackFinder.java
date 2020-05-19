package net.minecraft.resources;

import java.util.Map;

public interface IPackFinder {
   <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory);
}