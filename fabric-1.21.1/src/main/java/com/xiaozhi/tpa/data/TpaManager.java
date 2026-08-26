package com.xiaozhi.tpa.data;

import com.xiaozhi.tpa.util.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory store of pending teleport requests (per-session only, not persisted).
 * A player can have at most one outgoing and one incoming request at a time.
 * Requests expire after {@link Reference#TPA_TIMEOUT}.
 */
public final class TpaManager {
    private static final Map<UUID, TpaRequest> INCOMING = new HashMap<>(); // target -> request
    private static final Map<UUID, TpaRequest> OUTGOING = new HashMap<>(); // sender -> request

    private TpaManager() {}

    public static boolean hasIncoming(UUID target) {
        TpaRequest req = INCOMING.get(target);
        return req != null && !isExpired(req);
    }

    public static boolean hasOutgoing(UUID from) {
        TpaRequest req = OUTGOING.get(from);
        return req != null && !isExpired(req);
    }

    public static TpaRequest getIncoming(UUID target) {
        TpaRequest req = INCOMING.get(target);
        if (req == null) {
            return null;
        }
        if (isExpired(req)) {
            removeRequest(target);
            return null;
        }
        return req;
    }

    public static void sendRequest(UUID from, UUID target) {
        removeRequest(from);
        removeRequest(target);
        TpaRequest req = new TpaRequest(from, target, System.currentTimeMillis());
        INCOMING.put(target, req);
        OUTGOING.put(from, req);
    }

    /** Clears the pending request addressed to {@code target}. */
    public static void removeRequest(UUID target) {
        TpaRequest req = INCOMING.remove(target);
        if (req != null) {
            OUTGOING.remove(req.from());
        }
    }

    private static boolean isExpired(TpaRequest req) {
        return System.currentTimeMillis() - req.timestamp() > Reference.TPA_TIMEOUT;
    }
}
