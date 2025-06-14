package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {

    @ModifyConstant(method = "getDamageBonus", constant = @Constant(floatValue = 2.5F, ordinal = 0))
    private float updateSmiteDamage(float damage) { return 1.5F; }
}