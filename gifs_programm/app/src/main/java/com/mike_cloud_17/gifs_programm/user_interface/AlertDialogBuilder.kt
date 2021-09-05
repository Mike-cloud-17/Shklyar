package com.mike_cloud_17.gifs_programm.user_interface

import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mike_cloud_17.gifs_programm.R
import timber.log.Timber

class AlertDialogBuilder(private val activity: AppCompatActivity) {
    private val authorName: String = activity.resources.getString(R.string.author_name)
    private val authorEmail: String = activity.resources.getString(R.string.author_email)
    private val dialogTitle: String = activity.resources.getString(R.string.dialog_info_title)
    private val dialogCloseText: String = activity.resources.getString(R.string.dialog_info_close)

    init {
        Timber.plant(Timber.DebugTree())
    }

    fun build(): AlertDialog.Builder {
        return AlertDialog.Builder(activity).apply {
            try {
                setMessage(
                    "${activity.title} ver ${
                        activity.packageManager.getPackageInfo(
                            activity.packageName,
                            0
                        ).versionName
                    }\nby $authorName\nemail: $authorEmail."
                )
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e)
            }
            setTitle(dialogTitle)
            setCancelable(true)
            setNeutralButton(dialogCloseText) { dialog, _ ->
                dialog?.dismiss()
            }
            setIcon(R.mipmap.ic_launcher_round)
            create()

            Timber.i("Dialog was been created.")
        }
    }
}