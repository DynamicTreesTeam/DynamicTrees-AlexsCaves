package maxhyper.dtalexscaves.blocks;

import maxhyper.dtalexscaves.DynamicTreesAlexsCaves;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class BananaSuckerBlock extends HorizontalDirectionalBlock implements IPlantable {

    protected static final VoxelShape SHAPE_E = Shapes.box(
            0 /16f,0  /16f,6  /16f,
            4 /16f,15 /16f,10 /16f);
    protected static final VoxelShape SHAPE_W = Shapes.box(
            12  /16f,0 /16f, 6  /16f,
            16 /16f,15 /16f,10 /16f);
    protected static final VoxelShape SHAPE_N = Shapes.box(
            6  /16f,0  /16f,12  /16f,
            10 /16f,15 /16f,16 /16f);
    protected static final VoxelShape SHAPE_S = Shapes.box(
            6  /16f,0  /16f,0 /16f,
            10 /16f,15 /16f,4 /16f);

    public BananaSuckerBlock() {
        super(Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        if (pState.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return pLevel.getBlockState(blockpos).canSustainPlant(pLevel, blockpos, Direction.UP, this);
        return false;
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) return defaultBlockState();
        return state;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_E;
            case SOUTH -> SHAPE_S;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    public Item asItem() {
        Item item = ForgeRegistries.ITEMS.getValue(DynamicTreesAlexsCaves.location("banana_seed"));
        if (item == null) return Items.AIR;
        return item;
    }

}
