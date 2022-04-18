package moe.hertz.side_effects.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

@Mixin(EntitySpawnS2CPacket.class)
public abstract class EntitySpawnS2CPacketMixin {
  @Shadow
  @Final
  @Mutable
  private EntityType<?> entityTypeId;

  @Unique
  public void setEntityTypeId(EntityType<?> type) {
    entityTypeId = type;
  }
}
