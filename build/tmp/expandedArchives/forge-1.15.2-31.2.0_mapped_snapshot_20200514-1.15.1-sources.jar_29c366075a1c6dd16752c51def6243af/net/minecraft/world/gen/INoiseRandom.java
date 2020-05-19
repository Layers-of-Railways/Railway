package net.minecraft.world.gen;

public interface INoiseRandom {
   int random(int bound);

   ImprovedNoiseGenerator getNoiseGenerator();
}