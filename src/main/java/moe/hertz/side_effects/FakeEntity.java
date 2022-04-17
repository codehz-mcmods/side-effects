package moe.hertz.side_effects;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import moe.hertz.side_effects.mixins.EntityAttributesS2CPacketMixin;
import moe.hertz.side_effects.mixins.EntityMixin;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;

public class FakeEntity extends PersistentState {
  @Getter
  @NonNull
  protected EntityType<?> type;
  @Getter
  private final int id = EntityMixin.getCurrentId().incrementAndGet();
  @Getter
  private final UUID uuid;
  @Getter
  @NonNull
  protected Vec3d position;
  @Getter
  protected float pitch = 0f;
  @Getter
  protected float yaw = 0f;
  @Getter
  protected int data = 0;
  @Getter
  protected boolean onGround = false;
  @Getter
  @Setter
  protected boolean persistent = false;
  private boolean dirty = false;

  FakeEntity(EntityType<?> type, Vec3d position) {
    this.type = type;
    this.position = position;
    this.uuid = UUID.randomUUID();
  }

  FakeEntity(EntityType<?> type, Vec3d position, UUID uuid) {
    this.type = type;
    this.position = position;
    this.uuid = uuid;
  }

  public Packet<?> getSpawnPacket() {
    return new EntitySpawnS2CPacket(
        id,
        uuid,
        position.x,
        position.y,
        position.z,
        pitch,
        yaw,
        type,
        data,
        Vec3d.ZERO);
  }

  public void setPosition(Vec3d position) {
    this.position = position;
    this.dirty = true;
  }

  public void setRotation(float pitch, float yaw) {
    this.pitch = pitch;
    this.yaw = yaw;
    this.dirty = true;
  }

  public void setOnGround(boolean value) {
    this.onGround = value;
    dirty = true;
  }

  public Packet<?> getDespawnPacket() {
    return new EntitiesDestroyS2CPacket(id);
  }

  public Packet<?> getMovePacket() {
    PacketByteBuf byteBuf = PacketByteBufs.create();
    byteBuf.writeVarInt(id);
    byteBuf.writeDouble(position.x);
    byteBuf.writeDouble(position.y);
    byteBuf.writeDouble(position.z);
    byteBuf.writeByte((byte) (yaw * 256.0f / 360.0f));
    byteBuf.writeByte((byte) (pitch * 256.0f / 360.0f));
    byteBuf.writeBoolean(onGround);
    return new EntityPositionS2CPacket(byteBuf);
  }

  public Packet<?> getStatusPacket(byte status) {
    PacketByteBuf byteBuf = PacketByteBufs.create();
    byteBuf.writeInt(id);
    byteBuf.writeByte(status);
    return new EntityStatusS2CPacket(byteBuf);
  }

  public class AttributeUpdate {
    EntityAttributesS2CPacket packet = new EntityAttributesS2CPacket(id, Collections.emptyList());
    List<EntityAttributesS2CPacket.Entry> entries = ((EntityAttributesS2CPacketMixin) packet).getEntries();

    AttributeUpdate add(EntityAttribute attribute, double baseValue, EntityAttributeModifier... modifiers) {
      entries.add(new EntityAttributesS2CPacket.Entry(attribute, baseValue, Arrays.asList(modifiers)));
      return this;
    }

    public Packet<?> build() {
      return packet;
    }
  }

  public class DataUpdate {
    private List<DataTracker.Entry<?>> entries = new ArrayList<DataTracker.Entry<?>>();

    public <T> DataUpdate add(TrackedData<T> key, T value) {
      entries.add(new DataTracker.Entry<T>(key, value));
      return this;
    }

    public DataUpdate flag(EntityFlag... flags) {
      return add(EntityMixin.getFlagTracker(), EntityFlag.of(flags));
    }

    public DataUpdate noGravity(boolean value) {
      return add(EntityMixin.getNoGravityTracker(), value);
    }

    public DataUpdate silent(boolean value) {
      return add(EntityMixin.getSilentTracker(), value);
    }

    public DataUpdate pose(EntityPose value) {
      return add(EntityMixin.getEntityPoseTracker(), value);
    }

    public Packet<?> build() {
      return new EntityTrackerUpdateS2CPacket(id,
          new DataTracker(null) {
            @Override
            public List<Entry<?>> getDirtyEntries() {
              return entries;
            }
          },
          false);
    }
  }

  public void tick(ServerWorld world) {}

  public void sync(ServerPlayerEntity player, boolean spawn) {
    var handler = player.networkHandler;
    if (spawn) {
      handler.sendPacket(getSpawnPacket());
    } else if (this.dirty) {
      handler.sendPacket(getMovePacket());
    }
  }

  public void markClean() {
    this.dirty = false;
  }

  protected void readNbt(NbtCompound nbt) {
    this.pitch = nbt.getFloat("pitch");
    this.yaw = nbt.getFloat("yaw");
    this.data = nbt.getInt("data");
    this.onGround = nbt.getBoolean("on_ground");
  }

  private static HashMap<String, Constructor<?>> constructorCache = new HashMap<String, Constructor<?>>();

  private static Constructor<?> getConstructor(String name) {
    return constructorCache.computeIfAbsent(name, (key) -> {
      try {
        var clazz = Class.forName(key);
        if (clazz == null)
          return null;
        if (!FakeEntity.class.isAssignableFrom(clazz))
          return null;
        return clazz.getDeclaredConstructor(EntityType.class, Vec3d.class, UUID.class);
      } catch (ReflectiveOperationException e) {
        return null;
      }
    });
  }

  public static FakeEntity fromNbt(NbtCompound nbt) {
    try {
      var constructor = getConstructor(nbt.getString("clazz"));
      if (constructor == null)
        return null;
      var type = EntityType.get(nbt.getString("type"));
      if (type.isEmpty())
        return null;
      var position = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
      var uuid = nbt.getUuid("uuid");
      if (uuid == null)
        return null;
      var inst = (FakeEntity) constructor.newInstance(type.get(), position, uuid);
      inst.persistent = true;
      inst.readNbt(nbt);
      return inst;
    } catch (ReflectiveOperationException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    nbt.putString("clazz", this.getClass().getCanonicalName());
    nbt.putString("type", EntityType.getId(type).toString());
    nbt.putUuid("uuid", uuid);
    nbt.putDouble("x", position.x);
    nbt.putDouble("y", position.y);
    nbt.putDouble("z", position.z);
    nbt.putFloat("pitch", pitch);
    nbt.putFloat("yaw", yaw);
    nbt.putInt("data", data);
    nbt.putBoolean("on_ground", onGround);
    return nbt;
  }

}
