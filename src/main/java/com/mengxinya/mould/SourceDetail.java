package com.mengxinya.mould;

public class SourceDetail {

    private final String leftSource;
    private final Clay clay;
    private final boolean isFinish;

    private final boolean isSkip;

    private SourceDetail(String leftSource, Clay clay, boolean isFinish, boolean isSkip) {
        this.leftSource = leftSource;
        this.clay = clay;
        this.isFinish = isFinish;
        this.isSkip = isSkip;
    }

    public SourceDetail(String leftSource, Clay clay) {
        this(leftSource, clay, true, false);
    }

    private SourceDetail(String leftSource) {
        this(leftSource, Clay.makeEmpty(), true, true);
    }



    public String getLeftSource() {
        return leftSource;
    }

    public Clay getClay() {
        return clay;
    }

    public static SourceDetail notMatch(String leftSource) {
        return new SourceDetail(leftSource, null, false, false);
    }

    public static SourceDetail notMatch(String leftSource, Clay clay) {
        return new SourceDetail(leftSource, clay, false, false);
    }

    public static SourceDetail skip(String leftSource) {
        return new SourceDetail(leftSource);
    }

    public boolean isFinish() {
        return this.isFinish;
    }

    public boolean isSkip() {
        return isSkip;
    }


}
