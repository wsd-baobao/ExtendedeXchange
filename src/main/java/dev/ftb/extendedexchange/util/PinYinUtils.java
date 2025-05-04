package dev.ftb.extendedexchange.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


@OnlyIn(Dist.CLIENT)
public class PinYinUtils {
    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }
    /**
     * 将中文转换为拼音（全拼和首字母）
     */
    public static String toPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) return " | ";

        StringBuilder pinyin = new StringBuilder();
        StringBuilder initials = new StringBuilder();

        for (char c : chinese.toCharArray()) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyin.append(pinyinArray[0]);
                        initials.append(pinyinArray[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 忽略
                }
            } else {
                pinyin.append(c);
                initials.append(c);
            }
        }

        // 返回格式: "全拼,首字母"
        return pinyin + "|" + initials;
    }

    /**
     * 获取物品名称的拼音表示
     */
    public static String getItemPinyin(ItemStack stack) {
        String displayName = stack.getDisplayName().getString();
        return toPinyin(displayName);
    }
}
