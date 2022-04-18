package moe.hertz.side_effects.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;

@Mixin(MobSpawnS2CPacket.class)
public interface MobSpawnS2CPacketMixin {
  @Accessor
  void setEntityTypeId(int id);
}
