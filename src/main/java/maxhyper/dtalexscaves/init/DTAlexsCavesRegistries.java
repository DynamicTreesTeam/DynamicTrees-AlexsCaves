package maxhyper.dtalexscaves.init;

import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import maxhyper.dtalexscaves.genfeatures.DTAlexsMobsGenFeatures;
import maxhyper.dtalexscaves.growthlogic.DTAlexsCavesGrowthLogicKits;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTAlexsCavesRegistries {

    public static void setup() {

    }

    @SubscribeEvent
    public static void onGenFeatureRegistry (final com.ferreusveritas.dynamictrees.api.registry.RegistryEvent<GenFeature> event) {
        DTAlexsMobsGenFeatures.register(event.getRegistry());
    }
    @SubscribeEvent
    public static void onGrowthLogicKitRegistry (final com.ferreusveritas.dynamictrees.api.registry.RegistryEvent<GrowthLogicKit> event) {
        DTAlexsCavesGrowthLogicKits.register(event.getRegistry());
    }


}
