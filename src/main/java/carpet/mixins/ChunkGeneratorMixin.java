package carpet.mixins;

import carpet.CarpetSettings;
import carpet.fakes.ChunkGeneratorInterface;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin implements ChunkGeneratorInterface
{
    @Shadow protected abstract void generateStrongholdPositions();

    @Override
    public void initStrongholds()
    {
        generateStrongholdPositions();
    }
    // this might not be needed anymore
    /*
    @Inject(method = "hasStructure", at = @At("HEAD"), cancellable = true)
    private void skipGenerationBiomeChecks(Biome biome_1, StructureFeature<? extends FeatureConfig> structureFeature_1, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.skipGenerationChecks)
        {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

     */
}
