package net.hypercubemc.seedfix.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin extends ChunkGenerator {
    @Shadow
    @Final
    @Mutable
    public static Codec<NoiseBasedChunkGenerator> CODEC;

    @Unique
    private static long generationSeed = 0;

    public NoiseBasedChunkGeneratorMixin(Registry<StructureSet> registry, Optional<HolderSet<StructureSet>> optional, BiomeSource biomeSource, BiomeSource biomeSource2, long l) {
        super(registry, optional, biomeSource, biomeSource2, l);
    }

    @Unique
    private static long getGenerationSeed() {
        return generationSeed;
    }


    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void seedfix$clinit(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.create((instance) -> {
            return commonCodec(instance).and(instance.group(RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter((noiseBasedChunkGenerator) -> {
                return ((NoiseBasedChunkGeneratorAccessor) (Object) noiseBasedChunkGenerator).getNoises();
            }), BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            Codec.LONG.fieldOf("seed").orElseGet(NoiseBasedChunkGeneratorMixin::getGenerationSeed).stable().forGetter((noiseBasedChunkGenerator) -> {
                return ((NoiseBasedChunkGeneratorAccessor) (Object) noiseBasedChunkGenerator).getSeed();
            }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((noiseBasedChunkGenerator) -> {
                return ((NoiseBasedChunkGeneratorAccessor) (Object) noiseBasedChunkGenerator).getSettings();
            }))).apply(instance, instance.stable(NoiseBasedChunkGenerator::new));
        });
    }

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/core/Registry;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/biome/BiomeSource;JLnet/minecraft/core/Holder;)V",
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
