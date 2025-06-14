package com.exosomnia.exoadvadditions.mixins;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoarmory.ExoArmory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
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
        boolean beginningDimension = level().dimension().equals(Registry.BEGINNING_DIMENSION);
        WitherBoss boss = ((WitherBoss) (Object) this);

        if (level().getGameTime() % 10 != 0) return;

        int bonusAmp;
        double range;
        double chance;
        if (beginningDimension) {
            if (boss.isPowered()) { boss.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 2)); }
            bonusAmp = 1;
            range = 48.0;
            chance = 0.25F;
            heal(0.5F);
            if (level().random.nextFloat() < 0.05F) {
                WitherSkeleton newMinion = new WitherSkeleton(EntityType.WITHER_SKELETON, level());
                newMinion.setPos(level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPosition()).above().getCenter());
                newMinion.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1));
                newMinion.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1));
                level().addFreshEntity(newMinion);
            }
        }
        else {
            bonusAmp = 0;
            range = 24.0;
            chance = 0.125F;
        }

        level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(range)).forEach(player -> {
            player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 40, bonusAmp, true, true));
            player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 40, 2 + bonusAmp, true, true));

            if (player.hasEffect(MobEffects.WITHER)) {
                player.addEffect(new MobEffectInstance(Registry.EFFECT_FATIGUED.get(), 40, bonusAmp, true, true));
                if (level().random.nextFloat() < chance) {
                    boss.performRangedAttack(player, 0.0F);
                }
            }
        });
    }
}
