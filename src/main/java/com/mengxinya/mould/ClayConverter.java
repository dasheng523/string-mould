package com.mengxinya.mould;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ClayConverter {
    Clay apply(Clay clay);

    /**
     * 将Clay集合拼接成字符串
     */
    static ClayConverter joining(String sep) {
        return clay -> {
            List<Clay> clayList = Clay.deconstruct(clay);
            return Clay.make(
                    clayList.stream()
                            .map(item -> item.value(Object.class))
                            .map(Object::toString)
                            .collect(Collectors.joining(sep))
            );
        };
    }

    ClayConverter Cons = clay -> {
        List<Clay> clayList = Clay.deconstruct(clay);
        if (clayList.size() == 0) {
            return Clay.makeEmpty();
        }
        List<Object> list = new ArrayList<>();
        list.add(clayList.get(0).value(Object.class));
        list.addAll(clayList.get(1).value(List.class));

        return Clay.make(list);
    };

    static ClayConverter deconstruct(int index) {
        return clay -> Clay.deconstruct(clay, index);
    }

    static ClayConverter compose(ClayConverter... converters) {
        return clay -> {
            Clay result = clay;
            for (ClayConverter converter : converters) {
                result = converter.apply(result);
            }
            return result;
        };
    }

    ClayConverter Append = clay -> {
        List<Clay> clayList = Clay.deconstruct(clay);

        List<Object> result = Clay.getValues(clayList.remove(0), Object.class);

        for (Clay item : clayList) {
            List<Object> itemObjList = Clay.getValues(item, Object.class);
            result.addAll(itemObjList);
        }

        return Clay.make(result);
    };

    static Clay appendClay(Clay clay, Clay... clays) {
        List<Object> result = Clay.getValues(clay, Object.class);
        for (Clay item : clays) {
            List<Object> itemObjList = Clay.getValues(item, Object.class);
            result.addAll(itemObjList);
        }
        return Clay.make(result);
    }

    ClayConverter MakeListOf = clay -> Clay.make(List.of(clay.value(Object.class)));

}
