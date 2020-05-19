package net.minecraft.entity.merchant;

import net.minecraft.entity.Entity;

public interface IReputationTracking {
   void updateReputation(IReputationType type, Entity target);
}