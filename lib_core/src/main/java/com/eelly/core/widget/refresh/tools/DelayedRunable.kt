package com.eelly.core.widget.refresh.tools

/**
 * @author Vurtne on 14-Dec-17.
 */
class DelayedRunable :Runnable{

    var delayMillis: Long = 0
    var runnable: Runnable? = null
    constructor(runnable: Runnable){
        this.runnable = runnable
    }

    constructor(runnable: Runnable, delayMillis: Long){
        this.runnable = runnable
        this.delayMillis = delayMillis
    }

    override fun run() {
        try {
            if (runnable != null) {
                runnable!!.run()
                runnable = null
            }
        } catch (ignored: Throwable) {
        }

    }
}