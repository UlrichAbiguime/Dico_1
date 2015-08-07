package dico.wule.tg.dico.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by lakiu_000 on 4/4/2015.
 */
public class DatabaseInterfaceService extends Service {


    private IBinder myBinder = new DataTransBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // we may init all the different kind of list or
        // map or arrays inside of this


    }

    // the most important, each time there is any action on the database, update the data.
    public class DataTransBinder extends Binder {
        public DataTransBinder getService() {  return DataTransBinder.this; }
    }

}
