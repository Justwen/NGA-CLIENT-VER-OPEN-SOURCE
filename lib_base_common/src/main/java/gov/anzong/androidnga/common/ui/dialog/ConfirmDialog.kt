package gov.anzong.androidnga.common.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

class ConfirmDialog(private var mMessage: CharSequence, private var mActionRunnable: Runnable) : DialogFragment() {

    companion object {
        fun showConfirmDialog(activity: FragmentActivity, message: CharSequence, action: Runnable) {
            try {
                ConfirmDialog(message, action).show(activity.supportFragmentManager, null);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!);
        builder.setMessage(mMessage)
                .setPositiveButton(android.R.string.ok) { dialog, which -> mActionRunnable.run() }
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }
}