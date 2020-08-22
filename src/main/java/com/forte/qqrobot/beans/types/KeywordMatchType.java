/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-core
 * File     KeywordMatchType.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package com.forte.qqrobot.beans.types;

import com.forte.qqrobot.listener.invoker.FilterParameterMatcher;
import com.forte.qqrobot.listener.invoker.FilterParameterMatcherImpl;
import com.simplerobot.modules.utils.KQCodeUtils;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 再{@link com.forte.qqrobot.anno.Filter}中,关键词匹配的时候使用的匹配方式
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @date Created in 2019/3/12 11:27
 * @since JDK1.8
 **/
public enum KeywordMatchType {

    //**************** 正则匹配相关 ****************//
    /*
        正则相关的匹配均支持匹配参数提取，但是其他的方法不支持。
     */

    /** 使用正则matches规则匹配 */
    REGEX((msg, regex) -> regex.matcher(msg).matches()),

    /** 首尾去空后正则matches匹配 */
    TRIM_REGEX((msg, regex) -> regex.matcher(msg).matches(), String::trim),

    /** 移除掉所有CQ码后正则matches匹配 */
    RE_CQCODE_REGEX((msg, regex) -> regex.matcher(msg).matches(), KQCodeUtils.INSTANCE::remove),

    /** 移除掉所有CQ码并首尾去空后正则matches匹配 */
    RE_CQCODE_TRIM_REGEX((msg, regex) -> regex.matcher(msg).matches(), msg -> KQCodeUtils.INSTANCE.remove(msg).trim()),


    /** 使用正则find规则匹配 */
    FIND((msg, regex) -> regex.matcher(msg).find(0)),

    /** 首尾去空后正则find匹配 */
    TRIM_FIND((msg, regex) -> regex.matcher(msg).find(0), String::trim),

    /** 移除掉所有CQ码后正则find匹配 */
    RE_CQCODE_FIND((msg, regex) -> regex.matcher(msg).find(0), KQCodeUtils.INSTANCE::remove),

    /** 移除掉所有CQ码并首尾去空后正则find匹配 */
    RE_CQCODE_TRIM_FIND((msg, regex) -> regex.matcher(msg).find(0), msg -> KQCodeUtils.INSTANCE.remove(msg).trim()),



    //**************** 相同匹配相关 ****************//

    /** 使用完全相同匹配 */
    EQUALS((msg, regex) -> msg.equals(regex.toString())),

    /** 首尾去空后相同匹配  */
    TRIM_EQUALS((msg, regex) -> msg.equals(regex.toString()), String::trim),

    /** 移除掉所有CQ码后相同匹配 */
    RE_CQCODE_EQUALS((msg, regex) -> msg.equals(regex.toString()), KQCodeUtils.INSTANCE::remove),

    /** 移除掉所有CQ码并首尾去空后相同匹配 */
    RE_CQCODE_TRIM_EQUALS((msg, regex) -> msg.equals(regex.toString()), msg -> KQCodeUtils.INSTANCE.remove(msg).trim()),

    //**************** 包含匹配相关 ****************//

    /** 包含匹配 */
    CONTAINS((msg, regex) -> msg.contains(regex.toString())),

    /** 去空的包含匹配 */
    TRIM_CONTAINS((msg, regex) -> msg.contains(regex.toString()), String::trim),

    /** 移除掉所有CQ码后包含匹配 */
    RE_CQCODE_CONTAINS((msg, regex) -> msg.contains(regex.toString()), KQCodeUtils.INSTANCE::remove),

    /** 移除掉所有CQ码并首尾去空后包含匹配 */
    RE_CQCODE_TRIM_CONTAINS((msg, regex) -> msg.contains(regex.toString()), msg -> KQCodeUtils.INSTANCE.remove(msg).trim()),

    //**************** 开头匹配 ****************//

    /** 首部匹配 */
    STARTS_WITH((msg, regex) -> msg.startsWith(regex.toString())),
    /** 去空的首部匹配 */
    TRIM_STARTS_WITH((msg, regex) -> msg.trim().startsWith(regex.toString()), String::trim),
    /** 移除掉所有CQ码后首部匹配 */
    RE_CQCODE_STARTS_WITH((msg, regex) -> msg.startsWith(regex.toString()), KQCodeUtils.INSTANCE::remove),
    /** 移除掉所有CQ码并首尾去空后首部匹配 */
    RE_CQCODE_TRIM_STARTS_WITH((msg, regex) -> msg.startsWith(regex.toString()), msg -> KQCodeUtils.INSTANCE.remove(msg).trim()),

    //**************** 结尾匹配 ****************//

    /** 去空的尾部匹配 */
    TRIM_ENDS_WITH((msg, regex) -> msg.endsWith(regex.toString()), String::trim),
    /** 移除掉所有CQ码后尾部匹配 */
    RE_CQCODE_ENDS_WITH((msg, regex) -> msg.endsWith(regex.toString()), KQCodeUtils.INSTANCE::remove),
    /** 移除掉所有CQ码并首尾去空后尾部匹配 */
    RE_CQCODE_TRIM_ENDS_WITH((msg, regex) -> msg.endsWith(regex.toString()), msg -> KQCodeUtils.INSTANCE.remove(msg).trim()),
    /** 尾部匹配 */
    ENDS_WITH((msg, regex) -> msg.endsWith(regex.toString())),


    // **************** //
    // TODO hutool FDA查找匹配
    // https://www.bookstack.cn/read/hutool/453b51ee888bfc31.md
    // **************** //


    ;

    /**
     * 进行比对
     * @param msg       消息
     * @param keyword   关键词的正则对象
     * @return          比对结果
     */
    public Boolean test(String msg, Pattern keyword){
        return msg != null && filter.test(msg, keyword);
    }

    /**
     * 进行比对
     * @param msg       消息
     * @param keyword   关键词
     * @return          比对结果
     */
    public Boolean test(String msg, String keyword){
        return msg != null && filter.test(msg, Pattern.compile(keyword));
    }



    /**
     * 将字符串转化为 {@link Pattern} 对象
     * @param regex 消息字符串
     * @return {@link Pattern}
     */
    public Pattern toPattern(String regex){
        return toMatcher.apply(regex).getPattern();
    }


    public FilterParameterMatcher toMatcher(String regex){
        return toMatcher.apply(regex);
    }

    /**
     * 字符串消息的前置处理器
     */
    final Function<String, String> msgHandler;

    /**
     * 根据规则而对字符串进行过滤
     */
    final BiPredicate<String, Pattern> filter;

    /**
     * 将@Filter的value参数解析为{@link FilterParameterMatcher}
     */
    final Function<String, FilterParameterMatcher> toMatcher;

    /**
     * 将@Filter的value参数解析为{@link Pattern}
     */
    final Function<String, Pattern> toPattern;


    /**
     * 构造
     * @param filter 匹配规则
     */
    KeywordMatchType(BiPredicate<String, Pattern> filter){
        this.msgHandler = s -> s;
        this.filter = filter;
        this.toMatcher = FilterParameterMatcherImpl::compile;
        this.toPattern = s -> this.toMatcher.apply(s).getPattern();
    }

    /**
     * 构造
     * @param filter 匹配规则
     * @param msgHandler 对消息字符串的前置处理器
     */
    KeywordMatchType(BiPredicate<String, Pattern> filter, Function<String, String> msgHandler){
        this.msgHandler = msgHandler;
        this.filter = (s, p) -> filter.test(msgHandler.apply(s), p);
        this.toMatcher = s -> FilterParameterMatcherImpl.compile(s, msgHandler);
        this.toPattern = s -> this.toMatcher.apply(msgHandler.apply(s)).getPattern();
    }
}
