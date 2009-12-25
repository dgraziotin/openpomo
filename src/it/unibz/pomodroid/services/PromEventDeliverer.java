package it.unibz.pomodroid.services;

import android.util.Log;

import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.XmlRpcClient;

/**
 * @author Thomas Schievenin
 *
 * A class that saves into PROM our information 
 */
public class PromEventDeliverer {
	private String appName = "Pomodroid";
	private String promDB = "prom";

	/**
	 * @param user
	 * @return nuber of id uploaded
	 */
	public Integer getUploadId(User user) {
		Object[] params = { new Integer(123), appName, promDB };
		Object result = XmlRpcClient.fetchSingleResult(user.getPromUrl(),
				"upload.getUploadID", params);
		return (Integer) result;
	}

	/**
	 * Upload the data
	 * 
	 * @param zipIni
	 * @param user
	 */
	public void uploadData(byte[] zipIni, User user) {
		try {

			Integer uploadId = getUploadId(user);

			Log.i("PromEventDeliverer.uploadData()", "Upload ID:" + uploadId);
			Object[] params = { uploadId, appName, promDB, zipIni };

			Object ret = XmlRpcClient.fetchSingleResult(user.getPromUrl(),
					"zipupload.uploadData", params);

			if (((Integer) ret).intValue() >= 0) {
				// TODO: delete events
			}

		} catch (Exception e) {
			Log.e("PromEventDeliverer.uploadData()", "Tranfer problem: "
					+ e.getMessage());
		}
	}

	/**
	 * Check if the data have been uploaded correctly
	 * 
	 * @param zipIni
	 * @param user
	 * @return
	 */
	public int testUploadData(byte[] zipIni, User user) {
		try {

			Integer uploadId = getUploadId(user);

			Log.i("PromEventDeliverer.testUploadData()", "Upload ID:"
					+ uploadId);
			Object[] params = { uploadId, appName, promDB, zipIni };

			Object ret = XmlRpcClient.fetchSingleResult(user.getPromUrl(),
					"zipupload.uploadData", params);

			if (((Integer) ret).intValue() >= 0) {
				return ((Integer) ret).intValue();
			}else{
				return -1;
			}

		} catch (Exception e) {
			Log.e("PromEventDeliverer.uploadData()", "Tranfer problem: "
					+ e.getMessage());
			return -1;
		}
	}

}
