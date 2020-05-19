package net.minecraft.world.storage.loot;

import java.util.Random;
import net.minecraft.util.ResourceLocation;

public interface IRandomRange {
   ResourceLocation CONSTANT = new ResourceLocation("constant");
   ResourceLocation UNIFORM = new ResourceLocation("uniform");
   ResourceLocation BINOMIAL = new ResourceLocation("binomial");

   int generateInt(Random rand);

   ResourceLocation getType();
}