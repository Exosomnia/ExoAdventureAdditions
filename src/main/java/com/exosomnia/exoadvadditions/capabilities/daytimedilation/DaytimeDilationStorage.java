package com.exosomnia.exoadvadditions.capabilities.daytimedilation;

import net.minecraft.nbt.IntTag;

public class DaytimeDilationStorage implements IDaytimeDilationStorage {

    private int amount;

    public DaytimeDilationStorage(int amount) {
        setAmount(amount);
    }

    @Override
    public void setAmount(int amount) { this.amount = amount; }

    @Override
    public int getAmount() { return amount; }

    @Override
    public IntTag serializeNBT() { return IntTag.valueOf(amount); }

    @Override
    public void deserializeNBT(IntTag nbt) { amount = nbt.getAsInt(); }
}
