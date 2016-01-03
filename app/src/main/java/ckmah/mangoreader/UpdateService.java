package ckmah.mangoreader;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.william.mangoreader.R;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ckmah.mangoreader.activity.MangoReaderActivity;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import ckmah.mangoreader.parse.MangaEden;

public class UpdateService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * Used to name the worker thread, important only for debugging.
     */
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            Log.d("UpdateService", "starting");
            // Synchronously download list of all manga
            MangaEden.MangaEdenList list = MangaEden
                    .getMangaEdenService(this)
                    .listAllManga()
                    .execute()
                    .body();

            List<MangaEdenMangaListItem> updated = new ArrayList<>();

            for (MangaEdenMangaListItem item : list.manga) {
                // Check whether manga was released in the last 24 hours. TODO longer window?
                DateTime lastChapterDate = new DateTime(item.lastChapterDate * 1000L); // Convert ms to sec
                DateTime yesterday = new DateTime().minusDays(1);
                if (lastChapterDate.isAfter(yesterday)) {

                    // Check whether manga is in library
                    if (UserLibraryHelper.isInLibrary(item)) {
                        Log.d("UpdateService", lastChapterDate.toString() + " -- " + item.title);
                        updated.add(item);
                    }
                }
            }
            notify(updated);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notify(List<MangaEdenMangaListItem> updated) {
        String message;
        switch (updated.size()) {
            case 0:
                // Nothing updated, exit without notification
                return;
            case 1:
                // Only 1 item updated
                message = String.format("%s was updated");
                break;
            default:
                // Multiple updated, find newest one
                MangaEdenMangaListItem newest = Collections.min(updated, new Comparator<MangaEdenMangaListItem>() {
                    @Override
                    public int compare(MangaEdenMangaListItem lhs, MangaEdenMangaListItem rhs) {
                        return (int) (lhs.lastChapterDate - rhs.lastChapterDate);
                    }
                });
                message = String.format("%s (+%d) were updated", newest.title, updated.size() - 1);
        }


        // Sets up the notification
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(message)
                        .setContentText("Touch to read.");


        // Launch MangoReaderActivity when notification is clicked
        Intent resultIntent = new Intent(this, MangoReaderActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
