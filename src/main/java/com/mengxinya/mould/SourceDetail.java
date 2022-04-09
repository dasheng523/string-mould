package com.mengxinya.mould;

public class SourceDetail {

    private final String leftSource;
    private final Clay clay;

    private boolean isSkip = false;

    public SourceDetail(String leftSource, Clay clay) {
        this.leftSource = leftSource;
        this.clay = clay;
    }

    private SourceDetail(String leftSource) {
        this.leftSource = leftSource;
        this.isSkip = true;
        this.clay = Clay.makeEmpty();
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

    public static SourceDetail skip(String leftSource) {
        return new SourceDetail(leftSource);
    }

    public boolean isMatch() {
        return clay != null;
    }

    public boolean isSkip() {
        return isSkip;
    }


}
