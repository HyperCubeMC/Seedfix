package net.hypercubemc.seedfix.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hypercubemc.seedfix.SeedFix;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin extends ChunkGenerator {
    @Shadow
    @Final
    @Mutable
    public static Codec<NoiseBasedChunkGenerator> CODEC;

    @Unique
    private static long generationSeed = 0;

    @Unique
    private static long getGenerationSeed() {
        return generationSeed;
    }

    public NoiseBasedChunkGeneratorMixin(BiomeSource biomeSource, BiomeSource biomeSource2, StructureSettings structureSettings, long l) {
        super(biomeSource, biomeSource2, structureSettings, l);
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void seedfix$clinit(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter((noiseBasedChunkGenerator) -> {
                return ((NoiseBasedChunkGeneratorAccessor) (Object) noiseBasedChunkGenerator).getNoises();
            }), BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            Codec.LONG.fieldOf("seed").orElseGet(NoiseBasedChunkGeneratorMixin::getGenerationSeed).stable().forGetter((noiseBasedChunkGenerator) -> {
                return ((NoiseBasedChunkGeneratorAccessor) (Object) noiseBasedChunkGenerator).getSeed();
            }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((noiseBasedChunkGenerator) -> {
                return ((NoiseBasedChunkGeneratorAccessor) (Object) noiseBasedChunkGenerator).getSettings();
            })).apply(instance, instance.stable(NoiseBasedChunkGenerator::new));
        });
    }

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/biome/BiomeSource;JLjava/util/function/Supplier;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static long seedfix$init(long seed) {
        if (seed == 0) {
            return getGenerationSeed();
        } else {
            return generationSeed = seed;
        }
    }
}
