package com.eelly.holder

import com.eelly.model.MovieBean
import java.util.ArrayList

/**
 * @author vurtne on 1-Mar-18.
 */
class SortHolder {

    companion object {
        /**
         * Fatal Exception: java.util.ConcurrentModificationException
         * 目前会报这个错误,解决方法,就是需要对此集合进行同步,但可能更影响效率,故暂且换掉
         */
        fun onQuickSort(list: List<MovieBean>): List<MovieBean> {
            val moves = ArrayList<MovieBean>()
            moves.addAll(list)
            onQuickSort(moves, 0, moves.size - 1)
            return moves
        }

        private fun onQuickSort(list: MutableList<MovieBean>, left: Int, right: Int) {
            if (left >= right) {
                return
            }
            var i = left
            var j = right
            val key = list[left]
            while (i < j) {
                while (i < j && list[j].rating.stars <= key.rating.stars) {
                    j--
                }
                list[i] = list[j]
                while (i < j && list[i].rating.stars > key.rating.stars) {
                    i++
                }
                list[j] = list[i]
            }
            list[i] = key
            onQuickSort(list, left, i - 1)
            onQuickSort(list, i + 1, right)
        }
    }
}