package com.face.device;

import android.app.Activity;

import com.zqzn.android.face.config.EnvConfig;
import com.zqzn.android.face.exceptions.SDKException;

import org.json.JSONException;

import java.io.IOException;

public class DoNothingEnvConfig extends EnvConfig {
    public DoNothingEnvConfig(Activity context, String configFile) {
        super(context, configFile);
    }

    @Override
    public void init() throws JSONException, IOException, SDKException {
    }

    @Override
    public void load() throws IOException, JSONException, SDKException {
    }

    @Override
    public void save() throws IOException, JSONException {
    }
}
