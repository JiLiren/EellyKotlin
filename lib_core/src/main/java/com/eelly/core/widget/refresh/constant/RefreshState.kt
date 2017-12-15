package com.eelly.core.widget.refresh.constant

/**
 * @author Vurtne on 14-Dec-17.
 */

enum class RefreshState {
    None(0, false),
    PullDownToRefresh(1, true), PullToUpLoad(2, true),
    PullDownCanceled(1, false), PullUpCanceled(2, false),
    ReleaseToRefresh(1, true), ReleaseToLoad(2, true),
    ReleaseToTwoLevel(1, true), TwoLevelReleased(1, false),
    RefreshReleased(1, false), LoadReleased(2, false),
    Refreshing(1, false, true), Loading(2, false, true), TwoLevel(1, false, true),
    RefreshFinish(1, false), LoadFinish(2, false), TwoLevelFinish(1, false), ;

    private val role: Int
    val draging: Boolean
    val opening: Boolean

    constructor(role: Int, draging: Boolean):this(role,draging,false)

    constructor(role: Int, draging: Boolean, opening: Boolean){
        this@RefreshState.role = role
        this@RefreshState.draging = draging
        this@RefreshState.opening = opening

    }

    fun isHeader(): Boolean {
        return role == 1
    }

    fun isFooter(): Boolean {
        return role == 2
    }
}