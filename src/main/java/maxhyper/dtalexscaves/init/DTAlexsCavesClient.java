package maxhyper.dtalexscaves.init;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class DTAlexsCavesClient {

    public static void setup (){
        registerRenderLayers();
    }

    private static void registerRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(DTAlexsCavesRegistries.BANANA_SUCKER_BLOCK.get(), RenderType.cutout());
    }

}
