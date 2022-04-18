package moe.hertz.side_effects;

import moe.hertz.side_effects.mixins.EntitySpawnS2CPacketMixin;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

public interface IExtendEntity extends IFakeEntity {
  default public Packet<?> createSpawnPacket() {
    var ret = new EntitySpawnS2CPacket((Entity) (Object) this);
    ((EntitySpawnS2CPacketMixin) ret).setEntityTypeId(getFakeType());
    return ret;
  }
}
