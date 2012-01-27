/**
 * This file is part of Pomodroid.
 *
 *   Pomodroid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Pomodroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Pomodroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.task3.pomosimple;

import cc.task3.pomosimple.models.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class implements graphically the pomodoro technique. Here we have the
 * counter and the description of the activity to be faced.
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see android.view.View.OnClickListener;
 */

public class Pomodoro extends SharedActivity {

    /**
     * Default value when dimming light
     */
    private final static float DESIRED_BRIGHTNESS = 0.05f;
    /**
     * How many seconds are in a minute
     */
    private final static int SECONDS_PER_MINUTE = 60;

    /**
     * Dummy ID for our notifications
     */
    private final static int NOTIFICATION_ID = 1;

    private int pomodoroSecondsLength = -1;

    /**
     * Holds remaining time in seconds
     */
    private int pomodoroSecondsValue = -1;

    private Activity selectedActivity;
    /**
     * Wake variable for Power Manager
     */
    private PowerManager.WakeLock wakeLock;
    /**
     * Will store the brightness value set by user
     */
    private float originalBrightness = -1;
    /**
     * Messenger for communicating with service.
     */
    Messenger mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mIsBound;
    /**
     * Boolean that indicates whether the Pomodoro is running or not
     */
    boolean isRunning = false;


    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PomodoroService.MSG_POMODORO_START:
                    Integer numberPomodoro1 = msg.getData().getInt("numberPomodoro");
                    String pomodoroMessage1 = msg.getData().getString("pomodoroMessage");
                    TextView atvActivityNumberPomodoro1 = (TextView) findViewById(R.id.atvActivityNumberPomodoro);
                    atvActivityNumberPomodoro1
                            .setText("Number of pomodoro: "
                                    + numberPomodoro1.toString());
                    notifyUser(pomodoroMessage1, false);
                    break;
                case PomodoroService.MSG_POMODORO_TICK:
                    isRunning = true;
                    pomodoroSecondsValue = msg.getData().getInt(
                            "pomodoroSecondsValue");
                    updateProgressbarAndTimer(pomodoroSecondsValue);
                    break;
                case PomodoroService.MSG_POMODORO_FINISHED:
                    if (getUser().isDimLight())
                        setBrightness(originalBrightness);
                    pomodoroSecondsValue = msg.getData().getInt(
                            "pomodoroSecondsValue");
                    isRunning = false;
                    updateProgressbarAndTimer(pomodoroSecondsValue);

                    Integer numberPomodoro = msg.getData().getInt("numberPomodoro");
                    String pomodoroMessage = msg.getData().getString("pomodoroMessage");
                    TextView atvActivityNumberPomodoro = (TextView) findViewById(R.id.atvActivityNumberPomodoro);
                    atvActivityNumberPomodoro
                            .setText("Number of pomodoro: "
                                    + numberPomodoro.toString());
                    notifyUser(pomodoroMessage, true);

                    break;
                case PomodoroService.MSG_POMODORO_STOP:
                    isRunning = false;
                    Integer numberInterruptions = msg.getData().getInt(
                            "numberInterruptions");
                    TextView TextViewActivityNumberInterruptions = (TextView) findViewById(R.id.atvActivityNumberInterruptions);
                    TextViewActivityNumberInterruptions.setText("Number of interruptions: "
                            + numberInterruptions.toString());
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private IncomingHandler handler = new IncomingHandler();

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(handler);

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null,
                        PomodoroService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                //no need to do anything here
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(Pomodoro.this, PomodoroService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            PomodoroService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // ne need to do anything here
                }
            }
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private void sendMessenger(int message) {
        Message msg = Message.obtain(null, message, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the Timer TextView, given the new timer value in seconds.
     * It also updates the progress bar
     *
     * @param pomodoroSecondsValue
     */
    private void updateProgressbarAndTimer(int pomodoroSecondsValue) {
        TextView atvPomodoroTimer = (TextView) findViewById(R.id.atvPomodoroTimer);

        int seconds = pomodoroSecondsValue;
        int minutes = seconds / SECONDS_PER_MINUTE;
        seconds = seconds % SECONDS_PER_MINUTE;

        ProgressBar apbProgressBar = (ProgressBar) findViewById(R.id.apbProgressBar);
        apbProgressBar.setProgress(100 - ((pomodoroSecondsValue * 100) / pomodoroSecondsLength));

        if (seconds >= 10 && seconds < 60) {
            if (minutes < 10)
                atvPomodoroTimer.setText("0" + minutes + ":" + seconds);
            else
                atvPomodoroTimer.setText(minutes + ":" + seconds);
        } else if (seconds < 10) {
            if (minutes < 10)
                atvPomodoroTimer.setText("0" + minutes + ":0" + seconds);
            else
                atvPomodoroTimer.setText(minutes + ":0" + seconds);
        } else {
            atvPomodoroTimer.setText("" + minutes + ":" + seconds);
        }
    }

    /**
     * Notifies the user in various ways
     *
     * @param message The message we want to display to the user
     */
    private void notifyUser(String message, boolean vibrate) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.pomo_red,
                "Pomodroid", System.currentTimeMillis());
        Intent intent = new Intent(context, Pomodoro.class);
        TextView atvActivitySummary = (TextView) findViewById(R.id.atvActivitySummary);
        String activitySummary = (String) atvActivitySummary.getText();
        notification.setLatestEventInfo(Pomodoro.this, activitySummary,
                message, PendingIntent.getActivity(getBaseContext(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT));
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.ledARGB = 0xff00ff00;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        if (vibrate && getUser().isVibration())
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        nm.notify(NOTIFICATION_ID, notification);
        //TODO message sent in any case
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Sets the device brightness
     *
     * @param brightness
     */
    private void setBrightness(float brightness) {
        WindowManager.LayoutParams layoutParameters = getWindow()
                .getAttributes();
        layoutParameters.screenBrightness = brightness;
        getWindow().setAttributes(layoutParameters);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pomodoro);

        this.pomodoroSecondsValue = getUser().getPomodoroMinutesDuration() * SECONDS_PER_MINUTE;
        this.pomodoroSecondsLength = this.pomodoroSecondsValue;
        this.selectedActivity = getUser().getSelectedActivity();

        updateProgressbarAndTimer(pomodoroSecondsValue);

        TextView atvActivitySummary = (TextView) findViewById(R.id.atvActivitySummary);
        atvActivitySummary.setText(selectedActivity.getSummary() + " ("
                + selectedActivity.getStringDeadline() + ")");
        TextView atvActivityDescription = (TextView) findViewById(R.id.atvActivityDescription);
        atvActivityDescription.setText(selectedActivity.getDescription()
                + " (Given by: " + selectedActivity.getReporter() + ")");
        TextView atvActivityNumberPomodoro = (TextView) findViewById(R.id.atvActivityNumberPomodoro);
        Integer numberPomodoro = selectedActivity.getNumberPomodoro();
        atvActivityNumberPomodoro.setText("Number of pomodoro: "
                + numberPomodoro.toString());

        TextView TextViewActivityNumberInterruptions = (TextView) findViewById(R.id.atvActivityNumberInterruptions);
        Integer numberInterruptions = 0;
        numberInterruptions = selectedActivity.getNumberInterruptions();

        TextViewActivityNumberInterruptions.setText("Number of interruptions: "
                + numberInterruptions.toString());

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "DoNotDimScreen");

        this.originalBrightness = getWindow().getAttributes().screenBrightness;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, PomodoroService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        final Intent pomodoroService = new Intent(context,
                PomodoroService.class);
        startService(pomodoroService);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        if (isRunning) {
            String message = "Pomodoro running in background..";
            notifyUser(message, false);
        }
    }

    /**
     * We specify the menu labels and theirs icons
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, R.id.ACTION_POMODORO_START, 0, "Start").setIcon(
                R.drawable.ic_menu_play_clip);
        menu.add(0, R.id.ACTION_POMODORO_STOP, 0, "Stop").setIcon(
                R.drawable.ic_menu_stop);
        return true;
    }

    /**
     * As soon as the user clicks on the menu a new intent is created for adding new Activity.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.ScrollView01);
        switch (item.getItemId()) {
            case R.id.ACTION_POMODORO_START:
                if (isRunning)
                    return true;
                if (getUser().isDimLight())
                    setBrightness(Pomodoro.DESIRED_BRIGHTNESS);
                sendMessenger(PomodoroService.MSG_POMODORO_START);
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                isRunning = true;
                break;
            case R.id.ACTION_POMODORO_STOP:
                if (!isRunning)
                    return true;
                if (getUser().isDimLight())
                    setBrightness(originalBrightness);
                sendMessenger(PomodoroService.MSG_POMODORO_STOP);
                pomodoroSecondsValue = getUser().getPomodoroMinutesDuration() * SECONDS_PER_MINUTE;
                updateProgressbarAndTimer(pomodoroSecondsValue);
                notifyUser(getString(R.string.pomodoro_broken), true);
                isRunning = false;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}