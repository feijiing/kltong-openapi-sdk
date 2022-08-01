package utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import constant.SignTypeEnum;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName:SignUtils
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:33
 * @Version: v1.0
 */
public class SignUtils {

    public static String sign(String content, String privateKey, SignTypeEnum signType) throws Exception {
        if (SignTypeEnum.RSA.equals(signType)) {
            return RSAUtils.rsa256Sign(content, privateKey, StandardCharsets.UTF_8.name());
        } else if (SignTypeEnum.SM2.equals(signType)) {
            SM2 sm2Sign = SmUtil.sm2(privateKey, null);
            sm2Sign.usePlainEncoding();
            return HexUtil.encodeHexStr(sm2Sign.sign(content.getBytes(StandardCharsets.UTF_8)));
        }
        throw new IllegalArgumentException("不支持的签名类型！");
    }


    public static boolean verySign(String content, String signMsg, String publicKey, SignTypeEnum signType) throws Exception {
        if (SignTypeEnum.RSA.equals(signType)) {
            return RSAUtils.rsa256VerifySign(content, signMsg, publicKey, StandardCharsets.UTF_8.name());
        } else if (SignTypeEnum.SM2.equals(signType)) {
            SM2 sm2Sign = SmUtil.sm2(null, publicKey);
            sm2Sign.usePlainEncoding();
            return sm2Sign.verifyHex(HexUtil.encodeHexStr(content), signMsg);
        }
        throw new IllegalArgumentException("不支持的签名类型！");
    }

}
