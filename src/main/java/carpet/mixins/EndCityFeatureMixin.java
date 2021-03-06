package carpet.mixins;

import carpet.CarpetSettings;
import com.google.common.collect.Lists;
//import com.mojang.datafixers.Dynamic;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;
//import java.util.function.Function;

@Mixin(EndCityFeature.class)
public abstract class EndCityFeatureMixin extends StructureFeature<DefaultFeatureConfig>
{
    private static final List<SpawnSettings.SpawnEntry> spawnList = Lists.newArrayList(new SpawnSettings.SpawnEntry(EntityType.SHULKER, 10, 4, 4));

    public EndCityFeatureMixin(Codec<DefaultFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public List<SpawnSettings.SpawnEntry> getMonsterSpawns()
    {
        if (CarpetSettings.shulkerSpawningInEndCities)
            return spawnList;
        return Collections.emptyList();
    }
}
