package cn.com.xuxiaowei.nacos.sentinel.utils;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
public class StringUtils {

	/**
	 * 补充字符串到指定长度，不足的部分使用空格代替
	 * @param obj 字符串
	 * @param length 指定长度
	 * @return 返回指定长度的字符串
	 */
	public static String formatLength(Object obj, int length) {
		return String.format("%-" + length + "s", obj);
	}

	/**
	 * 截取字符串中 @ 左侧的内容，若字符串无 @ 将返回原始值
	 * @param str 字符串
	 * @return 返回 字符串中 @ 左侧的内容，或者是原始值
	 */
	public static String extractAtLeft(String str) {
		if (str == null) {
			return null;
		}
		int atIndex = str.indexOf("@");
		if (atIndex != -1) {
			return str.substring(0, atIndex);
		}
		else {
			return str;
		}
	}

}
