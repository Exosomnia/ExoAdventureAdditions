package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40))
    private int updateMaxOnScreen(int max) { return 200; }
}
