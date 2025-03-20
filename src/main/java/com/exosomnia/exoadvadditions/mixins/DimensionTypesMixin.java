package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.data.worldgen.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DimensionTypes.class)
public class DimensionTypesMixin {

    @ModifyConstant(method = "bootstrap", constant = @Constant(intValue = 0, ordinal = 1))
    private int updateBlockLightLevelOverworld(int value) {
        return 7;
    }

    @ModifyConstant(method = "bootstrap", constant = @Constant(intValue = 0, ordinal = 5))
    private int updateBlockLightLevelEnd(int value) {
        return 7;
    }

    @ModifyConstant(method = "bootstrap", constant = @Constant(intValue = 0, ordinal = 7))
    private int updateBlockLightLevelOverworldCaves(int value) {
        return 7;
    }
}
