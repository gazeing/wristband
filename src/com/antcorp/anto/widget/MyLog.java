package com.antcorp.anto.widget;

import android.os.Debug;
import android.util.Log;

public class MyLog {
	
	public static boolean isLogging =  Debug.isDebuggerConnected();
	public static void i(String tag,String test){
		if ((tag==null)||(test==null))
			return;
		if(isLogging){
			
			
			if (test.length() > 4000) {
			    Log.i(tag, "text.length = " + test.length());
			    int chunkCount = test.length() / 4000;     // integer division
			    for (int i = 0; i <= chunkCount; i++) {
			        int max = 4000 * (i + 1);
			        if (max >= test.length()) {
			            Log.i(tag, "chunk " + (i+1) + " of " + (chunkCount+1) + ":" + test.substring(4000 * i));
			        } else {
			            Log.i(tag, "chunk " + (i+1) + " of " + (chunkCount+1) + ":" + test.substring(4000 * i, max));
			        }
			    }
			}else
				Log.i(tag,test);
		}
	}
	public static void i(String test){
		if(isLogging)
			i("antcorp",test);

	}
	
	public static void i(Exception e){
		if(isLogging)
			i("XXXXXXXXXXXXXXXXXXXXXXXXXError!!    "+e.getMessage());
	}

}
