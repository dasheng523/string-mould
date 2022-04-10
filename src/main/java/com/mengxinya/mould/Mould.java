package com.mengxinya.mould;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Mould {

    SourceDetail fill(String source);

    /**
     * 匹配单个字母
     */
    Mould Letter = source -> {
        if (source == null || source.equals("")) {
            return SourceDetail.notMatch(source);
        }
        if (Character.isLowerCase(source.charAt(0)) || Character.isUpperCase(source.charAt(0))) {
            return new SourceDetail(source.substring(1), Clay.make(source.charAt(0)));
        }
        return SourceDetail.notMatch(source);
    };

    /**
     * 匹配单个数字
     */
    Mould Digit = source -> {
        if (source == null || source.equals("")) {
            return SourceDetail.notMatch(source);
        }
        if (Character.isDigit(source.charAt(0))) {
            return new SourceDetail(
                    source.substring(1),
                    Clay.make(Integer.parseInt(source.charAt(0) + ""))
            );
        }
        return SourceDetail.notMatch(source);
    };

    /**
     * 匹配单个汉字
     */
    Mould Han = new Mould() {
        private boolean isChinese(char c) {
            Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
            return pattern.matcher(c+"").find();
        }
        @Override
        public SourceDetail fill(String source) {
            if (source == null || source.equals("")) {
                return SourceDetail.notMatch(source);
            }
            if (isChinese(source.charAt(0))) {
                return new SourceDetail(
                        source.substring(1),
                        Clay.make(source.charAt(0) + "")
                );
            }
            return SourceDetail.notMatch(source);
        }
    };

    /**
     * 匹配多个数字
     */
    Mould Digits = convert(repeat(Digit), clay -> new Clay() {
        @Override
        public <T> T value(Class<T> tClass) {
            String rs = Clay.getValues(clay, Integer.class).stream().map(c -> c + "").collect(Collectors.joining());
            return tClass.cast(Integer.parseInt(rs));
        }
    });

    /**
     * 匹配英文单词
     */
    Mould EnWord = convert(repeat(Letter), ClayConverter.joining(""));

    /**
     * 匹配多个汉字
     */
    Mould Hans = convert(repeat(Han), ClayConverter.joining(""));

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
            return SourceDetail.notMatch(source);
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
            return SourceDetail.notMatch(source);
        };
    }

    Mould EOF = source -> {
        if (source.equals("")) {
            return new SourceDetail("", Clay.make(""));
        }
        else {
            return SourceDetail.notMatch(source);
        }
    };

    static Mould convert(Mould mould, ClayConverter converter) {
        return source -> {
            SourceDetail detail = mould.fill(source);
            if (!detail.isFinish() || detail.isSkip()) {
                return detail;
            }
            return new SourceDetail(detail.getLeftSource(), converter.apply(detail.getClay()));
        };
    }

    static Mould compose(Mould... moulds) {
        return new Mould() {
            @Override
            public SourceDetail fill(String source) {
                List<Mould> mouldList = new ArrayList<>(Arrays.asList(moulds));
                List<SourceDetail> result = new ArrayList<>();

                boolean rs = loopFill(mouldList, result, source);
                if (rs) {
                    return new SourceDetail(
                            result.get(result.size() - 1).getLeftSource(),
                            Clay.compose(result.stream().map(SourceDetail::getClay).collect(Collectors.toList())));
                }
                else {
                    return SourceDetail.notMatch(source);
                }
            }

            private boolean loopFill(List<Mould> mouldList, List<SourceDetail> result, String source) {
                if (mouldList.isEmpty()) {
                    return true;
                }
                Mould first = mouldList.remove(0);
                SourceDetail detail = first.fill(source);
                if (!detail.isFinish()) {
                    return false;
                }
                if (!detail.isSkip()) {
                    result.add(detail);
                }
                return loopFill(mouldList, result, detail.getLeftSource());
            }
        };
    }

    static Mould composeJoining(Mould... moulds) {
        return convert(compose(moulds), ClayConverter.joining(""));
    }

    static Mould cons(Mould car, Mould cdr) {
        return convert(compose(car, cdr), ClayConverter.Cons);
    }

    static Mould join(Mould separator, Mould item) {
        return source -> {
            SourceDetail itemRs = item.fill(source);
            if (!itemRs.isFinish()) {
                return SourceDetail.notMatch(source);
            }
            if (itemRs.isSkip()) {
                return itemRs;
            }

            Mould repeatMould = repeat(compose(separator, item));
            SourceDetail repeatRs = repeatMould.fill(itemRs.getLeftSource());
            if (repeatRs.isFinish()) {
                List<Clay> clayList = new ArrayList<>();
                clayList.add(itemRs.getClay());
                clayList.addAll(
                        Clay.deconstruct(
                                repeatRs.getClay(),
                                clayItem -> Clay.deconstruct(clayItem, 1)
                        )
                );
                return new SourceDetail(
                        repeatRs.getLeftSource(),
                        Clay.compose(clayList)
                );
            }
            else {
                return new SourceDetail(itemRs.getLeftSource(), ClayConverter.MakeListOf.apply(itemRs.getClay()));
            }
        };
    }


    static Mould join(MouldContext context, Mould separator, Mould item) {
        return source -> {
            String key = System.currentTimeMillis() + "";

            Mould refSep = context.ref(separator, key);
            Mould backSep = context.backRef(key);
            Mould repeatMould = cons(
                    convert(compose(refSep, item), ClayConverter.deconstruct(1)),
                    repeat(convert(compose(backSep, item), ClayConverter.deconstruct(1)), 0)
            );

            SourceDetail itemDetail = item.fill(source);
            if (!itemDetail.isFinish()) {
                return SourceDetail.notMatch(source);
            }

            SourceDetail repeatDetail = repeatMould.fill(itemDetail.getLeftSource());
            if (!repeatDetail.isFinish()) {
                return new SourceDetail(itemDetail.getLeftSource(), ClayConverter.MakeListOf.apply(itemDetail.getClay()));
            }

            Mould composeMould = cons(item, zeroOrOne(repeatMould));

            SourceDetail repeatRs = composeMould.fill(source);
            if (repeatRs.isFinish()) {
                return repeatRs;
            }
            else {
                return SourceDetail.notMatch(source);
            }
        };
    }

    static MouldList repeat(Mould item, int min, int max) {
        return new MouldList() {
            @Override
            public SourceDetail doNext(String source) {
                List<SourceDetail> result = new ArrayList<>();
                boolean rs = loopFill(source, 0, result);
                if (rs) {
                    if (result.size() > 0) {
                        return new SourceDetail(
                                result.get(result.size() - 1).getLeftSource(),
                                Clay.compose(result.stream().map(SourceDetail::getClay).collect(Collectors.toList())));
                    }
                    else {
                        return new SourceDetail(source, Clay.make(new ArrayList<>()));
                    }
                }
                else {
                    if (result.size() > 0) {
                        return SourceDetail.notMatch(result.get(result.size() - 1).getLeftSource(), Clay.make(result));
                    }
                    else {
                        return SourceDetail.notMatch(source);
                    }
                }
            }

            private boolean loopFill(String source, int count, List<SourceDetail> result) {
                if (max != 0 && count == max) {
                    return true;
                }
                SourceDetail detail = item.fill(source);
                if (detail.isSkip()) {
                    return true;
                }
                if (!detail.isFinish()) {
                    return count >= min;
                }
                else {
                    result.add(detail);
                    return loopFill(detail.getLeftSource(), count + 1, result);
                }
            }
        };
    }

    static MouldList repeat(Mould item, int min) {
        return repeat(item, min, 0);
    }
    static MouldList repeat(Mould item) {
        return repeat(item, 1);
    }

    static Mould zeroOrOne(Mould item) {
        return source -> {
            SourceDetail detail = item.fill(source);
            if (detail.isFinish()) {
                return detail;
            }
            return SourceDetail.skip(source);
        };
    }

    static Mould maybe(Mould... moulds) {
        return source -> {
            for (Mould item : moulds) {
                SourceDetail detail = item.fill(source);
                if (detail.isFinish()) {
                    return detail;
                }
            }
            return SourceDetail.notMatch(source);
        };
    }

    static Mould interweave(MouldList item, Mould adorn) {
        return new Mould() {
            @Override
            public SourceDetail fill(String source) {
                SourceDetail detail = item.doNext(source);
                if (detail.isFinish()) {
                    return detail;
                }
                SourceDetail adornDetail = adorn.fill(detail.getLeftSource());
                if (!adornDetail.isFinish()) {
                    return SourceDetail.notMatch(source);
                }

                SourceDetail leftDetail = fill(adornDetail.getLeftSource());
                if (!leftDetail.isFinish()) {
                    return SourceDetail.notMatch(source);
                }
                return new SourceDetail(
                        leftDetail.getLeftSource(),
                        ClayConverter.appendClay(
                                detail.getClay(),
                                ClayConverter.MakeListOf.apply(adornDetail.getClay()),
                                leftDetail.getClay()
                        )
                );
            }
        };
    }

    static MouldContext makeContext() {
        return new MouldContext();
    }

    final class MouldContext {
        private final Map<String, String> context = new HashMap<>();
        private final Map<String, Mould> mouldMap = new HashMap<>();

        public Mould ref(Mould mould, String key) {
            return source -> {
                SourceDetail detail = mould.fill(source);
                if (!detail.isFinish()) {
                    return SourceDetail.notMatch(source);
                }
                if (detail.isSkip()) {
                    return detail;
                }
                String readStr = source.substring(0, source.length() - detail.getLeftSource().length());
                context.put(key, readStr);
                mouldMap.put(key, mould);
                return detail;
            };
        }

        public Mould backRef(String key) {
            return source -> {
                String readStr = context.get(key);
                Mould mould = mouldMap.get(key);
                if (mould == null || readStr == null) {
                    throw new ClayException("key不存在");
                }
                if (!source.startsWith(readStr)) {
                    return SourceDetail.notMatch(source);
                }
                return mould.fill(source);
            };
        }
    }
}
