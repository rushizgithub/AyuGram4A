/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.sync;

public class AyuSyncControllerEmpty extends AyuSyncController {
    @Override
    public void connect() {
        // nah
    }

    @Override
    public void forceSync() {
        // nah
    }

    @Override
    public void syncRead(int accountId, long dialogId, int untilId) {
        // nah
    }
}
