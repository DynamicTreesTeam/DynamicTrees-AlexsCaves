package maxhyper.dtalexscaves;

import com.ferreusveritas.dynamictrees.api.GatherDataHelper;
import com.ferreusveritas.dynamictrees.api.registry.RegistryHandler;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import maxhyper.dtalexscaves.init.DTAlexsCavesClient;
import maxhyper.dtalexscaves.init.DTAlexsCavesRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DynamicTreesAlexsCaves.MOD_ID)
public class DynamicTreesAlexsCaves {
    public static final String MOD_ID = "dtalexscaves";

    public DynamicTreesAlexsCaves() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);

        RegistryHandler.setup(MOD_ID);

        DTAlexsCavesRegistries.BLOCKS.register(modEventBus);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DTAlexsCavesClient.setup();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        DTAlexsCavesRegistries.setup();
    }

    private void gatherData(final GatherDataEvent event) {
        GatherDataHelper.gatherAllData(
                MOD_ID, event,
                Family.REGISTRY,
                Species.REGISTRY,
                LeavesProperties.REGISTRY,
                Fruit.REGISTRY);
    }

    public static ResourceLocation location(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}