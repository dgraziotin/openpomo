package it.unibz.pomodroid.services;
import android.util.Log;

import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.XmlRpcClient;


public class PromEventDeliverer {
	private String appName = "Pomodroid";
	private String promDB = "prom";
	
	public Integer getUploadId(User user) throws Exception{
		Object[] params = {new Integer(123),appName,promDB};
		Object result = XmlRpcClient.fetchSingleResult(user.getPromUrl(), "upload.getUploadID",params);
		return (Integer) result;
	}
	
public void uploadData(byte[] zipIni, User user) {
		try {
		
			Integer uploadId = getUploadId(user);
			
			Log.i("PromEventDeliverer.uploadData()", "Upload ID:" + uploadId);
			Object[] params = {
					uploadId,
					appName,
					promDB,
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
