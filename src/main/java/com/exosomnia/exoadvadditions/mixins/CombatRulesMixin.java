package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CombatRules.class)
public class CombatRulesMixin {

    @ModifyConstant(method = "getDamageAfterMagicAbsorb", constant = @Constant(floatValue = 20.0F))
    private static float updateMaxEPF(float max) { return 24.0F; }

    @ModifyConstant(method = "getDamageAfterMagicAbsorb", constant = @Constant(floatValue = 25.0F))
    private static float updateDivisor(float divisor) { return 30.0F; }
}
