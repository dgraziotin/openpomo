package it.unibz.pomodroid.services;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.util.Log;

import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.services.XmlRpcClient;

public class PromEventDeliverer {
	private int getUploadId(User user){
		Object[] params = {new Integer(2),new String(""),new String("")};
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
					"namaah",
					"database",
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
