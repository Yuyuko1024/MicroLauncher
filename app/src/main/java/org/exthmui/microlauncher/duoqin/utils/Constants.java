package org.exthmui.microlauncher.duoqin.utils;

import org.exthmui.microlauncher.duoqin.BuildConfig;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> DeviceList = Arrays.asList(
            "k61v1_64_bsp", // Qin F21 Pro
            "ums312_2h10_Natv", // Qin F21 Pro+
            "AGN_1244RO_MT6769S_MX6432" // Qin F22 Pro
    );

    public static final String HIDE_APP_ACTION = BuildConfig.APPLICATION_ID + ".action.HIDE_APP";

    public static final String launcherSettingsPref = BuildConfig.APPLICATION_ID +"_preferences";

    public static final String appExcludePref = "exclude";

}
