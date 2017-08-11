package com.family.donghyunlee.family.data;

import com.squareup.otto.Bus;

/**
 * Created by DONGHYUNLEE on 2017-07-30.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
