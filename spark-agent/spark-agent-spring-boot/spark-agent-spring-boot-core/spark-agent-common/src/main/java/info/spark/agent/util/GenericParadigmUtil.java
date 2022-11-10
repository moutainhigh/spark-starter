package info.spark.agent.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: 泛型工具类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.05 21:59
 * @since 1.5.0
 */
public class GenericParadigmUtil {

    /**
     * Parse generic paradigm
     *
     * @param object   object
     * @param position position
     * @return the class
     * @since 1.5.0
     */
    @Contract("null, _ -> null")
    public static Class<?> parseGenericParadigm(Object object, int position) {
        if (object == null) {
            return null;
        }

        return GenericParadigmUtil.parseGenericParadigm(object.getClass(), position);
    }

    /**
     * Parse generic paradigm
     *
     * @param clazz    clazz
     * @param position position
     * @return the class
     * @since 1.5.0
     */
    @Contract("null, _ -> null")
    public static Class<?> parseGenericParadigm(Class<?> clazz, int position) {
        if (clazz == null) {
            return null;
        }
        List<Pathfinder> pathfinders = new ArrayList<>(1);
        pathfinders.add(new ConsistentPathfinder(Integer.MAX_VALUE, position));
        return GenericParadigmUtil.parseGenericParadigm(clazz, pathfinders);
    }

    /**
     * Parse generic paradigm
     *
     * @param object      object
     * @param pathfinders pathfinders
     * @return the class
     * @since 1.5.0
     */
    @Contract("null, _ -> null")
    public static Class<?> parseGenericParadigm(Object object, List<Pathfinder> pathfinders) {
        if (object == null) {
            return null;
        }
        return GenericParadigmUtil.parseGenericParadigm(object.getClass(), pathfinders);
    }

    /**
     * Parse generic paradigm
     *
     * @param clazz       clazz
     * @param pathfinders pathfinders
     * @return the class
     * @since 1.5.0
     */
    public static @Nullable Class<?> parseGenericParadigm(Class<?> clazz, List<Pathfinder> pathfinders) {

        if (!GenericParadigmUtil.isGenericParadigm(clazz) || pathfinders == null || pathfinders.isEmpty()) {
            return null;
        }
        assertPathfinder(pathfinders);
        Pathfinder pathfinder = pathfinders.get(0);
        boolean isConsistentPathfinder = pathfinder instanceof ConsistentPathfinder;
        int size = pathfinders.size();
        Type type = clazz.getGenericSuperclass();

        return GenericParadigmUtil.getGenericClassPlus(type, 0, size, isConsistentPathfinder, pathfinders);
    }

    /**
     * Parse interface generic paradigm
     *
     * @param object   object
     * @param who      who
     * @param position position
     * @return the class
     * @since 1.5.0
     */
    @Contract("null, _, _ -> null")
    public static Class<?> parseInterfaceGenericParadigm(Object object, int who, int position) {
        if (object == null) {
            return null;
        }

        return GenericParadigmUtil.parseInterfaceGenericParadigm(object.getClass(), who, position);
    }

    /**
     * Parse interface generic paradigm
     *
     * @param clazz    clazz
     * @param who      who
     * @param position position
     * @return the class
     * @since 1.5.0
     */
    @Contract("null, _, _ -> null")
    public static Class<?> parseInterfaceGenericParadigm(Class<?> clazz, int who, int position) {
        if (clazz == null) {
            return null;
        }
        List<Pathfinder> pathfinders = new ArrayList<>(1);
        pathfinders.add(new ConsistentPathfinder(Integer.MAX_VALUE, position));
        return GenericParadigmUtil.parseInterfaceGenericParadigm(clazz, who, pathfinders);
    }

    /**
     * Parse interface generic paradigm
     *
     * @param object      object
     * @param who         who
     * @param pathfinders pathfinders
     * @return the class
     * @since 1.5.0
     */
    @Contract("null, _, _ -> null")
    public static Class<?> parseInterfaceGenericParadigm(Object object, int who, List<Pathfinder> pathfinders) {
        if (object == null) {
            return null;
        }
        return GenericParadigmUtil.parseInterfaceGenericParadigm(object.getClass(), who, pathfinders);
    }

    /**
     * Parse interface generic paradigm
     *
     * @param clazz       clazz
     * @param who         who
     * @param pathfinders pathfinders
     * @return the class
     * @since 1.5.0
     */
    public static @Nullable Class<?> parseInterfaceGenericParadigm(Class<?> clazz, int who, List<Pathfinder> pathfinders) {
        if (!GenericParadigmUtil.isInterfaceGenericParadigm(clazz) || pathfinders == null || pathfinders.isEmpty()) {
            return null;
        }
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        int length = genericInterfaces.length;
        if (who < 0 || who >= length) {
            return null;
        }
        assertPathfinder(pathfinders);
        Pathfinder pathfinder = pathfinders.get(0);
        boolean isConsistentPathfinder = pathfinder instanceof ConsistentPathfinder;
        int size = pathfinders.size();
        Type type = genericInterfaces[who];

        return GenericParadigmUtil.getGenericClassPlus(type, 0, size, isConsistentPathfinder, pathfinders);
    }

    /**
     * Is interface generic paradigm
     *
     * @param object object
     * @return the boolean
     * @since 1.5.0
     */
    @Contract("null -> false")
    public static boolean isInterfaceGenericParadigm(Object object) {
        if (object == null) {
            return false;
        }
        return GenericParadigmUtil.isInterfaceGenericParadigm(object.getClass());
    }

    /**
     * Is interface generic paradigm
     *
     * @param clazz clazz
     * @return the boolean
     * @since 1.5.0
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isInterfaceGenericParadigm(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        return genericInterfaces.length > 0;
    }

    /**
     * Is generic paradigm
     *
     * @param object object
     * @return the boolean
     * @since 1.5.0
     */
    @Contract("null -> false")
    public static boolean isGenericParadigm(Object object) {
        if (object == null) {
            return false;
        }
        return GenericParadigmUtil.isGenericParadigm(object.getClass());
    }

    /**
     * Is generic paradigm
     *
     * @param clazz clazz
     * @return the boolean
     * @since 1.5.0
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isGenericParadigm(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        Type genericSuperclass = clazz.getGenericSuperclass();
        return genericSuperclass instanceof ParameterizedType;
    }

    /**
     * Gets generic class plus *
     *
     * @param type                   type
     * @param level                  level
     * @param size                   size
     * @param isConsistentPathfinder is consistent pathfinder
     * @param pathfinders            pathfinders
     * @return the generic class plus
     * @since 1.5.0
     */
    private static @Nullable Class<?> getGenericClassPlus(Type type, int level, int size, boolean isConsistentPathfinder,
                                                          List<Pathfinder> pathfinders) {
        if (isConsistentPathfinder || level < size) {
            // 得到指路人指明前进的道路
            Pathfinder pathfinder = isConsistentPathfinder ? pathfinders.get(0) : pathfinders.get(level);
            if (type instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                int length = types.length;
                int position = pathfinder.position;
                if (position < 0 || position >= length) {
                    return null;
                }
                return getGenericClassPlus(types[position], level + 1, size, isConsistentPathfinder, pathfinders);
            }
        }

        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    /**
     * Gets generic class *
     *
     * @param type                   type
     * @param level                  level
     * @param size                   size
     * @param isConsistentPathfinder is consistent pathfinder
     * @param pathfinders            pathfinders
     * @return the generic class
     * @since 1.5.0
     */
    @Deprecated
    private static @Nullable Class<?> getGenericClass(Type type, int level, int size, boolean isConsistentPathfinder,
                                                      List<Pathfinder> pathfinders) {
        if (isConsistentPathfinder) {
            // 特殊, 找到泛型的最深处类型
            if (type instanceof Class) {
                return (Class<?>) type;
            }
        } else {
            // 指定指路人
            if (level >= size) {
                if (type instanceof Class) {
                    return (Class<?>) type;
                } else if (type instanceof ParameterizedType) {
                    return (Class<?>) ((ParameterizedType) type).getRawType();
                }
            }
        }

        // 得到指路人指明前进的道路
        Pathfinder pathfinder = isConsistentPathfinder ? pathfinders.get(0) : pathfinders.get(level);
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            int length = types.length;
            int position = pathfinder.position;
            if (position < 0 || position >= length) {
                return null;
            }
            return getGenericClass(types[position], level + 1, size, isConsistentPathfinder, pathfinders);
        } else {
            return null;
        }
    }

    /**
     * Assert pathfinder
     *
     * @param pathfinders pathfinders
     * @since 1.5.0
     */
    @Contract("null -> fail")
    private static void assertPathfinder(List<Pathfinder> pathfinders) {
        if (pathfinders == null || pathfinders.isEmpty()) {
            throw new IllegalArgumentException("Oh, No, It`s not have Pathfinder...");
        }
        Pathfinder pathfinder = pathfinders.get(0);
        boolean isConsistentPathfinder = pathfinder instanceof ConsistentPathfinder;
        if (!isConsistentPathfinder) {
            int size = pathfinders.size();
            for (int level = 0; level < size; level++) {
                pathfinder = pathfinders.get(level);
                if (level != pathfinder.depth) {
                    throw new IllegalArgumentException("Oh, No, Pathfinders is incomplete...");
                }
            }
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.05 21:59
     * @since 1.5.0
     */
    private static class ConsistentPathfinder extends Pathfinder {
        /**
         * Consistent pathfinder
         *
         * @param depth    in depth
         * @param position in position
         * @since 1.5.0
         */
        ConsistentPathfinder(int depth, int position) {
            super.depth = depth;
            super.position = position;
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.05 21:59
     * @since 1.5.0
     */
    public static class Pathfinder {
        /** Depth */
        public int depth;
        /** Position */
        public int position;

        /**
         * Pathfinder
         *
         * @since 1.5.0
         */
        @Contract(pure = true)
        public Pathfinder() {
        }

        /**
         * Pathfinder
         *
         * @param depth    in depth
         * @param position in position
         * @since 1.5.0
         */
        @Contract(pure = true)
        public Pathfinder(int depth, int position) {
            this.depth = depth;
            this.position = position;
        }
    }

}
