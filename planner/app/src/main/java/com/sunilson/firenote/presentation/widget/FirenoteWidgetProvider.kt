package com.sunilson.firenote.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.typeBundle
import com.sunilson.firenote.presentation.shared.typeChecklist
import com.sunilson.firenote.presentation.shared.typeNote
import dagger.android.AndroidInjection
import javax.inject.Inject

class FirenoteWidgetProvider @Inject constructor() : AppWidgetProvider() {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            val noteIntent = Intent(context, MainActivity::class.java)
            noteIntent.putExtra("noteType", typeNote)
            val pendingNoteIntent = PendingIntent.getActivity(context, 0, noteIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val checklistIntent = Intent(context, MainActivity::class.java)
            checklistIntent.putExtra("noteType", typeChecklist)
            val pendingChecklistIntent = PendingIntent.getActivity(context, 0, checklistIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val bundleIntent = Intent(context, MainActivity::class.java)
            bundleIntent.putExtra("noteType", typeBundle)
            val pendingBundleIntent = PendingIntent.getActivity(context, 0, bundleIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val views = RemoteViews(context?.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_button_checklist, pendingChecklistIntent)
            views.setOnClickPendingIntent(R.id.widget_button_bundle, pendingBundleIntent)
            views.setOnClickPendingIntent(R.id.widget_button_note, pendingNoteIntent)

            val svcIntent = Intent(context, WidgetRemoteViewsService::class.java)
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it)
            svcIntent.data = Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME))
            views.setRemoteAdapter(R.id.widget_listview, svcIntent)

            appWidgetManager?.notifyAppWidgetViewDataChanged(it, R.id.widget_listview)
            appWidgetManager?.updateAppWidget(it, views)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}