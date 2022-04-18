package moe.hertz.side_effects;

import moe.hertz.side_effects.mixins.MobSpawnS2CPacketMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.util.registry.Registry;

public interface IExtendLivingEntity extends IExtendEntity {
  @Override
  default public Packet<?> createSpawnPacket() {
    var ret = new MobSpawnS2CPacket((LivingEntity) (Object) this);
    ((MobSpawnS2CPacketMixin) ret).setEntityTypeId(Registry.ENTITY_TYPE.getRawId(getFakeType()));
    return ret;
  }
}
