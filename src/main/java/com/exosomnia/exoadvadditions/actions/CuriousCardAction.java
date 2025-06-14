package com.exosomnia.exoadvadditions.actions;

import com.exosomnia.exoadvadditions.mixins.VillagerAccessor;
import com.exosomnia.exolib.scheduler.actions.ScheduledAction;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;

public class CuriousCardAction extends ScheduledAction {

    public Villager villager;

    public CuriousCardAction(Villager villager) {
        this.villager = villager;
    }

    @Override
    public boolean isValid() {
        return !villager.isRemoved();
    }

    @Override
    public void action() {
        VillagerData data = villager.getVillagerData();
        if (VillagerData.canLevelUp(data.getLevel())) {
            VillagerAccessor access = ((VillagerAccessor) villager);

            access.setIncreaseProfessionLevelOnUpdate(true);
            access.setUpdateMerchantTimer(1);

            manager.scheduleAction(this, 40);
        }
    }
}
