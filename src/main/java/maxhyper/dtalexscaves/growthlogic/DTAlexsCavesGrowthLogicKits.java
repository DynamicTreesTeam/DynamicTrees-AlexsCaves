package maxhyper.dtalexscaves.growthlogic;

import com.ferreusveritas.dynamictrees.api.registry.Registry;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import maxhyper.dtalexscaves.DynamicTreesAlexsCaves;

public class DTAlexsCavesGrowthLogicKits {

    public static final GrowthLogicKit CANOPY = new CanopyLogic(DynamicTreesAlexsCaves.location("canopy"));
    public static final GrowthLogicKit THORNWOOD = new ThornwoodLogic(DynamicTreesAlexsCaves.location("thornwood"));

    public static void register(final Registry<GrowthLogicKit> registry) {
        registry.registerAll(CANOPY, THORNWOOD);
    }

}
