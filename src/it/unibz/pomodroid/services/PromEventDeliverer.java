package it.unibz.pomodroid.services;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.XmlRpcClient;

public class PromEventDeliverer {
	public int getUploadId(User user){
		Object[] params = {new Integer(1),new String("testtt"),new String("testtttt")};
		Integer result = (Integer) XmlRpcClient.fetchSingleResult("http://task3.cc:8080", "upload.getUploadID",params);
		return result.intValue();
	}
}
