package com.mengxinya.mould;


public interface MouldList extends Mould{
    /**
     *  当填充过程中遇到不匹配的时候，它需要把已经匹配的内容返回出去。，而且Clay必须是List
     * @param source source
     * @return SourceDetail
     */
    SourceDetail doNext(String source);

    @Override
    default SourceDetail fill(String source) {
        return doNext(source);
    }
}
