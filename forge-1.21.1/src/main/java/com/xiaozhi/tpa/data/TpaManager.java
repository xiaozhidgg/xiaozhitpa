package com.xiaozhi.tpa.data;

import com.xiaozhi.tpa.util.Reference;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory store of pending teleport requests.
 * A player can have at most one outgoing and one incoming request at a time
 * (mirroring the reference implementation). Requests expire after {@link Reference#TPA_TIMEOUT}.
 *
 * <p>Note: this data is intentionally <em>not</em> persisted; it is per-session state only.</p>
 */
public final class TpaManager {
    private static final Map<UUID, TpaRequest> INCOMING = new HashMap<>(); // target -> request
    private static final Map<UUID, TpaRequest> OUTGOING = new HashMap<>(); // sender -> request

    private TpaManager() {}

    public static boolean hasIncoming(ServerPlayer target) {
        TpaRequest req = INCOMING.get(target.getUUID());
        return req != null && !isExpired(req);
    }

    public static boolean hasOutgoing(ServerPlayer from) {
        TpaRequest req = OUTGOING.get(from.getUUID());
        return req != null && !isExpired(req);
    }

    /** Returns the pending request addressed to {@code target}, or null if none / expired. */
    public static TpaRequest getIncoming(ServerPlayer target) {
        TpaRequest req = INCOMING.get(target.getUUID());
        if (req == null) {
            return null;
        }
        if (isExpired(req)) {
            removeRequest(target);
            return null;
        }
        return req;
    }

    public static void sendRequest(ServerPlayer from, ServerPlayer to) {
        removeRequest(from);
        removeRequest(to);
        TpaRequest req = new TpaRequest(from.getUUID(), to.getUUID(), System.currentTimeMillis());
        INCOMING.put(to.getUUID(), req);
        OUTGOING.put(from.getUUID(), req);
    }

    /** Clears the pending request addressed to {@code target}. */
    public static void removeRequest(ServerPlayer target) {
        TpaRequest req = INCOMING.remove(target.getUUID());
        if (req != null) {
            OUTGOING.remove(req.from());
        }
    }

    private static boolean isExpired(TpaRequest req) {
        return System.currentTimeMillis() - req.timestamp() > Reference.TPA_TIMEOUT;
    }
}
