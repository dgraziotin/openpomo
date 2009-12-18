package it.unibz.pomodroid.services;
import android.util.Log;

import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.XmlRpcClient;
import it.unibz.pomodroid.R;

public class PromEventDeliverer {
	private int getUploadId(User user){
		Object[] params = {new Integer(123),R.string.app_name,"prom"};
		Object result = XmlRpcClient.fetchSingleResult(user.getPromUrl(), "upload.getUploadID",params);
		Integer id = (Integer) result;
		return id.intValue();
	}
	
public void uploadData(byte[] zipIni, User user) {
		try {
		
			Integer uploadId = getUploadId(user);
			
			Log.i("PromEventDeliverer.uploadData()", "Upload ID:" + uploadId);
			Object[] params = {
					uploadId,
					R.string.app_name,
					"prom",
					zipIni
			};
			
			Object ret = XmlRpcClient.fetchSingleResult(user.getPromUrl(), "zipupload.uploadData", params);
	        
			if (((Integer) ret).intValue() >= 0) {
				//TODO: delete events
			}

        } catch (Exception e) {
        	Log.e("PromEventDeliverer.uploadData()", "Tranfer problem: " + e.getMessage());
		} 
	}
	
}
