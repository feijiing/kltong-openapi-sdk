package dto;

import lombok.Data;
import utils.StringUtils;

import java.io.Serializable;

/**
 * @ClassName:Head
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:06
 * @Version: v1.0
 */
@Data
public class Head implements Serializable {

    private String version;
    private String signType;
    private String merchantId;
    private String sign;

    public Head(String merchantId) {
        if (StringUtils.isEmpty(merchantId)) {
            throw new IllegalArgumentException("商户号不能为null或者空字符串");
        } else {
            this.merchantId = merchantId;
            this.version = "1.0";
            this.signType = "2";
        }
    }
    public Head(String merchantId,String signType) {
        if (StringUtils.isEmpty(merchantId)) {
            throw new IllegalArgumentException("商户号不能为null或者空字符串");
        } else {
            this.merchantId = merchantId;
            this.version = "1.0";
            this.signType = signType;
        }
    }

    public void setSignType(String signType) {
        if (StringUtils.isEmpty(this.merchantId)) {
            throw new IllegalArgumentException("signType不能为null或者空字符串");
        } else {
            this.signType = signType;
        }
    }


    public void setMerchantId(String merchantId) {
        if (StringUtils.isEmpty(merchantId)) {
            throw new IllegalArgumentException("merchantId不能为null或者空字符串");
        } else {
            this.merchantId = merchantId;
        }
    }

    @Override
    public String toString() {
        return "Head{version='" + this.version + '\'' + ", signType='" + this.signType + '\'' + ", merchantId='" + this.merchantId + '\'' + ", sign='" + this.sign + '\'' + '}';
    }

}
