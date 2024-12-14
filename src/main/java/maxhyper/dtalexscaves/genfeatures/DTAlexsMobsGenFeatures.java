package maxhyper.dtalexscaves.genfeatures;

import com.ferreusveritas.dynamictrees.api.registry.Registry;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import maxhyper.dtalexscaves.DynamicTreesAlexsCaves;

public class DTAlexsMobsGenFeatures {

    public static final GenFeature BANANA_FRUIT = new BananaFruitGenFeature(DynamicTreesAlexsCaves.location("banana_fruit"));
    public static final GenFeature PLANT_SUCKERS = new PlantSuckerGenFeature(DynamicTreesAlexsCaves.location("plant_suckers"));

    public static void register(final Registry<GenFeature> registry) {
        registry.registerAll(BANANA_FRUIT, PLANT_SUCKERS);
    }

}
