package com.exosomnia.exoadvadditions.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ExoPotionUtils {

    public static ItemStack setColor(ItemStack itemStack, int color) {
        CompoundTag compoundtag = itemStack.getTag();
        if (compoundtag != null) {
            compoundtag.putInt("CustomPotionColor", color);
        }
        return itemStack;
    }
}
