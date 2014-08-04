package com.antcorp.anto.nfc;

import java.io.IOException;




import com.antcorp.anto.widget.MyLog;

import android.annotation.TargetApi;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class AsyncLockTag extends AsyncTask<Object, Void, Boolean> {

	@Override
	protected Boolean doInBackground(Object... arg0) {
		Object n = null;
		if ((arg0 != null) && (arg0.length > 0)) {
			n = arg0[0];
			if (n instanceof Ndef) {
				Ndef ndef = (Ndef) n;
				try {
					ndef.connect();
					boolean isSuccess = ndef.makeReadOnly();
					ndef.close();
					return isSuccess;
				} catch (IOException e) {
					MyLog.i(e.getMessage());
				}
			}
		}
		return null;
	}

}
