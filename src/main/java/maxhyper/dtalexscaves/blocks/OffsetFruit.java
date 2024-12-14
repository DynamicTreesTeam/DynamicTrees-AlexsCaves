package maxhyper.dtalexscaves.blocks;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.FruitBlock;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OffsetFruit extends Fruit {

    public static final TypedRegistry.EntryType<Fruit> TYPE = TypedRegistry.newType(OffsetFruit::new);

    public OffsetFruit(ResourceLocation registryName) {
        super(registryName);
    }

    protected FruitBlock createBlock(Block.Properties properties) {
        return new FruitBlock(properties, this){
            @Override
            public boolean isSupported(LevelReader world, BlockPos pos, BlockState state) {
                return world.getBlockState(pos.above()).getBlock() instanceof LeavesBlock || world.getBlockState(pos.above(2)).getBlock() instanceof LeavesBlock;
            }
        };
    }

}
