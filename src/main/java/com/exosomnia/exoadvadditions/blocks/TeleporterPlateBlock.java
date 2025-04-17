package com.exosomnia.exoadvadditions.blocks;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.world.TeleporterPlateTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TeleporterPlateBlock extends BasePressurePlateBlock {

    private final static Properties properties = Properties.of()
            .mapColor(MapColor.STONE).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().noCollission().strength(2.0F).pushReaction(PushReaction.DESTROY);
    private final BlockSetType type;

    private final static Map<Block, ResourceKey<Level>> registeredTeleporters = new HashMap<>();
    static {
        registeredTeleporters.put(Blocks.GRASS_BLOCK, Level.OVERWORLD);
        registeredTeleporters.put(Registry.BLOCK_ENCHANTED_DEEPSLATE.get(), Registry.DEPTHS_DIMENSION);
        registeredTeleporters.put(Registry.BLOCK_ERROR.get(), Registry.BEGINNING_DIMENSION);
    }

    public TeleporterPlateBlock() {
        super(properties, BlockSetType.STONE);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.ENABLED, Boolean.FALSE));
        type = BlockSetType.STONE;
    }

    //region Forced Overrides
    @Override
    protected int getSignalStrength(Level p_49336_, BlockPos p_49337_) {return 0;}

    @Override
    protected int getSignalForState(BlockState p_49354_) {return 0;}

    @Override
    protected BlockState setSignalForState(BlockState p_49301_, int p_49302_) {return null;}
    //endregion

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55262_) {
        p_55262_.add(BlockStateProperties.ENABLED);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.getPressedState(blockState) ? PRESSED_AABB : AABB;
    }

    @Override
    protected int getPressedTime() {
        return 10;
    }

    public boolean getPressedState(BlockState blockState) { return blockState.getValue(BlockStateProperties.ENABLED); }

    public boolean getIsPressed(Level level, BlockPos blockPos) {
        return getEntityCount(level, TOUCH_AABB.move(blockPos), Player.class) > 0;
    }

    protected BlockState setPressedState(BlockState blockState, int value) {
        return blockState.setValue(BlockStateProperties.ENABLED, Boolean.valueOf(value > 0));
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        int i = this.getPressedState(blockState) ? 1 : 0;
        if (i > 0) {
            this.checkPressed(null, serverLevel, blockPos, blockState, i);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!level.isClientSide) {
            int i = this.getPressedState(blockState) ? 1 : 0;
            if (i == 0) {
                this.checkPressed(entity, level, blockPos, blockState, i);
            }
        }
    }

    private void checkPressed(@Nullable Entity entity, Level level, BlockPos blockPos, BlockState blockState, int value) {
        int i = getIsPressed(level, blockPos) ? 1 : 0;
        boolean prevPressed = value > 0;
        boolean currentPressed = i > 0;
        if (value != i) {
            BlockState blockstate = this.setPressedState(blockState, i);
            level.setBlock(blockPos, blockstate, 2);
            this.updateNeighbours(level, blockPos);
            level.setBlocksDirty(blockPos, blockState, blockstate);
        }

        if (currentPressed) {
            level.scheduleTick(new BlockPos(blockPos), this, this.getPressedTime());
        }

        if (!currentPressed && prevPressed) {
            level.playSound(null, blockPos, this.type.pressurePlateClickOff(), SoundSource.BLOCKS);
            level.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, blockPos);
        } else if (currentPressed && !prevPressed) {
            level.playSound(null, blockPos, this.type.pressurePlateClickOn(), SoundSource.BLOCKS);
            level.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, blockPos);
            if (entity != null && !entity.isOnPortalCooldown()) {
                ResourceKey<Level> targetKey = registeredTeleporters.get(level.getBlockState(blockPos.below()).getBlock());
                if (targetKey == null) { return; }

                ServerLevel target = level.getServer().getLevel(targetKey);
                if (target == null || target.equals(level)) { return; }

                TeleporterPlateTeleporter teleporter = new TeleporterPlateTeleporter();
                if (!teleporter.setupPortal((ServerLevel)level, target, blockPos, entity)) { return; }

                entity.changeDimension(target, teleporter);
            }
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bool) {
        super.onRemove(blockState, level, blockPos, blockState2, bool);
    }
}
