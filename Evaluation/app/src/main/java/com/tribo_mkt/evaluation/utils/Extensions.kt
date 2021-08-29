package com.tribo_mkt.evaluation.utils

import android.app.ActionBar
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.tribo_mkt.evaluation.R

fun Context.createDialog(block: MaterialAlertDialogBuilder.() -> Unit = {}): AlertDialog {
    val builder = MaterialAlertDialogBuilder(this)
    builder.setPositiveButton(android.R.string.ok, null)
    block(builder)
    return builder.create()
}

fun Context.createProgressDialog(): AlertDialog {

    val dialog = createDialog {
        val padding =
            this@createProgressDialog.resources.getDimensionPixelOffset(R.dimen.layout_padding)
        val progressBar = ProgressBar(this@createProgressDialog)
        progressBar.setPadding(padding, padding, padding, padding)
        progressBar.indeterminateTintList = ColorStateList.valueOf(Color.parseColor("#FF7A00"))
        setView(progressBar)
        setPositiveButton(null, null)
        setCancelable(false)
    }
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    return dialog
}

fun createNetInfoSnackBar(view: View): Snackbar{

        val snackBarView =
            Snackbar.make(view, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
        val layoutParams = ActionBar.LayoutParams(snackBarView.view.layoutParams)
        layoutParams.gravity = Gravity.TOP
        snackBarView.view.layoutParams = layoutParams
        snackBarView.setTextColor(Color.WHITE)
        val mTextView: TextView =
            snackBarView.view.findViewById(com.google.android.material.R.id.snackbar_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER;
        } else {
            mTextView.gravity = Gravity.CENTER_HORIZONTAL;
        }
        snackBarView.view.setBackgroundColor(Color.parseColor("#DE90A4AE"))
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE

    return snackBarView
}

fun hideRefreshingBarIfIsOn(view: SwipeRefreshLayout){
    if(view.isRefreshing){
        view.isRefreshing = false
    }
}



