package android.course.books_312316433_313601130.Services;

import java.util.List;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.course.books_312316433_313601130.Activities.MainActivity;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Network.NetworkConnector;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.course.books_312316433_313601130.Utils.CyclerManager;
import android.course.books_312316433_313601130.Utils.DateUtil;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotifyierService extends Service implements IResultReceiver {
	// broadcast receiver name/action
	public static final String BROADCAST = "android.course.a_b_c.ActivityReceiver";
	 // Notification ID to allow for future updates
	public static final int MY_NOTIFICATION_ID = 1;
	private static final String CHANNEL_ID = "Books_channel";
	public NotifyierService() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

			NetworkConnector.getInstance().initialize(getApplicationContext());
			boolean wasClosed = DataHandler.getInstance().dbIsClosed();

			if (wasClosed) {
				DataHandler.getInstance().initDataBase(getApplicationContext());

			}
			if (DataHandler.getInstance().getUser() == null){
				if(DataHandler.getInstance().initUser() != null){
					CyclerManager.getInstance().start();
					CyclerManager.getInstance().subscribeForResults(this);
				}
			}else {
				CyclerManager.getInstance().start();
				CyclerManager.getInstance().subscribeForResults(this);
			}


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		CyclerManager.getInstance().unsubscibeForResults(this);
		CyclerManager.getInstance().stop();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;

	}

	@Override
	public void onRecieve(List<JSONObject> results) {
		notifyUser(results);
	}

	private void notifyUser(List<JSONObject> results) {

		JSONArray arr;
		JSONObject j;
		String username, type, date;

		// building notification string
		for(JSONObject s : results) {
			try {
				arr = s.getJSONArray(Constants.NOTIFICATIONS);

				for (int i = 0;i < arr.length(); i++){
					j = arr.getJSONObject(i);
					type = j.getString(Constants.TYPE);
					username = j.getString(Constants.SENDER);
					date = DateUtil.getCurrentDate(j.getLong(Constants.DATE));

					showNotific(type, username, date);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private void showNotific(String type, String username, String date) {

		// Notification Action Elements
		Intent mNotificationIntent;
		PendingIntent mContentIntent;

		// Define the Notification's expanded message and Intent:
		// Notification Sound and Vibration on Arrival
		Uri soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		long[] mVibratePattern = { 0, 200, 200, 300 };

		// Notification Text Elements
		String contentTitle = "Books";

		StringBuilder sb = new StringBuilder();
		sb.append(username);
		if (type.equals(Constants.COMMENT)) {
			sb.append("\nCommented on one of your story chapters.\n");
		}else if (type.equals(Constants.ACTIVITIES)){
			sb.append("\nPosted an activity status.\n");
		}else if (type.equals(Constants.FOLLOWING)){
			sb.append("\nFollowed you.\n");
		}else if (type.equals(Constants.MESSAGE)){
			sb.append("\nSent you a message.\n");
		}else if (type.equals(Constants.REPLIES)){
			sb.append("\nReplied to one of your activity statuses.\n");
		}

		sb.append(date);
		mNotificationIntent = new Intent(getApplicationContext(), MainActivity.class);
		mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mContentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mNotificationIntent,
				Intent.FILL_IN_ACTION);
		Intent intent = new Intent();
		intent.setAction(BROADCAST);

		// sending broadcast to activity receiver
		getApplicationContext().sendBroadcast(intent);

		// building notification
		String contentText = sb.toString();

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

		notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setContentTitle(contentTitle);
		notificationBuilder.setContentText(contentText);
		notificationBuilder.setGroup(type);
		notificationBuilder.setContentIntent(mContentIntent).setSound(soundURI);
		notificationBuilder.setVibrate(mVibratePattern);
		notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
					getString(R.string.channel_name),
					NotificationManager.IMPORTANCE_HIGH);
			mNotificationManager.createNotificationChannel(channel);
		}

		// Pass the Notification to the NotificationManager:
		mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
	}

}
