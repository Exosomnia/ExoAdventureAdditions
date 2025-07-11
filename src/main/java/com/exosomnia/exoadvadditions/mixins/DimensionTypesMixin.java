package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.data.worldgen.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DimensionTypes.class)
public class DimensionTypesMixin {

    @ModifyConstant(method = "bootstrap", constant = @Constant(intValue = 0, ordinal = 1))
    private static int updateBlockLightLevelOverworld(int value) {
        return 4;
    }

    @ModifyConstant(method = "bootstrap", constant = @Constant(intValue = 0, ordinal = 5))
    private static int updateBlockLightLevelEnd(int value) {
        return 4;
    }

    @ModifyConstant(method = "bootstrap", constant = @Constant(intValue = 0, ordinal = 7))
    private static int updateBlockLightLevelOverworldCaves(int value) {
        return 4;
    }
}
