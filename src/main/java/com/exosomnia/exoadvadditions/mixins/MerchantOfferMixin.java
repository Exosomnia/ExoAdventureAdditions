package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {

    @Inject(method = "resetUses", at = @At("HEAD"), cancellable = true)
    public void resetUses(CallbackInfo ci) {
        MerchantOffer offer = (MerchantOffer)(Object)this;
        if (offer.getResult().is(Items.ENCHANTED_BOOK) && offer.getUses() != offer.getMaxUses()) { ci.cancel(); }
    }
}
