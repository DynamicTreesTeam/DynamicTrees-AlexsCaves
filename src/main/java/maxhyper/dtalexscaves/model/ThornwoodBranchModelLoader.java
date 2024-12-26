package maxhyper.dtalexscaves.model;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.models.geometry.BranchBlockModelGeometry;
import com.ferreusveritas.dynamictrees.models.loader.BranchBlockModelLoader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ThornwoodBranchModelLoader extends BranchBlockModelLoader {

    private static final String SPINES = "spines";

    protected ResourceLocation getLeavesTextureLocation(final JsonObject textureObject) {
        return this.getTextureLocation(textureObject, SPINES);
    }

    @Override
    public BranchBlockModelGeometry read(JsonObject modelObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        final JsonObject textures = this.getTexturesObject(modelObject);
        final ResourceLocation familyName = this.getLocation(modelObject);

        return this.getModelGeometry(this.getBarkTextureLocation(textures), this.getRingsTextureLocation(textures), this.getLeavesTextureLocation(textures),
                familyName == null ? null : TreeRegistry.processResLoc(familyName));
    }

    protected BranchBlockModelGeometry getModelGeometry(final ResourceLocation barkResLoc, final ResourceLocation ringsResLoc, final ResourceLocation leavesResLoc, @Nullable final ResourceLocation familyResLoc) {
        return new ThornwoodBranchBlockModelGeometry(barkResLoc, ringsResLoc, leavesResLoc);
    }

}
