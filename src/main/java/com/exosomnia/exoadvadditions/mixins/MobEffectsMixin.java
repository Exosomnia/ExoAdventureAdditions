package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MobEffects.class)
public class MobEffectsMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 3.0D))
    private static double updateStrengthEffect(double value) { return 2.5D; }
}
