package com.eelly.core.widget.refresh.api;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author Vurtne on 14-Dec-17.
 */

public interface DefaultRefreshHeaderCreater {
    @NonNull
    IRefreshHeader createRefreshHeader(Context context, IRefreshLayout layout);
}
