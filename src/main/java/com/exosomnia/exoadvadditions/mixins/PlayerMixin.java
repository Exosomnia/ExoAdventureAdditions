package com.exosomnia.exoadvadditions.mixins;

import com.exosomnia.exoadvadditions.Registry;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tryToStartFallFlying()Z", at = @At("HEAD"), cancellable = true)
    public void groundedCheckElytra(CallbackInfoReturnable<Boolean> cir) {
        if (((Player)(Object)this).hasEffect(Registry.EFFECT_GROUNDED.get())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
