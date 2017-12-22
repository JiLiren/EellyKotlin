package com.eelly.core.widget.refresh.api;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author Vurtne on 14-Dec-17.
 */

public interface DefaultRefreshFooterCreater {
    @NonNull
    IRefreshFooter createRefreshFooter(Context context, IRefreshLayout layout);
}
