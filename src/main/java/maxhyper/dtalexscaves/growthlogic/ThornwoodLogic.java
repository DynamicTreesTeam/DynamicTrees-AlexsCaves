package maxhyper.dtalexscaves.growthlogic;

import com.ferreusveritas.dynamictrees.api.configuration.ConfigurationProperty;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKitConfiguration;
import com.ferreusveritas.dynamictrees.growthlogic.context.DirectionManipulationContext;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;

public class ThornwoodLogic extends GrowthLogicKit {

    public static final ConfigurationProperty<Float> TWIST_CHANCE = ConfigurationProperty.floatProperty("zigzag_up_chance");

    public ThornwoodLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected GrowthLogicKitConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(TWIST_CHANCE, 0.4f);
    }

    @Override
    protected void registerProperties() {
        this.register(TWIST_CHANCE);
    }

    public static int shouldTwist (LevelAccessor world, BlockPos pos, int weight){
        long day = world.dayTime() / 24000L;
        int month = (int)day / 30;//Change the hashs every in-game month
        return (CoordUtils.coordHashCode(pos.above(month), 2) % weight);//Vary the height energy by a psuedorandom hash function
    }

    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration configuration, DirectionManipulationContext context) {
        int[] probMap = super.populateDirectionProbabilityMap(configuration, context);
        int twistFactor = shouldTwist(context.level(), context.pos(), 1024);
        boolean twist = twistFactor / 1024f < configuration.get(TWIST_CHANCE);
        Direction originDir = context.signal().dir.getOpposite();
        if (twist){
            int up = context.signal().defaultDir.ordinal();
            int dir = twistFactor % 4;
            if (dir != originDir.ordinal() && up != originDir.ordinal()){
                //Swap the probabilities of the up direction with the selected side direction.
                //This will have the effect of twisting the tree if the species' upProb is high enough.
                int upProb = probMap[up];
                probMap[up] = probMap[2+dir];
                probMap[2+dir] = upProb;
            }
        }
        probMap[originDir.ordinal()] = 0;
        return probMap;
    }

}