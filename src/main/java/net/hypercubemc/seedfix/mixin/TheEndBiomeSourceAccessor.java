package net.hypercubemc.seedfix.mixin;

import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TheEndBiomeSource.class)
public interface TheEndBiomeSourceAccessor {
    @Accessor
    long getSeed();
}
