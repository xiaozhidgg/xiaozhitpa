package com.xiaozhi.tpa.util;

import com.xiaozhi.tpa.XiaozhiTpaMod;

public final class Reference {
    public static final String MODID = XiaozhiTpaMod.MODID;
    /** NBT data-storage key under the world's saved data. */
    public static final String PLAYER_DATA_NAME = "xiaozhi_tpa_player_data";
    /** TPA requests expire after this many milliseconds. */
    public static final long TPA_TIMEOUT = 60_000L;

    private Reference() {}
}
