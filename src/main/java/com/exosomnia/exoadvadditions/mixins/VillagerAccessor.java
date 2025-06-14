package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface VillagerAccessor {

    @Accessor("increaseProfessionLevelOnUpdate")
    void setIncreaseProfessionLevelOnUpdate(boolean bool);

    @Accessor("updateMerchantTimer")
    void setUpdateMerchantTimer(int time);

    @Invoker("increaseMerchantCareer")
    void callIncreaseMerchantCareer();

    @Invoker("updateDemand")
    void callUpdateDemand();

    @Invoker("resendOffersToTradingPlayer")
    void callResendOffersToTradingPlayer();
}
