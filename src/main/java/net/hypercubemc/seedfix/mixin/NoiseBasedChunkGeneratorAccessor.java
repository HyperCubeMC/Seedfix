package net.hypercubemc.seedfix.mixin;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(NoiseBasedChunkGenerator.class)
public interface NoiseBasedChunkGeneratorAccessor {
    @Accessor
    Registry<NormalNoise.NoiseParameters> getNoises();

    @Accessor
    long getSeed();

    @Accessor
    Supplier<NoiseGeneratorSettings> getSettings();
}
