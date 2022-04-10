package com.mengxinya.mould;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.stream.Collectors;

public interface Clay {


    <T> T value(Class<T> tClass);

    default String toJsonString() {
        return JSON.toJSONString(value(Object.class));
    }

    static Clay make(Object obj) {
        return new Clay() {
            @Override
            public <T> T value(Class<T> tClass) {
                return tClass.cast(obj);
            }
        };
    }

    static Clay makeEmpty() {
        return new Clay() {
            @Override
            public <T> T value(Class<T> tClass) {
                return null;
            }

            @Override
            public String toString() {
                return "";
            }

            @Override
            public String toJsonString() {
                return "";
            }
        };
    }

    static boolean isEmpty(Clay clay) {
        return clay.value(Object.class) == null;
    }

    static Clay compose(List<Clay> collect) {
        return new Clay() {
            @Override
            public <T> T value(Class<T> tClass) {
                return tClass.cast(
                        collect.stream()
                                .map(item -> item.value(Object.class))
                                .collect(Collectors.toList())
                );
            }
        };
    }

    static <T> List<T> getValues(Clay clay, Class<T> tClass) {
        List<?> list = clay.value(List.class);
        return list.stream().map(tClass::cast).collect(Collectors.toList());
    }

    static List<Clay> deconstruct(Clay clay, ClayConverter converter) {
        return deconstruct(clay)
                .stream()
                .map(converter::apply)
                .collect(Collectors.toList());
    }

    static List<Clay> deconstruct(Clay clay) {
        return getValues(clay, Object.class)
                .stream()
                .map(Clay::make)
                .collect(Collectors.toList());
    }
    // 当ClayList里面还有ClayList的时候，使用Clay::make则会导致values取不到。

    static Clay deconstruct(Clay clay, int index) {
        List<Clay> clayList = deconstruct(clay);
        if (clayList.size() <= index) {
            throw new ClayException("index 越界");
        }
        return clayList.get(index);
    }

}
