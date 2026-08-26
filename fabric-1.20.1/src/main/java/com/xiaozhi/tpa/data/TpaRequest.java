package com.xiaozhi.tpa.data;

import java.util.UUID;

/** A single pending teleport request. */
public record TpaRequest(UUID from, UUID target, long timestamp) {
}
