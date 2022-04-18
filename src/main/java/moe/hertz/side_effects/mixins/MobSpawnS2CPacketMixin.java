package moe.hertz.side_effects.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.util.registry.Registry;

@Mixin(MobSpawnS2CPacket.class)
public abstract class MobSpawnS2CPacketMixin {
  @Shadow
  @Final
  @Mutable
  private int entityTypeId;

  @Unique
  public void setEntityTypeId(EntityType<?> type) {
    entityTypeId = Registry.ENTITY_TYPE.getRawId(type);
  }
}
