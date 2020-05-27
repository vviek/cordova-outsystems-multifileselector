package org.multiplefileselector;


import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;


public class FileSelectorPlugin extends CordovaPlugin {


    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {

        if (action.equalsIgnoreCase("register")) {


        }


        return false;
    }
}