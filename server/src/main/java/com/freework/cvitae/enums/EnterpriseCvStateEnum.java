package com.freework.cvitae.enums;

/**
 * @author daihongru
 */

public enum EnterpriseCvStateEnum {
    /**
     * 枚举字段
     */
    NOT_PASS(-1, "未通过"),
    DELIVERY(0, "投递中"),
    PASS(1, "通过");

    /**
     * 状态表示
     */
    private int state;

    /**
     * 状态说明
     */
    private String stateInfo;

    EnterpriseCvStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 依据传入的state返回相应的enum值
     */
    public static EnterpriseCvStateEnum stateOf(int state) {
        for (EnterpriseCvStateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
}
