package com.zhouz.dialogqueue;


import com.zhouz.dialogqueue.DialogEx;

/**
 * @author linmin1 on 2018/10/11.
 */
public class MethodStackTrace {


    private MethodStackTrace() {

    }

    public static void printMethodStack(String tag, String msg) {
        DialogEx.INSTANCE.getLog().i(tag, "%s %s", msg, getStackMsg(Thread.currentThread().getStackTrace()));
    }

    public static void printMethodStack2(String tag, String msg) {
        DialogEx.INSTANCE.getLog().i(tag, "%s %s", msg, getStackMsg(Thread.currentThread().getStackTrace()));
    }

    private static String getStackMsg(StackTraceElement[] stackArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
