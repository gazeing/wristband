package com.antcorp.anto.network;

public class AntRetStatus {
    public AntRetStatus ()
    {
    }

    public final static int  OK = 0;
    public final static int  ERR = -1;

    public final static int USER_EXIST = 1000;
    public final static int CONNECTED  = 1001;
    public final static int NOT_CONNECTED = -100;
    
    
    public final static int SERVER_ERROR = -88888;

    public final static int INTERNAL_FAILURE = -99999;
}
