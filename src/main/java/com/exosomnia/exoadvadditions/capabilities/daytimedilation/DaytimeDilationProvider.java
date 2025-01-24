package com.exosomnia.exoadvadditions.capabilities.daytimedilation;

import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class DaytimeDilationProvider implements ICapabilitySerializable<IntTag> {

    public static final Capability<IDaytimeDilationStorage> DAYTIME_DILATION = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<IDaytimeDilationStorage> instance = LazyOptional.of(() -> new DaytimeDilationStorage(0));

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == DAYTIME_DILATION ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public IntTag serializeNBT() { return instance.resolve().get().serializeNBT(); }

    @Override
    public void deserializeNBT(IntTag nbt) { instance.resolve().get().deserializeNBT(nbt); }
}
