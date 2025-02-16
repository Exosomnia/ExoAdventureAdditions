package com.exosomnia.exoadvadditions.mixins;

import com.exosomnia.exoadvadditions.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyConstant(method = "getDamageAfterMagicAbsorb", constant = @Constant(intValue = 25))
    private int updateResistanceEffect(int value) { return 50; }

    @ModifyConstant(method = "getDamageAfterMagicAbsorb", constant = @Constant(floatValue = 25.0F))
    private float updateResistanceEffectDivisor(float value) { return 50.0F; }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z", shift = At.Shift.AFTER))
    private void updateTotemEffects(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity)(Object)this;

        int duration = switch (entity.level().getLevelData().getDifficulty()) {
            case PEACEFUL, EASY -> 3600;
            case NORMAL -> 5400;
            case HARD -> 7200;
        };
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 9));
        entity.addEffect(new MobEffectInstance(Registry.EFFECT_CHEATED_DEATH.get(), duration, 0, true, false, true));
    }

    @ModifyArg(method = "updateFallFlying()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setSharedFlag(IZ)V"))
    private boolean groundedCheckElytra(boolean flag) {
        if (flag) { return !((LivingEntity) (Object) this).hasEffect(Registry.EFFECT_GROUNDED.get()); }
        return false;
    }
}
