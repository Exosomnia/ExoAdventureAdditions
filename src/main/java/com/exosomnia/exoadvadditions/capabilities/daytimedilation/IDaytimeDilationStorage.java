package com.exosomnia.exoadvadditions.capabilities.daytimedilation;

import net.minecraft.nbt.IntTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDaytimeDilationStorage extends INBTSerializable<IntTag> {

    void setAmount(int amount);

    int getAmount();
}
