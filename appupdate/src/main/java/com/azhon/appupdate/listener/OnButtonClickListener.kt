package com.azhon.appupdate.listener


/**
 * createDate:  2022/4/7 on 15:56
 * desc:
 *
 * @author azhon
 */

interface OnButtonClickListener {
    companion object {
        /**
         * click update button
         */
        const val UPDATE = 0

        /**
         * click cancel button
         */
        const val CANCEL = 1
    }

    fun onButtonClick(id: Int)
}