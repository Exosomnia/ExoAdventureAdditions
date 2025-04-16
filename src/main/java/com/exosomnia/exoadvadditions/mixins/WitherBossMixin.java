package com.exosomnia.exoadvadditions.mixins;

import com.exosomnia.exoarmory.ExoArmory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Monster {

    protected WitherBossMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void injectWitherTick(CallbackInfo ci) {
        if (level().getGameTime() % 10 != 0) return;
        level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(24.0)).forEach(player -> {
            player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 40, 0, true, true));
            player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 40, 2, true, true));

            if (player.hasEffect(MobEffects.WITHER) && level().random.nextFloat() < 0.125F) {
                ((WitherBoss)(Object)this).performRangedAttack(player, 0.0F);
            }
        });
    }
}
