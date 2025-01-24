package com.exosomnia.exoadvadditions.world;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class TeleporterPlateTeleporter implements ITeleporter {

    private PortalInfo portalInfo = null;

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity.setPortalCooldown(10);
        Entity teleportedEntity = repositionEntity.apply(false);

        BlockPos telePos = teleportedEntity.blockPosition();
        int teleX = telePos.getX();
        int teleY = telePos.getY();
        int teleZ = telePos.getZ();

        //Only replace nearby blocks with air if they can be replaced, but always replace the two blocks the player takes up.
        destWorld.setBlockAndUpdate(telePos.above(), Blocks.AIR.defaultBlockState());
        destWorld.setBlockAndUpdate(telePos, Blocks.AIR.defaultBlockState());
        BlockPos.betweenClosed(teleX - 1, teleY, teleZ - 1, teleX + 1, teleY + 1, teleZ + 1).forEach((pos) -> {
            if (destWorld.getBlockState(pos).canBeReplaced()) { destWorld.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()); }
        });

        //Only replace nearby blocks with obsidian if they can be replaced, but always replace the blocks below the player.
        destWorld.setBlockAndUpdate(telePos.below(), Blocks.OBSIDIAN.defaultBlockState());
        BlockPos.betweenClosed(teleX - 1, teleY - 1, teleZ - 1, teleX + 1, teleY - 1, teleZ + 1).forEach((pos) -> {
            if (destWorld.getBlockState(pos).canBeReplaced()) { destWorld.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState()); }
        });

        return teleportedEntity;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return portalInfo;
    }

    public boolean setupPortal(ServerLevel origin, ServerLevel destination, BlockPos teleporterPlate, Entity entity) {
        double scale = DimensionType.getTeleportationScale(origin.dimensionType(), destination.dimensionType());
        BlockPos calcPos = BlockPos.containing(new Vec3(teleporterPlate.getX(), teleporterPlate.getY(), teleporterPlate.getZ()).multiply(scale, 1.0, scale));

        if (calcPos.getY() < destination.getMinBuildHeight() + 2 && entity instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable("block.exoadvadditions.teleporter_plate.too_low").withStyle(ChatFormatting.RED));
            return false;
        }
        else if (calcPos.getY() > destination.getMinBuildHeight() + destination.getLogicalHeight() - 3 && entity instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable("block.exoadvadditions.teleporter_plate.too_high").withStyle(ChatFormatting.RED));
            return false;
        }

        BlockPos validPos = calcPos;
        for (BlockPos.MutableBlockPos checkPos : BlockPos.spiralAround(calcPos, 16, Direction.EAST, Direction.SOUTH)) {
            BlockState footing = destination.getBlockState(checkPos.below());
            if (destination.getBlockState(checkPos).canBeReplaced() && destination.getBlockState(checkPos.above()).canBeReplaced() &&
                    (footing.canBeReplaced() || footing.is(Blocks.OBSIDIAN))) {
                validPos = checkPos;
                break;
            }
        }

        int teleX = validPos.getX();
        int teleY = validPos.getY();
        int teleZ = validPos.getZ();

        portalInfo = new PortalInfo(new Vec3(teleX + 0.5, teleY, teleZ + 0.5), Vec3.ZERO, entity.getYRot(), entity.getXRot());
        return true;
    }

    @Override
    public boolean isVanilla() {
        return false;
    }
}
