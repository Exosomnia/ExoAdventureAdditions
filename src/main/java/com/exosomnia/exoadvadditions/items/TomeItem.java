package com.exosomnia.exoadvadditions.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public abstract class TomeItem extends Item {

    public boolean eternal;
    public final int rank;

    public TomeItem(Properties properties, int rank, boolean eternal) {
        super(properties);
        this.rank = rank;
        this.eternal = eternal;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return eternal;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) { return UseAnim.BOW; }

    @Override
    public int getUseDuration(ItemStack itemStack) { return 20; }
}
