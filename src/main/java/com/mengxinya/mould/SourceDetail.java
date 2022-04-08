package com.mengxinya.mould;

public class SourceDetail {

    private final String leftSource;
    private final Clay clay;

    public SourceDetail(String leftSource, Clay clay) {
        this.leftSource = leftSource;
        this.clay = clay;
    }

    public String getLeftSource() {
        return leftSource;
    }

    public Clay getClay() {
        return clay;
    }

    public static SourceDetail notMatch() {
        return new SourceDetail("", null);
    }

    public boolean isMatch() {
        return clay != null;
    }

}
