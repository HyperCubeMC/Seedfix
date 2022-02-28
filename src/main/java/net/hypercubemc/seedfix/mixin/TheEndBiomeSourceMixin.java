package net.hypercubemc.seedfix.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TheEndBiomeSource.class)
public abstract class TheEndBiomeSourceMixin extends BiomeSource {
    @Shadow
    @Final
    @Mutable
    public static Codec<TheEndBiomeSource> CODEC;

    @Unique
    private static long generationSeed = 0;

    @Unique
    private static long getGenerationSeed() {
        return generationSeed;
    }

    protected TheEndBiomeSourceMixin(List<Holder<Biome>> list) {
        super(list);
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void seedfix$clinit(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter((theEndBiomeSource) -> {
                return null;
            }), Codec.LONG.fieldOf("seed").orElseGet(TheEndBiomeSourceMixin::getGenerationSeed).stable().forGetter((theEndBiomeSource) -> {
                return ((TheEndBiomeSourceAccessor) theEndBiomeSource).getSeed();
            })).apply(instance, instance.stable(TheEndBiomeSource::new));
        });
    }

    @ModifyVariable(
            method = "<init>(JLnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V",
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
