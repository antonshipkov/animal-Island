package logics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class CollectionUtils {

    private CollectionUtils() {
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mergeMaps(BinaryOperator<V> valueMerge,Map<K, V>... maps) {
        HashMap<K, V> result = new HashMap<>();
        for (Map<K, V> map : maps) {
            map.forEach ( (key ,value) -> result.merge ( key ,value ,valueMerge ) );
        }
        return result;
    }
}

