package moe.hertz.side_effects.mixins;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import moe.hertz.side_effects.FakeEntityManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorage.Session;
import net.minecraft.world.spawner.Spawner;

@Mixin(ServerWorld.class)
public class ServerWorldMixin extends ServerWorld {
  public ServerWorldMixin(MinecraftServer server, Executor workerExecutor, Session session,
      ServerWorldProperties properties, RegistryKey<World> worldKey, RegistryEntry<DimensionType> registryEntry,
      WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator,
      boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime) {
    super(server, workerExecutor, session, properties, worldKey, registryEntry, worldGenerationProgressListener,
        chunkGenerator, debugWorld, seed, spawners, shouldTickTime);
  }

  @Unique
  private FakeEntityManager fakeEntityManager;

  public static FakeEntityManager getFakeEntityManager(ServerWorld world) {
    return ((ServerWorldMixin) world).fakeEntityManager;
  }

  @Inject(at = @At("HEAD"), method = "addPlayer")
  private void onAddPlayer(ServerPlayerEntity player, CallbackInfo ci) {
    fakeEntityManager.refreshPlayer(player, true);
  }

  @Inject(at = @At("TAIL"), method = "<init>")
  private void initFakeEntityManager(
      MinecraftServer server,
      Executor workerExecutor,
      LevelStorage.Session session,
      ServerWorldProperties properties,
      RegistryKey<World> worldKey,
      RegistryEntry<DimensionType> registryEntry,
      WorldGenerationProgressListener worldGenerationProgressListener,
      ChunkGenerator chunkGenerator,
      boolean debugWorld,
      long seed,
      List<Spawner> spawners,
      boolean shouldTickTime,
      CallbackInfo ci) {
    this.fakeEntityManager = this.getPersistentStateManager().getOrCreate(
        (nbt) -> FakeEntityManager.fromNbt(this, nbt),
        () -> new FakeEntityManager(this),
        "fake_entity");
  }

  @Inject(at = @At("TAIL"), method = "tick")
  private void tickFakeEntity(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    // this.
  }
}
