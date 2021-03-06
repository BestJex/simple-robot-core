/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-core
 * File     RunParameter.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package com.forte.qqrobot.system;

/**
 * 启动程序的时候的参数
 * 类型一般就 String, String[]
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface RunParameter {

    public String getBase();

    String getName();

    String[] getParameters();

    String getParameter(int index);

    int parameterLength();

    default String getParameterFirst(){
        return parameterLength() == 0 ? null : getParameter(0);
    }

    RunParameterType getType();

}
