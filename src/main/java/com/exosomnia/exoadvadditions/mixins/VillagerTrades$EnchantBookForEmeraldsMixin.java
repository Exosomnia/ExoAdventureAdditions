package com.exosomnia.exoadvadditions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public class VillagerTrades$EnchantBookForEmeraldsMixin {

    @ModifyConstant(method = "getOffer", constant = @Constant(intValue = 12))
    private int updateMaxUses(int value) {
        return 3;
    }
}
