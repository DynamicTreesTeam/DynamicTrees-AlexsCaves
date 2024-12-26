package maxhyper.dtalexscaves.model;

import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.client.ModelUtils;
import com.ferreusveritas.dynamictrees.models.modeldata.ModelConnections;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ThornwoodBranchBlockBakedModel implements IDynamicBakedModel {

    protected final BlockModel blockModel;

    protected final ResourceLocation modelLocation;

    protected final TextureAtlasSprite barkTexture;
    protected final TextureAtlasSprite ringsTexture;
    protected final TextureAtlasSprite spinesTexture;


    protected final BakedModel[][] sleeves = new BakedModel[6][7];
    protected final BakedModel[][] cores = new BakedModel[3][8]; // 8 Cores for 3 axis with the bark texture all all 6 sides rotated appropriately.
    protected final BakedModel[] rings = new BakedModel[8]; // 8 Cores with the ring textures on all 6 sides.

    public ThornwoodBranchBlockBakedModel(IGeometryBakingContext customData, ResourceLocation modelLocation, ResourceLocation barkTextureLocation, ResourceLocation ringsTextureLocation, ResourceLocation spinesTextureLocation,
                                          Function<Material, TextureAtlasSprite> spriteGetter) {
        this.blockModel = new BlockModel(null, new ArrayList<>(), new HashMap<>(), false, BlockModel.GuiLight.FRONT,
                ItemTransforms.NO_TRANSFORMS, new ArrayList<>());
        this.blockModel.customData.setRenderTypeHint(customData.getRenderTypeHint());
        this.modelLocation = modelLocation;
        this.barkTexture = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, barkTextureLocation));
        this.ringsTexture = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, ringsTextureLocation));
        this.spinesTexture = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, spinesTextureLocation));
        initModels();
    }

    private void initModels() {
        for (int i = 0; i < 8; i++) {
            int radius = i + 1;
            if (radius < 8) {
                for (Direction dir : Direction.values()) {
                    sleeves[dir.get3DDataValue()][i] = bakeSleeve(radius, dir, barkTexture, spinesTexture);
                }
            }
            cores[0][i] = bakeCore(radius, Axis.Y, barkTexture, spinesTexture, false); //DOWN<->UP
            cores[1][i] = bakeCore(radius, Axis.Z, barkTexture, spinesTexture, false); //NORTH<->SOUTH
            cores[2][i] = bakeCore(radius, Axis.X, barkTexture, spinesTexture, false); //WEST<->EAST

            rings[i] = bakeCore(radius, Axis.Y, ringsTexture, null, true);
        }
    }

    public BakedModel bakeLeaves(int radius, Axis axis, TextureAtlasSprite leavesTexture){

        Vector3f posFrom1 = new Vector3f(0, 0, 2.35f);
        Vector3f posTo1 = new Vector3f(0, 16, 20.35f);
        Vector3f posFrom2 = new Vector3f(11.35f, 0, -9);
        Vector3f posTo2 = new Vector3f(11.35f, 16, 9);

        Map<Direction, BlockElementFace> mapFacesIn = Maps.newEnumMap(Direction.class);
        for (Direction face : Direction.values()) {
            if (face == Direction.WEST || face == Direction.EAST){
                BlockFaceUV uvface = new BlockFaceUV(new float[]{0,0,16,16}, 0);
                mapFacesIn.put(face, new BlockElementFace(null, -1, null, uvface));
            }
        }
        BlockElementRotation rot1 = new BlockElementRotation(new Vector3f(0, 0, 0), Axis.Y, 45, false);
        BlockElementRotation rot2 = new BlockElementRotation(new Vector3f(0, 0, 0), Axis.Y, -45, false);

        BlockElement part1 = new BlockElement(posFrom1, posTo1, mapFacesIn, rot1, false);
        BlockElement part2 = new BlockElement(posFrom2, posTo2, mapFacesIn, rot2, false);

        IModelBuilder<?> builder = ModelUtils.getModelBuilder(this.blockModel.customData, leavesTexture);
        for (Map.Entry<Direction, BlockElementFace> e : part1.faces.entrySet()) {
            Direction face = e.getKey();
            builder.addCulledFace(face, ModelUtils.makeBakedQuad(part1, e.getValue(), leavesTexture, face, BlockModelRotation.X0_Y0, this.modelLocation));
        }
        for (Map.Entry<Direction, BlockElementFace> e : part2.faces.entrySet()) {
            Direction face = e.getKey();
            builder.addCulledFace(face, ModelUtils.makeBakedQuad(part2, e.getValue(), leavesTexture, face, BlockModelRotation.X0_Y0, this.modelLocation));
        }

        return builder.build();
    }

//    public BlockElement generateSleeveSpines(int radius, Direction dir, int ){
//
//    }
    public BlockElement generateSleevePart(int radius, Direction dir, boolean flipNormals){
        //Work in double units(*2)
        int dradius = radius * 2;
        int halfSize = (16 - dradius) / 2;
        int halfSizeX = dir.getStepX() != 0 ? halfSize : dradius;
        int halfSizeY = dir.getStepY() != 0 ? halfSize : dradius;
        int halfSizeZ = dir.getStepZ() != 0 ? halfSize : dradius;
        int move = 16 - halfSize;
        int centerX = 16 + (dir.getStepX() * move);
        int centerY = 16 + (dir.getStepY() * move);
        int centerZ = 16 + (dir.getStepZ() * move);

        Vector3f posFrom = new Vector3f((centerX - halfSizeX) / 2f, (centerY - halfSizeY) / 2f, (centerZ - halfSizeZ) / 2f);
        Vector3f posTo = new Vector3f((centerX + halfSizeX) / 2f, (centerY + halfSizeY) / 2f, (centerZ + halfSizeZ) / 2f);
        if (flipNormals){
            Vector3f aux = posFrom;
            posFrom = posTo;
            posTo = aux;
            dir = dir.getOpposite();
        }

        boolean negative = dir.getAxisDirection() == AxisDirection.NEGATIVE;
        if (dir.getAxis() == Axis.Z) {//North/South
            negative = !negative;
        }

        Map<Direction, BlockElementFace> mapFacesIn = Maps.newEnumMap(Direction.class);

        for (Direction face : Direction.values()) {
            if (dir.getOpposite() != face) { //Discard side of sleeve that faces core
                BlockFaceUV uvface = null;
                if (dir == face) {//Side of sleeve that faces away from core
                    if (radius == 1 || radius == 2) {
                        uvface = new BlockFaceUV(new float[]{12 - radius, 4 - radius, 12 + radius, 4 + radius}, 0);
                    }
                } else { //UV for Bark texture
                    uvface = new BlockFaceUV(new float[]{4 - radius, negative ? 16 - halfSize : 0, 4 + radius, negative ? 16 : halfSize}, getFaceAngle(dir.getAxis(), face));
                }
                if (uvface != null) {
                    mapFacesIn.put(face, new BlockElementFace(null, -1, null, uvface));
                }
            }
        }

        return new BlockElement(posFrom, posTo, mapFacesIn, null, true);
    }
    public BakedModel bakeSleeve(int radius, Direction dir, TextureAtlasSprite bark, TextureAtlasSprite spines) {

        BlockElement part = generateSleevePart(radius, dir, false);
        //BlockElement spinesPart = generateSleeveSpines(radius, dir);
        IModelBuilder<?> builder = ModelUtils.getModelBuilder(this.blockModel.customData, bark);

        for (Map.Entry<Direction, BlockElementFace> e : part.faces.entrySet()) {
            Direction face = e.getKey();
            builder.addCulledFace(face, ModelUtils.makeBakedQuad(part, e.getValue(), bark, face, BlockModelRotation.X0_Y0, this.modelLocation));
        }
//        for (Map.Entry<Direction, BlockElementFace> e : spinesPart.faces.entrySet()) {
//            Direction face = e.getKey();
//            builder.addCulledFace(face, ModelUtils.makeBakedQuad(spinesPart, e.getValue(), spines, face, BlockModelRotation.X0_Y0, this.modelLocation));
//        }

        return builder.build();
    }

    protected BlockElement generateCoreSpines (int radius, Axis axis, int number){
        Vector3f posFrom = new Vector3f(16, 0, 0);
        Vector3f posTo = new Vector3f(18, 16, 0);
        BlockElementRotation rot = new BlockElementRotation(new Vector3f(1, 0.5f, 0), axis, 45 + (90*number), false);

        Map<Direction, BlockElementFace> mapFacesIn = Maps.newEnumMap(Direction.class);
        for (Direction face : Direction.values()) {
            if (face == Direction.WEST || face == Direction.EAST){
                BlockFaceUV uvface = new BlockFaceUV(new float[]{2,0,0,16}, 0);
                mapFacesIn.put(face, new BlockElementFace(null, -1, null, uvface));
            }
        }

        return new BlockElement(posFrom, posTo, mapFacesIn, rot, false);
    }
    protected BlockElement generateCorePart (int radius, Axis axis, boolean flipNormals, boolean isRings){
        Vector3f posFrom = new Vector3f(8 - radius, 8 - radius, 8 - radius);
        Vector3f posTo = new Vector3f(8 + radius, 8 + radius, 8 + radius);
        if (flipNormals){
            Vector3f aux = posFrom;
            posFrom = posTo;
            posTo = aux;
        }

        Map<Direction, BlockElementFace> mapFacesIn = Maps.newEnumMap(Direction.class);

        for (Direction face : Direction.values()) {
            BlockFaceUV uvface = new BlockFaceUV(
                    isRings ? new float[]{12 - radius, 4 - radius, 12 + radius, 4 + radius} :
                              new float[]{4 - radius, 8 - radius, 4 + radius, 8 + radius},
                    getFaceAngle(axis, face));
            mapFacesIn.put(face, new BlockElementFace(null, -1, null, uvface));
        }

        return new BlockElement(posFrom, posTo, mapFacesIn, null, true);
    }
    public BakedModel bakeCore(int radius, Axis axis, TextureAtlasSprite icon, TextureAtlasSprite spines, boolean isRings) {

        BlockElement part = generateCorePart(radius, axis, false, isRings);

        IModelBuilder<?> builder = ModelUtils.getModelBuilder(this.blockModel.customData, icon);

        for (Map.Entry<Direction, BlockElementFace> e : part.faces.entrySet()) {
            Direction face = e.getKey();
            builder.addCulledFace(face, ModelUtils.makeBakedQuad(part, e.getValue(), icon, face, BlockModelRotation.X0_Y0, this.modelLocation));
        }

        if (spines != null){
            BlockElement spines0 = generateCoreSpines(radius, axis, 0);
            BlockElement spines1 = generateCoreSpines(radius, axis, 1);
            BlockElement spines2 = generateCoreSpines(radius, axis, 2);
            BlockElement spines3 = generateCoreSpines(radius, axis, 3);

            for (Map.Entry<Direction, BlockElementFace> e : spines0.faces.entrySet()) {
                Direction face = e.getKey();
                builder.addCulledFace(face, ModelUtils.makeBakedQuad(spines0, e.getValue(), spines, face, BlockModelRotation.X0_Y0, this.modelLocation));
            }
            for (Map.Entry<Direction, BlockElementFace> e : spines1.faces.entrySet()) {
                Direction face = e.getKey();
                builder.addCulledFace(face, ModelUtils.makeBakedQuad(spines1, e.getValue(), spines, face, BlockModelRotation.X0_Y0, this.modelLocation));
            }
            for (Map.Entry<Direction, BlockElementFace> e : spines2.faces.entrySet()) {
                Direction face = e.getKey();
                builder.addCulledFace(face, ModelUtils.makeBakedQuad(spines2, e.getValue(), spines, face, BlockModelRotation.X0_Y0, this.modelLocation));
            }
            for (Map.Entry<Direction, BlockElementFace> e : spines3.faces.entrySet()) {
                Direction face = e.getKey();
                builder.addCulledFace(face, ModelUtils.makeBakedQuad(spines3, e.getValue(), spines, face, BlockModelRotation.X0_Y0, this.modelLocation));
            }
        }

        return builder.build();
    }

    /**
     * A Hack to determine the UV face angle for a block column on a certain axis
     *
     * @param axis
     * @param face
     * @return
     */
    public int getFaceAngle(Axis axis, Direction face) {
        if (axis == Axis.Y) { //UP / DOWN
            return 0;
        } else if (axis == Axis.Z) {//NORTH / SOUTH
            return switch (face) {
                case UP -> 0;
                case WEST -> 270;
                case DOWN -> 180;
                default -> 90;
            };
        } else { //EAST/WEST
            return (face == Direction.NORTH) ? 270 : 90;
        }
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) {
        if (state == null || side != null) {
            return Collections.emptyList();
        }

        final List<BakedQuad> quadsList = new ArrayList<>(24);

        final int coreRadius = getRadius(state);

        int[] connections = new int[]{0, 0, 0, 0, 0, 0};
        Direction forceRingDir = null;
        final AtomicInteger twigRadius = new AtomicInteger(1);

        ModelConnections connectionsData = extraData.get(ModelConnections.CONNECTIONS_PROPERTY);
        if (connectionsData != null) {
            connections = connectionsData.getAllRadii();
            forceRingDir = connectionsData.getRingOnly();

            connectionsData.getFamily().ifValid(family ->
                    twigRadius.set(family.getPrimaryThickness()));
        }

        // Count number of connections.
        int numConnections = 0;
        for (int i : connections) {
            numConnections += (i != 0) ? 1 : 0;
        }

        if (numConnections == 0 && forceRingDir != null) {
            quadsList.addAll(rings[coreRadius - 1].getQuads(state, forceRingDir, rand, extraData, renderType));
        } else {
            // The source direction is the biggest connection from one of the 6 directions.
            final Direction sourceDir = getSourceDir(coreRadius, connections);
            final int coreDir = resolveCoreDir(sourceDir);

            // This is for drawing the rings on a terminating branch.
            final Direction coreRingDir = (numConnections == 1 && sourceDir != null) ? sourceDir.getOpposite() : null;

            for (Direction face : Direction.values()) {
                // Get quads for core model.
                if (coreRadius != connections[face.get3DDataValue()]) {
                    if ((coreRingDir == null || coreRingDir != face)) {
                        quadsList.addAll(cores[coreDir][coreRadius - 1].getQuads(state, face, rand, extraData, renderType));
                    } else {
                        quadsList.addAll(rings[coreRadius - 1].getQuads(state, face, rand, extraData, renderType));
                    }
                }
                // Get quads for sleeves models.
                if (coreRadius != 8) { // Special case for r!=8... If it's a solid block, so it has no sleeves.
                    for (Direction connDir : Direction.values()) {
                        final int idx = connDir.get3DDataValue();
                        final int connRadius = connections[idx];
                        // If the connection side matches the quadpull side then cull the sleeve face.  Don't cull radius-1 connections for leaves (which are partly transparent).
                        if (connRadius > 0 && (connRadius == twigRadius.get() || face != connDir)) {
                            quadsList.addAll(sleeves[idx][connRadius - 1].getQuads(state, face, rand, extraData, renderType));
                        }
                    }
                }
            }

        }


        return quadsList;
    }


    /**
     * Checks all neighboring tree parts to determine the connection radius for each side of this branch block.
     */
    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
        ModelConnections modelConnections;
        if (state.getBlock() instanceof BranchBlock branchBlock) {
            modelConnections = new ModelConnections(branchBlock.getConnectionData(world, pos, state)).setFamily(branchBlock.getFamily());
        } else {
            modelConnections = new ModelConnections();
        }

        return modelConnections.toModelData(tileData);
    }

    /**
     * Locates the side with the largest neighbor radius that's equal to or greater than this branch block
     *
     * @param coreRadius
     * @param connections an array of 6 integers, one for the radius of each connecting side. DUNSWE.
     * @return
     */
    @Nullable
    protected Direction getSourceDir(int coreRadius, int[] connections) {
        int largestConnection = 0;
        Direction sourceDir = null;

        for (Direction dir : Direction.values()) {
            int connRadius = connections[dir.get3DDataValue()];
            if (connRadius > largestConnection) {
                largestConnection = connRadius;
                sourceDir = dir;
            }
        }

        if (largestConnection < coreRadius) {
            sourceDir = null;//Has no source node
        }
        return sourceDir;
    }

    /**
     * Converts direction DUNSWE to 3 axis numbers for Y,Z,X
     *
     * @param dir
     * @return
     */
    protected int resolveCoreDir(@Nullable Direction dir) {
        if (dir == null) {
            return 0;
        }
        return dir.get3DDataValue() >> 1;
    }

    protected int getRadius(BlockState blockState) {
        // This way works with branches that don't have the RADIUS property, like cactus
        return ((BranchBlock) blockState.getBlock()).getRadius(blockState);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return barkTexture;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Nonnull
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(getRenderType());
    }

    public RenderType getRenderType (){
        ResourceLocation renderTypeHint = blockModel.customData.getRenderTypeHint();
        if (renderTypeHint == null)
            return RenderType.solid();
        return NamedRenderTypeManager.get(renderTypeHint).block();
    }
}