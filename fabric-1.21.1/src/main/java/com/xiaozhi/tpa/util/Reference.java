package com.xiaozhi.tpa.util;

public final class Reference {
    public static final String MODID = "xiaozhi_tpa";
    /** TPA requests expire after this many milliseconds. */
    public static final long TPA_TIMEOUT = 60_000L;
    /** World-folder file that holds persistent player data (JSON). */
    public static final String DATA_FILE = "xiaozhi_tpa_player_data.json";

    private Reference() {}
}
