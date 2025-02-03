package com.exosomnia.exoadvadditions.mixins;

import com.exosomnia.exoadvadditions.Registry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(net.minecraft.world.level.levelgen.structure.structures.EndCityPieces.EndCityPiece.class)
public abstract class EndCityPieces$EndCityPieceMixin {

    @ModifyArg(method = "handleDataMarker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;setItem(Lnet/minecraft/world/item/ItemStack;Z)V", ordinal = 0))
    private ItemStack replaceElytra(ItemStack itemStack) {
        return new ItemStack(Registry.ITEM_UNFINISHED_ELYTRA.get());
    }
}
