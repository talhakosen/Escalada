package escalada.talhakosen.com.escaladaassignment.core;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Created by Talha Kosen on 14/05/2015.
 */

public final class EscaladaApplication extends Application {
    private RequestQueue requestQueue;
    private static EscaladaApplication instance;

    // can be used to store something in application context
    public static EscaladaApplication getInstance() {
        if (instance == null)
            instance = new EscaladaApplication();

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  Network Stack
        requestQueue = Volley.newRequestQueue(this);
        VolleyLog.DEBUG = true;
        VolleyLog.TAG = "Escalada";

    }
}
