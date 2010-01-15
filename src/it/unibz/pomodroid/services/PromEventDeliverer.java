package it.unibz.pomodroid.services;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.XmlRpcClient;

/**
 * A class that saves into PROM our information 
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 *
 */
public class PromEventDeliverer {
	private String appName = "Pomodroid";
	private String promDB = "prom";
	
	
	/**
	 * @param user
	 * @return nuber of id uploaded
	 * @throws PomodroidException 
	 */
	public Integer getUploadId(User user) throws PomodroidException {
		Object[] params = { new Integer(123), appName, promDB };
		Object result = XmlRpcClient.fetchSingleResult(user.getPromUrl(),"upload.getUploadID", params);
		return (Integer) result;
	}

	/**
	 * Upload the data
	 * 
	 * @param zipIni
	 * @param user
	 * @throws PomodroidException 
	 */
	public boolean uploadData(byte[] zipIni, User user) throws PomodroidException {
		try {
			
			
			if (zipIni==null || zipIni.length==0){
				return false;
			}
			Integer uploadId = getUploadId(user);

			Object[] params = { uploadId, appName, promDB, zipIni };

			Object ret = XmlRpcClient.fetchSingleResult(user.getPromUrl(),
					"zipupload.uploadData", params);
			
			if (((Integer) ret).intValue() >= 0)
				return true;
			else
				return false;
			

		} catch (Exception e) {
			throw new PomodroidException(e.toString());
		}
		
	}

	/**
	 * Check if the data have been uploaded correctly
	 * 
	 * @param zipIni
	 * @param user
	 * @return
	 * @throws PomodroidException 
	 */
	public int testUploadData(byte[] zipIni, User user) throws PomodroidException {
		try {

			Integer uploadId = getUploadId(user);

			Object[] params = { uploadId, appName, promDB, zipIni };

			Object ret = XmlRpcClient.fetchSingleResult(user.getPromUrl(),
					"zipupload.uploadData", params);

			if (((Integer) ret).intValue() >= 0) {
				return ((Integer) ret).intValue();
			}else{
				return -1;
			}

		} catch (Exception e) {
			throw new PomodroidException("ERROR in PromEventDeliverer.testUploadData() transer problem: "+e.toString());
		}
	}

}
