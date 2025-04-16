package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.recipes.tome.ShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipe;
import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.exosomnia.exolib.particles.shapes.ParticleShapeLine;
import com.exosomnia.exolib.particles.shapes.ParticleShapeOptions;
import com.exosomnia.exostats.ExoStats;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiConsumer;

public class MysteriousTomeItem extends Item {

    private boolean unleashed;

    public MysteriousTomeItem(boolean unleashed) {
        super(new Item.Properties().stacksTo(1));
        this.unleashed = unleashed;
    }

    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return unleashed ? Registry.MYSTERIOUS_RARITY : Rarity.EPIC;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level contextLevel = context.getLevel();
        if (contextLevel.isClientSide) return InteractionResult.sidedSuccess(true);

        BlockPos clickedPos = context.getClickedPos();
        ServerLevel level = (ServerLevel)contextLevel;
        ServerPlayer player = (ServerPlayer)context.getPlayer();
        Direction useDir = context.getClickedFace();

        if (!unleashed && tryUnleash(level, clickedPos, player, context.getHand())) { return InteractionResult.sidedSuccess(false); }
        boolean advanced = unleashed ? !Screen.hasShiftDown() : false;

        Vec3i offsets = switch(useDir) {
            case UP -> new Vec3i(-1, -2, -1);
            case DOWN -> new Vec3i(-1, 0, -1);
            case NORTH -> new Vec3i(-1, -1, 0);
            case SOUTH -> new Vec3i(-1, -1, -2);
            case EAST -> new Vec3i(-2, -1, -1);
            case WEST -> new Vec3i(0, -1, -1);
        };
        Vec3i innerCubeUpperOffset = new Vec3i(1, 1, 1);
        int craftSize = 3;

        if (advanced) {
            offsets = offsets.multiply(2);
            innerCubeUpperOffset = innerCubeUpperOffset.multiply(3);
            craftSize = 5;
        }

        int layerCount = craftSize;
        BlockPos startPos = clickedPos.offset(offsets);
        BlockPos innerCubeLowerPos = startPos.offset(1, 1, 1);
        BlockPos innerCubeUpperPos = innerCubeLowerPos.offset(innerCubeUpperOffset);
        BlockPos[] positions = new BlockPos[(int)Math.pow(craftSize, 3)];

        ArrayList<BlockState[][]> layers = new ArrayList<>(layerCount);
        int index = 0;
        for(var y = 0; y < layerCount; y++) {
            BlockState[][] targetLayer = new BlockState[craftSize][craftSize];
            for(var x = 0; x < layerCount; x++) {
                for(var z = 0; z < layerCount; z++) {
                    BlockPos searchPos = startPos.offset(x, y, z);
                    positions[index++] = searchPos;
                    BlockState state = level.getBlockState(searchPos);
                    targetLayer[x][z] = state;
                }
            }
            layers.add(targetLayer);
        }

        AABB items = new AABB(innerCubeLowerPos, innerCubeUpperPos);
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, items);
        ArrayList<ItemStack> craftingItems = null;
        if (!itemEntities.isEmpty()) {
            Map<Item, Integer> mappings = new HashMap<>();
            HashSet<Item> uniqueItems = new HashSet<>();
            for (ItemEntity entity : itemEntities) {
                ItemStack stack = entity.getItem();
                Item item = stack.getItem();
                uniqueItems.add(item);
                mappings.merge(item, stack.getCount(), Integer::sum);
            }
            ArrayList<ItemStack> builder = new ArrayList<>();
            uniqueItems.forEach(item -> builder.add(new ItemStack(item, mappings.get(item))));
            craftingItems = builder;
        }

        TomeRecipe recipe = Registry.TOME_RECIPE_MANAGER.getRecipe(craftingItems, layers);
        if (recipe == null) {
            player.sendSystemMessage(Component.translatable("item.exoadvadditions.mysterious_tome_active.not_valid").withStyle(ChatFormatting.RED), false);
            player.playNotifySound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0F, 0.5F);
            player.getCooldowns().addCooldown(this, 20);
        }
        else {
            //Drop result and player mechanics
            ItemStack result = recipe.getResult();
            Integer score = recipe.getScore();
            int centerOffset = craftSize/2;
            Vec3 centerPos = startPos.offset(centerOffset, centerOffset, centerOffset).getCenter();
            if (recipe.shouldDropResult()) {
                level.addFreshEntity(new ItemEntity(level, centerPos.x, centerPos.y, centerPos.z, result));
                player.awardStat(Stats.ITEM_CRAFTED.get(result.getItem()), 1);
            }
            if (score != 0) { player.awardStat(ExoStats.OCCULT_SCORE.get(), score); }
            player.awardStat(craftSize == 3 ? Registry.STAT_TOME_CRAFTS.get() : Registry.STAT_ADVANCED_TOME_CRAFTS.get(), 1);
            player.getCooldowns().addCooldown(this, 20);

            BlockPos centerBlockPos = BlockPos.containing(centerPos.x, centerPos.y, centerPos.z);
            BiConsumer<ServerPlayer, BlockPos> execute = recipe.getExecute();
            if (execute != null) { execute.accept(player, centerBlockPos); }

            //Recipe resource cleanup
            for (BlockPos pos : positions) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
            for (ItemEntity entity : itemEntities) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }

            //Flashy effects
            level.playSound(null, centerBlockPos, SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.0F, 0.75F);
            ParticleShapePacket particles = new ParticleShapePacket(new ParticleShapeLine(new RGBSParticleOptions(
                    ExoLib.REGISTRY.SWIRL_PARTICLE.get(), 0.0F, 0.5F, 0.25F, 0.1F),
                    player.getEyePosition(), new ParticleShapeOptions.Line(centerPos, 16)));
            for(ServerPlayer connected : level.players()) {
                PacketHandler.sendToPlayer(particles, connected);
            }
        }

        return InteractionResult.sidedSuccess(false);
    }

    private boolean tryUnleash(ServerLevel level, BlockPos position, ServerPlayer player, InteractionHand hand) {
        if (level.getBlockState(position).is(Blocks.LECTERN) && level.structureManager().getStructureAt(player.blockPosition(), level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(Registry.MYSTERIOUS_TOME_UNLEASH_STRUCTURE)).getStructure() != null) {
            level.playSound(null, position, SoundEvents.WITHER_AMBIENT, SoundSource.AMBIENT, 1.0F, 0.75F);
            level.playSound(null, position, SoundEvents.WITHER_DEATH, SoundSource.AMBIENT, 1.0F, 0.5F);

            ItemStack itemStack = player.getItemInHand(hand);
            itemStack.shrink(1);
            if (itemStack.isEmpty()) { player.setItemInHand(hand, new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get())); }
            else if (!player.getInventory().add(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get()))) {
                player.drop(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get()), false);
            }
            player.awardStat(Stats.ITEM_CRAFTED.get(Registry.ITEM_MYSTERIOUS_TOME_UNLEASHED.get()), 1);
            player.getCooldowns().addCooldown(this, 200);

            Vec3 middlePos = position.getCenter().add(0, 1, 1);
            level.explode(null, middlePos.x, middlePos.y, middlePos.z, 2F, Level.ExplosionInteraction.BLOCK);
            level.sendParticles(ParticleTypes.END_ROD, middlePos.x, middlePos.y, middlePos.z, 75, 0, 0, 0, 0.25);

            MinecraftServer server = level.getServer();
            CommandSourceStack stack = server.createCommandSourceStack().withPermission(4).withSource(server).withSuppressedOutput();
            for (var i = 0; i < 6; i++) {
                server.getCommands().performPrefixedCommand(stack, String.format("pandora %1$s pandorasbox:%2$s", player.getName().getString(),
                        Registry.VALID_PANDORA.get(level.random.nextInt(Registry.VALID_PANDORA.size()))));
            }

            GameStageHelper.increaseGlobalGameStage(GameStageHelper.getGameStages().get(4));
            return true;
        }
        return false;
    }
}
