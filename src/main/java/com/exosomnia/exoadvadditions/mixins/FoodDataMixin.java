package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 6.0F))
    private float modifyHealingExhaust(float og) {
        return 8.0F;
    }
}
