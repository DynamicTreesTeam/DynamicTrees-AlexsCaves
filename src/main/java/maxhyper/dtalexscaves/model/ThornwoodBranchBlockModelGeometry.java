package maxhyper.dtalexscaves.model;

import com.ferreusveritas.dynamictrees.models.geometry.BranchBlockModelGeometry;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ThornwoodBranchBlockModelGeometry extends BranchBlockModelGeometry {

    protected final ResourceLocation spinesTextureLocation;

    public ThornwoodBranchBlockModelGeometry(ResourceLocation barkTextureLocation, ResourceLocation ringsTextureLocation, ResourceLocation leavesTextureLocation) {
        super(barkTextureLocation, ringsTextureLocation, null, false);
        this.spinesTextureLocation = leavesTextureLocation;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        return new ThornwoodBranchBlockBakedModel(owner, modelLocation, this.barkTextureLocation, this.ringsTextureLocation, this.spinesTextureLocation, spriteGetter);
    }

}
