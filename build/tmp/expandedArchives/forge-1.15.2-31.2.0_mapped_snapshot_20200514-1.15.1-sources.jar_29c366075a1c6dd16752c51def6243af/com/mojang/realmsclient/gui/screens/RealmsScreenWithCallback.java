package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsScreenWithCallback<T> extends RealmsScreen {
   abstract void func_223627_a_(T p_223627_1_);
}