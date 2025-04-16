package com.exosomnia.exoadvadditions.loot.conditions;

import com.exosomnia.exoadvadditions.Registry;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class DifficultyStageCondition implements LootItemCondition {

    private int stage;

    DifficultyStageCondition(int stage) {
        this.stage = stage;
    }

    public LootItemConditionType getType() {
        return Registry.DIFFICULTY_STAGE_CONDITION.get();
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return GameStageHelper.getGlobalGameStage().getOrdinal() >= stage;
    }

    public static class Builder implements LootItemCondition.Builder {

        private int stage;

        public Builder setStage(int stage) {
            this.stage = stage;
            return this;
        }

        public LootItemCondition build() {
            return new DifficultyStageCondition(stage);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<DifficultyStageCondition> {

        @Override
        public void serialize(JsonObject object, DifficultyStageCondition condition, JsonSerializationContext context) {
            object.add("stage", context.serialize(condition.stage));
        }

        @Override
        public DifficultyStageCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            return new DifficultyStageCondition(GsonHelper.getAsInt(object, "stage"));
        }
    }
}
