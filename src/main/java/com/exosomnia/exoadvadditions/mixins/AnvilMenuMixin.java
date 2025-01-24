package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
    private int updateMaxOnMenu(int max) { return 100; }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 1))
    private int updateRenameOnlyMax(int max) { return 2; }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 39))
    private int updateRenameOnlyCost(int cost) { return 1; }
}