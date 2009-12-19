package org.xmlrpc;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.HttpHostConnectException;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Test extends Activity {

	private XMLRPCClient client;
	private URI uri;
	private DateFormat dateFormat;
	private DateFormat timeFormat;
	private Drawable errorDrawable;

	private TextView status;
	private TextSwitcher testResult;
	private ListView tests;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uri = URI.create("http://10.0.2.2:8888");
		client = new XMLRPCClient(uri);

		setContentView(R.layout.main);
        testResult = (TextSwitcher) findViewById(R.id.text_result);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        View v0 = inflater.inflate(R.layout.text_view, null);
        View v1 = inflater.inflate(R.layout.text_view, null);
        LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        testResult.addView(v0, 0, params);
        testResult.addView(v1, 1, params);
        testResult.setText("WARNING, before calling any test make sure server.py is running !!!");

        Animation inAnim = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
//        Animation inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
//        Animation outAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        inAnim.setStartOffset(250);
        testResult.setInAnimation(inAnim);
        testResult.setOutAnimation(outAnim);
        errorDrawable = getResources().getDrawable(R.drawable.error);
        errorDrawable.setBounds(0, 0, errorDrawable.getIntrinsicWidth(), errorDrawable.getIntrinsicHeight());

        status = (TextView) findViewById(R.id.status);
		dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL);
		timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.DEFAULT);

		tests = (ListView) findViewById(R.id.tests);
		ArrayAdapter<String> adapter = new TestAdapter(this, R.layout.test, R.id.title);
		adapter.add("add 3 to 3.6;in [int, float] out float");
		adapter.add("1 day from now;in/out Date");
		adapter.add("test string;in/out String");
		adapter.add("test struct;in/out Map");
		adapter.add("test array;in/out Object[]");
		adapter.add("desaturate image;in/out byte[]");
		adapter.add("invert random bool;in/out boolean");
		adapter.add("get huge string");
		adapter.add("get complex 2D array");
		tests.setAdapter(adapter);
		tests.setOnItemClickListener(testListener);
	}
	
	OnItemClickListener testListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {
		        XMLRPCMethod method = new XMLRPCMethod("add", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						testResult.setText(result.toString());
					}
		        });
		        Object[] params = {
		        		3,
		        		3.6f,
		        };
		        method.call(params);
			} else
			if (position == 1) {
		        XMLRPCMethod method = new XMLRPCMethod("addOneDay", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						testResult.setText(dateFormat.format(result) + "\n" + timeFormat.format(result));
					}
		        });
		        Object[] params = {
		        		new Date(),
		        };
		        method.call(params);
			} else
			if (position == 2) {
		        XMLRPCMethod method = new XMLRPCMethod("getHostName", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						testResult.setText(result.toString());
					}
		        });
		        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		        String ssn = "unknown";
		        if (manager != null) {
		        	ssn = manager.getSimSerialNumber();
		        	if (ssn == null) {
		        		ssn = "unknown";
		        	}
		        }
		        Object[] params = {
		        		ssn,
		        };
		        method.call(params);
			} else
			if (position == 3) {
		        XMLRPCMethod method = new XMLRPCMethod("testStruct", new XMLRPCMethodCallback() {
					@SuppressWarnings("unchecked")
					public void callFinished(Object result) {
						Map<String, Object> map = (Map<String, Object>) result;
						testResult.setText("City: " + map.get("City") + "\nTemperature: " + map.get("Temperature") + " °C");
					}
		        });
		        Map<String, Object> map = new HashMap<String, Object>();
		        map.put("City", "Barcelona");
		        Object[] params = {
		        		map,
		        };
		        method.call(params);
			} else
			if (position == 4) {
		        XMLRPCMethod method = new XMLRPCMethod("testArray", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						Object[] arr = (Object[]) result;
						testResult.setText("Sum: " + arr[0] + "\nLength: " + arr[1]);
					}
		        });
		        Object[] array = {
		        		1, 2, 3, 4, 5, 6
		        };
		        Object[] params = {
		        		array,
		        };
		        method.call(params);
			} else
			if (position == 5) {
		        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.android);
		        final Bitmap bIn = bd.getBitmap();
		        final int w = bIn.getWidth();
		        final int h = bIn.getHeight();

				XMLRPCMethod method = new XMLRPCMethod("desaturateImage", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						byte[] arr = (byte[]) result;
						ByteBuffer buffer = ByteBuffer.allocate(w * h * 4);
						buffer.order(ByteOrder.LITTLE_ENDIAN);
						buffer.put(arr);
						buffer.position(0);
						int[] iarr = new int[w * h];
						for (int i = 0; i < iarr.length; i++) {
							iarr[i] = buffer.getInt();
						}
						Bitmap bOut = Bitmap.createBitmap(iarr, w, h, Config.ARGB_8888);
						
						SpannableStringBuilder builder = new SpannableStringBuilder();
						builder.append("android in: ");
						int l = builder.length();
						builder.append("i");
						builder.setSpan(new ImageSpan(bIn), l, l+1, 0);
						builder.append("    ");
						builder.append("host out: ");
						l = builder.length();
						builder.append("i");
						builder.setSpan(new ImageSpan(bOut), l, l+1, 0);
						testResult.setText(builder);
					}
		        });
		        ByteBuffer buffer = ByteBuffer.allocate(w * h * 4);
				bIn.copyPixelsToBuffer(buffer);
		        Object[] params = {
		        		buffer.array(),
		        };
		        method.call(params);
			} else
			if (position == 6) {
		        XMLRPCMethod method = new XMLRPCMethod("invertBoolean", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						testResult.setText(result.toString());
					}
		        });
		        boolean b = Math.random()>0.5? true : false;
		        Object[] params = {
		        		b,
		        };
		        method.call(params);
			} else
			if (position == 7) {
				XMLRPCMethod method = new XMLRPCMethod("getHugeString", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						String hugeString = (String) result;
						testResult.setText("Got string with len == " + hugeString.length());
					}
				});
				method.call();
			} else
			if (position == 8) {
				XMLRPCMethod method = new XMLRPCMethod("get2DArray", new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						StringBuffer sb = new StringBuffer();
						sb.append("[");
						Object[] arrY = (Object[]) result;
						for (int y=0; y<arrY.length; y++) {
							Object[] arrX = (Object[]) arrY[y];
							sb.append("[");
							for (int x=0; x<arrX.length; x++) {
								Object object = arrX[x];
								sb.append(object);
								if (x + 1 < arrX.length) {
									sb.append(", ");
								}
							}
							sb.append("]");
							if (y + 1 < arrY.length) {
								sb.append(", ");
							}
						}
						sb.append("]");
						testResult.setText(sb.toString());
					}
				});
				method.call();
			}
		}
	};
	
	class TestAdapter extends ArrayAdapter<String> {
		private LayoutInflater layouter;
		private int layoutId;
		public TestAdapter(Context context, int layoutId, int textId) {
			super(context, layoutId, textId);
			this.layoutId = layoutId;
			layouter = LayoutInflater.from(Test.this);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = layouter.inflate(layoutId, null);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView params = (TextView) view.findViewById(R.id.params);
			String string = getItem(position);
			String[] arr = string.split(";");
			title.setText(arr[0]);
			if (arr.length == 2) {
				params.setText(arr[1]);
			} else {
				params.setVisibility(View.GONE);
			}
			return view;
		}
	}

	interface XMLRPCMethodCallback {
		void callFinished(Object result);
	}
	
	class XMLRPCMethod extends Thread {
		private String method;
		private Object[] params;
		private Handler handler;
		private XMLRPCMethodCallback callBack;
		public XMLRPCMethod(String method, XMLRPCMethodCallback callBack) {
			this.method = method;
			this.callBack = callBack;
			handler = new Handler();
		}
		public void call() {
			call(null);
		}
		public void call(Object[] params) {
			status.setTextColor(0xff80ff80);
			status.setError(null);
			status.setText("Calling host " + uri.getHost());
			tests.setEnabled(false);
			this.params = params;
			start();
		}
		@Override
		public void run() {
    		try {
    			final long t0 = System.currentTimeMillis();
    			final Object result = client.call(method, params);
    			final long t1 = System.currentTimeMillis();
    			handler.post(new Runnable() {
					public void run() {
						tests.setEnabled(true);
						status.setText("XML-RPC call took " + (t1-t0) + "ms");
						callBack.callFinished(result);
					}
    			});
    		} catch (final XMLRPCFault e) {
    			handler.post(new Runnable() {
					public void run() {
						testResult.setText("");
						tests.setEnabled(true);
						status.setTextColor(0xffff8080);
						status.setError("", errorDrawable);
						status.setText("Fault message: " + e.getFaultString() + "\nFault code: " + e.getFaultCode());
						Log.d("Test", "error", e);
					}
    			});
    		} catch (final XMLRPCException e) {
    			handler.post(new Runnable() {
					public void run() {
						testResult.setText("");
						tests.setEnabled(true);
						status.setTextColor(0xffff8080);
						status.setError("", errorDrawable);
						Throwable couse = e.getCause();
						if (couse instanceof HttpHostConnectException) {
							status.setText("Cannot connect to " + uri.getHost() + "\nMake sure server.py on your development host is running !!!");
						} else {
							status.setText("Error " + e.getMessage());
						}
						Log.d("Test", "error", e);
					}
    			});
    		}
		}
	}
}
