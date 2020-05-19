package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IStructureReader {
   @Nullable
   StructureStart getStructureStart(String stucture);

   void putStructureStart(String structureIn, StructureStart structureStartIn);

   LongSet getStructureReferences(String structureIn);

   void addStructureReference(String strucutre, long reference);

   Map<String, LongSet> getStructureReferences();

   void setStructureReferences(Map<String, LongSet> p_201606_1_);
}