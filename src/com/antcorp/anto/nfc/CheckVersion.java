package com.antcorp.anto.nfc;

public class CheckVersion {
	 
	public static boolean CheckVersion8(){//to see if the version newer than 8
		return (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) ;
		     
		
	}
	public static boolean CheckVersion10(){//to see if the version newer than 10
		return (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) ;
		     
		
	}
	
	public static boolean CheckVersion11(){//to see if the version newer than 11
		
		return  (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) ;
	}
	
	public static boolean CheckVersion14(){//to see if the version newer than 14
		
		return  (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) ;
	}
	
	public static boolean CheckVersion17(){//to see if the version newer than 14
		
		return  (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) ;
	}

}
