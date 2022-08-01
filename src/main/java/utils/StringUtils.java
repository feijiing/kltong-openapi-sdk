package utils;

/**
 * @ClassName:StringUtils
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:18
 * @Version: v1.0
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmptyAny(String... srcs) {
        String[] var1 = srcs;
        int var2 = srcs.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String str = var1[var3];
            if (isEmpty(str)) {
                return true;
            }
        }

        return false;
    }
}
