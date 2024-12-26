package maxhyper.dtalexscaves.model;

import maxhyper.dtalexscaves.DynamicTreesAlexsCaves;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DynamicTreesAlexsCaves.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BakedModelEventHandler {

    public static final ResourceLocation THORNWOOD = DynamicTreesAlexsCaves.location("thornwood");

    @SubscribeEvent
    public static void onModelRegistryEvent(ModelEvent.RegisterGeometryLoaders event) {
        event.register(THORNWOOD.getPath(), new ThornwoodBranchModelLoader());
    }

}
