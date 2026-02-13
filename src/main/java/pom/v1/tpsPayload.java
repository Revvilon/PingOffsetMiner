package pom.v1;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record tpsPayload(double tps, double mspt) implements CustomPayload {
    public static final Id<tpsPayload> ID = new Id<>(Identifier.of("ping-offset-miner", "tps_sync"));

    public static final PacketCodec<RegistryByteBuf, tpsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, tpsPayload::tps,
            PacketCodecs.DOUBLE, tpsPayload::mspt,
            tpsPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
