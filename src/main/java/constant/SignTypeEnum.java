package constant;


/**
 * @author daifei
 * @Date 2018/5/7 14:13
 */
public enum SignTypeEnum {

    /**
     * RSA
     */
    RSA("2", "RSA"),

    /**
     * 国密sm2
     */
    SM2("3", "SM2");

    private String code;
    private String desc;

    SignTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取签名类型
     *
     * @param code
     * @return
     */
    public static SignTypeEnum getSignTypeEnumByCode(String code) {
        for (SignTypeEnum signTypeEnum : values()) {
            if (signTypeEnum.getCode().equals(code)) {
                return signTypeEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

}
