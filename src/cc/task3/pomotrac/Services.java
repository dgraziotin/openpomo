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
package cc.task3.pomotrac;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import cc.task3.pomotrac.exceptions.PomodroidException;
import cc.task3.pomotrac.factories.ActivityFactory;
import cc.task3.pomotrac.models.*;

import cc.task3.pomotrac.services.TracTicketFetcher;
import cc.task3.pomotrac.services.XmlRpcClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * This class is in charge of retrieving tickets from a Service
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see cc.task3.pomotrac.SharedActivity
 */

public class Services extends SharedActivity {

    /**
     * Thread responsible for fetching the issues from Services
     */
    private Thread serviceThread = null;
    /**
     * Data structure to hold the issues fetched
     */
    private Vector<HashMap<String, Object>> serviceTasks = null;
    /**
     * Number of issues downloaded from Services
     */
    private int serviceTasksAdded = 0;
    /**
     * Represents the current service chosen for issue retrieval
     */
    private static int serviceChosen = -1;

    private static final int SERVICE_TRAC = 2;
    /**
     * Represents a positive message given to the Handler
     */
    private static final int MESSAGE_OK = 1;
    /**
     * Represents a negative message given to the Handler
     */
    private static final int MESSAGE_EXCEPTION = 2;
    /**
     * Represents an information message given to the Handler
     */
    private static final int MESSAGE_INFORMATION = 3;
    /**
     * Represents an information message given to the Handler
     */
    private static final int MESSAGE_ACTIVITY_DOWNLOADED = 4;

    private boolean isRunning = false;

    /**
     * Data structure to holds the Services to be queried
     */
    private List<Service> services = null;

    /**
     * @see cc.task3.pomotrac.SharedActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services);

        TextView atvServiceStatus = (TextView) findViewById(R.id.atvServiceStatus);
        atvServiceStatus.setText("Status: Idle.");
    }

    /**
     * Refreshes the list of active Services when the Activity gains focus
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            this.services = Service.getAllActive(super.getDbHelper());
            String message = "Active Services: ";
            if (this.services != null)
                message += "" + services.size();
            else
                message += "0";
            TextView atvServiceActiveNumb = (TextView) findViewById(R.id.atvServiceActiveNumb);
            atvServiceActiveNumb.setText(message);
        } catch (PomodroidException e) {
            e.alertUser(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * We specify the menu labels and their icons
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, R.id.ACTION_RETRIEVE_ACTIVITIES, 0, "Retrieve Activities").setIcon(
                android.R.drawable.ic_menu_add);
        menu.add(0, R.id.ACTION_ADD_SERVICE, 0, "Add a new Service").setIcon(
                android.R.drawable.ic_menu_add);
        menu.add(0, R.id.ACTION_LIST_SERVICES, 0, "Edit Services").setIcon(
                android.R.drawable.ic_menu_edit);
        return true;
    }

    /**
     * Method that starts a thread and shows a nice download bar.
     *
     * @throws PomodroidException
     */
    public void useServices() throws PomodroidException {
        List<Service> services = Service.getAllActive(super.getDbHelper());
        if (services == null || services.size() == 0) {
            PomodroidException.createAlert(context, "INFO",
                    "No active Services");
            return;
        }
        // create a new Thread that executes activityRetriever and start it
        this.serviceThread = new Thread(null, useServices, "UserServiceThread");
        this.serviceThread.start();
    }

    /**
     * As soon as a thread starts, this method is called. It retrieves Issues
     * from a Service. When the operation is finished, it sends an empty message
     * to the handler in order to inform the system that the operation is
     * finished.
     */
    protected Runnable useServices = new Runnable() {
        @Override
        public void run() {
            retrieveIssuesFromService();
        }
    };

    /**
     * This handler waits until the method run() sends an empty message in order
     * to inform us that the "retrieving phase" is finished.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            TextView atvServiceStatus = (TextView) findViewById(R.id.atvServiceStatus);
            switch (message.what) {
                case Services.MESSAGE_OK:
                    Services.this.isRunning = false;
                    serviceThread.interrupt();
                    Bundle okBundle = message.getData();
                    PomodroidException.createAlert(context, "INFO",
                            okBundle.getString("message"));
                    atvServiceStatus.setText("Status: Idle.");
                    break;
                case Services.MESSAGE_EXCEPTION:
                    Services.this.isRunning = false;
                    serviceThread.interrupt();
                    Bundle exceptionBundle = message.getData();
                    PomodroidException.createAlert(context, "ERROR",
                            exceptionBundle.getString("message"));
                    atvServiceStatus.setText("Status: Idle.");
                    break;
                case Services.MESSAGE_INFORMATION:
                    Bundle informationBundle = message.getData();
                    PomodroidException.createAlert(context, "INFO",
                            informationBundle.getString("message"));
                    break;
                case Services.MESSAGE_ACTIVITY_DOWNLOADED:
                    Bundle activityDownloadedBundle = message.getData();
                    atvServiceStatus.setText("Status: Downloading from "
                            + activityDownloadedBundle.getString("message"));
                    break;

            }
        }

    };

    /**
     * This method takes all not-closed tickets from the Service, then inserts
     * them into the local DB.
     *
     * @throws PomodroidException
     */
    private void retrieveIssuesFromService() {
        try {
            TracTicketFetcher tracTicketFetcher = new TracTicketFetcher();
            ActivityFactory activityFactory = new ActivityFactory();
            List<Service> services = Service.getAllActive(super.getDbHelper());
            sendMessageHandler(Services.MESSAGE_INFORMATION,
                    "Getting Issues, please wait..");
            for (Service service : services) {
                sendMessageHandler(Services.MESSAGE_ACTIVITY_DOWNLOADED,
                        service.getName());
                this.serviceTasks = tracTicketFetcher.fetch(service,
                        super.getDbHelper());
                this.serviceTasksAdded = activityFactory.produce(
                        this.serviceTasks, super.getDbHelper());
                sendMessageHandler(Services.MESSAGE_INFORMATION,
                        "Downloaded " + serviceTasks.size() + " issues from "
                                + service.getName());
            }
        } catch (Exception e) {
            sendMessageHandler(Services.MESSAGE_EXCEPTION, e.toString());
            return;
        }
        sendMessageHandler(Services.MESSAGE_OK,
                "Finished to download new Issues");

    }

    /**
     * Sends a customized message to the Handler.
     *
     * @param messageType  the type of message
     * @param messageValue the text of the message
     */
    private void sendMessageHandler(int messageType, String messageValue) {
        Message message = new Message();
        if (!messageValue.equals("")) {
            Bundle bundle = new Bundle();
            bundle.putString("message", messageValue);
            message.setData(bundle);
        }
        message.what = messageType;
        handler.sendMessage(message);
    }

    /**
     * As soon as the user clicks on the menu a new intent is created, for
     * either scroll the list of Services or add a new Service.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ACTION_ADD_SERVICE:
                startActivity(EditService.class);
                return true;
            case R.id.ACTION_LIST_SERVICES:
                startActivity(ListServices.class);
                return true;
            case R.id.ACTION_RETRIEVE_ACTIVITIES:
                if (this.isRunning)
                    return true;
                if (XmlRpcClient.isInternetAvailable(context)) {
                    try {
                        this.isRunning = true;
                        useServices();
                    } catch (PomodroidException e) {
                        e.alertUser(context);
                    }

                } else {
                    PomodroidException.createAlert(context, "ERROR",
                            context.getString(R.string.no_internet_available));
                }
        }
        return false;
    }

}
