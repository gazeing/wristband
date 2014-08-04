package com.antcorp.anto.nfc;

import java.util.Observable;

public class NfcWrittenObserver extends Observable {

    /**
     *@param value
     * the value to set
     */
    public void tagWritten() {
       
        setChanged();
        notifyObservers(this);
    }
}
