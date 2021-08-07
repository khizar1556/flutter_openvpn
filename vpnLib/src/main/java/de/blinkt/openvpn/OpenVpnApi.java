package de.blinkt.openvpn;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringReader;

import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;

public class OpenVpnApi {

    private static final String TAG = "OpenVpnApi";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void startVpn(Context context, String inlineConfig, String sCountry, String expireAt, String userName, String pw, String profileId, int timeOutInSeconds, String time,String dns1,String dns2) throws RemoteException {
        if (TextUtils.isEmpty(inlineConfig)) throw new RemoteException("config is empty");
        startVpnInternal(context, inlineConfig, sCountry, expireAt, userName, pw, profileId, timeOutInSeconds, time,dns1,dns2);
    }

    static void startVpnInternal(Context context, String inlineConfig, String sCountry, String expireAt, String userName, String pw, String profileId, int timeOutInSeconds, String time,String dns1,String dns2) throws RemoteException {
        ConfigParser cp = new ConfigParser();
        try {
            cp.parseConfig(new StringReader(inlineConfig));
            VpnProfile vp = cp.convertProfile();// Analysis.ovpn
            Log.d(TAG, "startVpnInternal: ==============" + cp + "\n" +
                    vp);
            vp.mName = sCountry;
            vp.mProdileId = profileId;
            vp.timeOutInSeconds = timeOutInSeconds;
            if (vp.checkProfile(context) != de.blinkt.openvpn.R.string.no_error_found) {
                throw new RemoteException(context.getString(vp.checkProfile(context)));
            }
            vp.mProfileCreator = context.getPackageName();
            vp.mUsername = userName;
            vp.mPassword = pw;
            vp.mExpireAt = expireAt;
            vp.time = time;
            if(dns1!=null && dns2!=null && !dns1.isEmpty() && !dns2.isEmpty()){
                vp.mDNS1=dns1;
                vp.mDNS2=dns2;
            }
            Toast.makeText(context, vp.time, Toast.LENGTH_SHORT).show();
            ProfileManager.setTemporaryProfile(context, vp);
            VPNLaunchHelper.startOpenVpn(vp, context);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            throw new RemoteException(e.getMessage());
        }
    }
}
