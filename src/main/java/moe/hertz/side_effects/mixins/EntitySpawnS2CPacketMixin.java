package moe.hertz.side_effects.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

@Mixin(EntitySpawnS2CPacket.class)
public interface EntitySpawnS2CPacketMixin {
  @Accessor
  public void setEntityTypeId(EntityType<?> type);
}
