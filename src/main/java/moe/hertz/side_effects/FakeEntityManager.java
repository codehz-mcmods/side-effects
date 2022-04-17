package moe.hertz.side_effects;

import java.util.HashMap;
import java.util.UUID;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.NonNull;
import moe.hertz.side_effects.mixins.ServerWorldMixin;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class FakeEntityManager extends PersistentState {
  final ServerWorld world;
  final HashMap<UUID, FakeEntity> entities = new HashMap<UUID, FakeEntity>();
  final Int2ObjectMap<FakeEntity> idIndex = new Int2ObjectLinkedOpenHashMap<FakeEntity>();

  public FakeEntityManager(ServerWorld world) {
    this.world = world;
  }

  public FakeEntity find(UUID uuid) {
    return entities.get(uuid);
  }

  public FakeEntity find(int id) {
    return idIndex.get(id);
  }

  public static FakeEntityManager fromNbt(ServerWorld world, NbtCompound nbt) {
    var ret = new FakeEntityManager(world);
    var list = nbt.getList("entities", NbtElement.COMPOUND_TYPE);
    for (var data : list) {
      ret.add(FakeEntity.fromNbt((NbtCompound) data));
    }
    return ret;
  }

  public void add(@NonNull FakeEntity entity) {
    this.entities.put(entity.getUuid(), entity);
    this.idIndex.put(entity.getId(), entity);
    for (var player : world.getPlayers()) {
      entity.sync(player, true);
    }
  }

  public void remove(@NonNull FakeEntity entity) {
    this.entities.remove(entity.getUuid());
    this.idIndex.remove(entity.getId());
    for (var player : world.getPlayers()) {
      player.networkHandler.sendPacket(entity.getDespawnPacket());
    }
  }

  public void refreshPlayer(ServerPlayerEntity player, boolean spawn) {
    for (var entity : entities.values()) {
      entity.sync(player, spawn);
    }
  }

  public void tick() {
    for (var entity : entities.values()) {
      entity.tick(world);
      for (var player : world.getPlayers()) {
        entity.sync(player, false);
      }
      entity.markClean();
    }
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    var list = new NbtList();
    for (var entity : entities.values()) {
      if (entity.isPersistent()) {
        list.add(entity.writeNbt(new NbtCompound()));
      }
    }
    nbt.put("entities", list);
    return nbt;
  }
}
