package org.exthmui.microlauncher.duoqin.bean;

import java.util.List;

public class AppExcludeBean {

    private int versionCode;
    private int totalCount;
    private List<String> excludePackagesName;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<String> getExcludePackagesName() {
        return excludePackagesName;
    }

    public void setExcludePackagesName(List<String> excludePackagesName) {
        this.excludePackagesName = excludePackagesName;
    }
}
