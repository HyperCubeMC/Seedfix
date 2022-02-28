package net.hypercubemc.seedfix.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NoiseBasedChunkGenerator.class)
public interface NoiseBasedChunkGeneratorAccessor {
    @Accessor
    Registry<NormalNoise.NoiseParameters> getNoises();

    @Accessor
    long getSeed();

    @Accessor
    Holder<NoiseGeneratorSettings> getSettings();
}
