package com.mengxinya.mould;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public interface Mould {

    SourceDetail fill(String source);

    /**
     * 匹配单个字母
     */
    Mould Letter = source -> {
        if (source == null || source.equals("")) {
            return SourceDetail.notMatch();
        }
        if (Character.isLetter(source.charAt(0))) {
            return new SourceDetail(source.substring(1), Clay.make(source.charAt(0)));
        }
        return SourceDetail.notMatch();
    };

    /**
     * 匹配单个数字
     */
    Mould Digit = source -> {
        if (source == null || source.equals("")) {
            return SourceDetail.notMatch();
        }
        if (Character.isDigit(source.charAt(0))) {
            return new SourceDetail(
                    source.substring(1),
                    Clay.make(Integer.parseInt(source.charAt(0) + ""))
            );
        }
        return SourceDetail.notMatch();
    };

    /**
     * 匹配多个数字
     */
    Mould Digits = addHandler(repeat(Digit), clay -> new Clay() {
        @Override
        public <T> T value(Class<T> tClass) {
            String rs = clay.values(Integer.class).stream().map(c -> c + "").collect(Collectors.joining());
            return tClass.cast(Integer.parseInt(rs));
        }
    });

    /**
     * 匹配英文单词
     */
    Mould EnWord = addHandler(repeat(Letter), clay -> new Clay() {
        @Override
        public <T> T value(Class<T> tClass) {
            String rs = clay.values(Character.class).stream().map(c -> c + "").collect(Collectors.joining());
            return tClass.cast(rs);
        }
    });

    /**
     * 匹配特定字符串
     * @param s 特定字符串
     * @return Mould
     */
    static Mould theMould(String s) {
        return source -> {
            if (source.startsWith(s)) {
                return new SourceDetail(
                        source.substring(s.length()),
                        Clay.make(s)
                );
            }
            return SourceDetail.notMatch();
        };
    }

    static Mould theMould(Integer i) {
        return source -> {
            String s = i + "";
            if (source.startsWith(i + "")) {
                return new SourceDetail(
                        source.substring(s.length()),
                        Clay.make(i)
                );
            }
            return SourceDetail.notMatch();
        };
    }

    static Mould addHandler(Mould mould, ClayConverter converter) {
        return source -> {
            SourceDetail detail = mould.fill(source);
            return new SourceDetail(detail.getLeftSource(), converter.apply(detail.getClay()));
        };
    }

    static Mould compose(Mould... moulds) {
        return new Mould() {
            @Override
            public SourceDetail fill(String source) {
                List<Mould> mouldList = Arrays.asList(moulds);
                LinkedList<SourceDetail> result = new LinkedList<>();

                boolean rs = loopFill(mouldList, result, source);
                if (rs) {
                    return new SourceDetail(
                            result.getLast().getLeftSource(),
                            Clay.compose(result.stream().map(SourceDetail::getClay).collect(Collectors.toList())));
                }
                else {
                    return SourceDetail.notMatch();
                }
            }

            private boolean loopFill(List<Mould> mouldList, List<SourceDetail> result, String source) {
                if (mouldList.isEmpty()) {
                    return true;
                }
                Mould first = mouldList.remove(0);
                SourceDetail detail = first.fill(source);
                if (!detail.isMatch()) {
                    return false;
                }
                result.add(detail);
                return loopFill(mouldList, result, detail.getLeftSource());
            }
        };
    }
    static Mould join(Mould separator, Mould item) {
        return source -> {
            SourceDetail itemRs = item.fill(source);
            if (!itemRs.isMatch()) {
                return SourceDetail.notMatch();
            }

            Mould repeatMould = repeat(compose(separator, item));
            SourceDetail repeatRs = repeatMould.fill(itemRs.getLeftSource());
            if (repeatRs.isMatch()) {
                List<Clay> clayList = new LinkedList<>();
                clayList.add(itemRs.getClay());
                clayList.addAll(Clay.deconstruct(repeatRs.getClay(), clayItem -> Clay.deconstruct(clayItem, 1)));
                return new SourceDetail(
                        repeatRs.getLeftSource(),
                        Clay.compose(clayList)
                );
            }
            else {
                return itemRs;
            }
        };
    }

    static Mould repeat(Mould item, int min, int max) {
        return new Mould() {
            @Override
            public SourceDetail fill(String source) {
                int count = 0;
                LinkedList<SourceDetail> result = new LinkedList<>();
                boolean rs = loopFill(source, count, result);
                if (rs) {
                    return new SourceDetail(
                            result.getLast().getLeftSource(),
                            Clay.compose(result.stream().map(SourceDetail::getClay).collect(Collectors.toList())));
                }
                else {
                    return SourceDetail.notMatch();
                }
            }

            private boolean loopFill(String source, int count, List<SourceDetail> result) {
                if (max != 0 && count == max) {
                    return true;
                }
                SourceDetail detail = item.fill(source);
                if (!detail.isMatch()) {
                    return count >= min;
                }
                else {
                    result.add(detail);
                    return loopFill(detail.getLeftSource(), count + 1, result);
                }
            }
        };
    }
    static Mould repeat(Mould item, int min) {
        return repeat(item, min, 0);
    }
    static Mould repeat(Mould item) {
        return repeat(item, 1);
    }

    static Mould maybe(Mould... moulds) {
        return null;
    }
}
