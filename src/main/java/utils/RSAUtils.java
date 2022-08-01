package utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:RSAUtils
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:18
 * @Version: v1.0
 */

public class RSAUtils {

    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHM = "SHA256WithRSA";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";

    public RSAUtils() {
    }

    public static String rsa256Sign(String content, String privateKey, String charset) throws Exception {
        if (StringUtils.isEmptyAny(new String[]{content, privateKey})) {
            return "";
        } else {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        }
    }

    public static boolean rsa256VerifySign(String content, String sign, String publicKey, String charset) throws Exception {
        if (StringUtils.isEmptyAny(new String[]{content, sign, publicKey})) {
            return false;
        } else {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(pubKey);
            if (StringUtils.isNotEmpty(charset)) {
                signature.update(content.getBytes(charset));
            } else {
                signature.update(content.getBytes());
            }

            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        }
    }

    public static Map<String, String> loadKeyByFile(String pfxPath, String password) throws Exception {
        return (Map)(StringUtils.isEmptyAny(new String[]{pfxPath, password}) ? new HashMap(0) : loadKeyByFile(new FileInputStream(pfxPath), password));
    }

    public static Map<String, String> loadKeyByFile(File pfx, String password) throws Exception {
        return (Map)(pfx != null && !StringUtils.isEmpty(password) ? loadKeyByFile(new FileInputStream(pfx), password) : new HashMap(0));
    }

    private static Map<String, String> loadKeyByFile(FileInputStream fis, String password) throws Exception {
        char[] nPassword;
        if (StringUtils.isEmpty(password)) {
            nPassword = null;
        } else {
            nPassword = password.toCharArray();
        }

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(fis, nPassword);
        fis.close();
        Enumeration enumas = ks.aliases();
        String keyAlias = null;
        if (enumas.hasMoreElements()) {
            keyAlias = (String)enumas.nextElement();
        }

        PrivateKey prikey = (PrivateKey)ks.getKey(keyAlias, nPassword);
        Certificate cert = ks.getCertificate(keyAlias);
        PublicKey pubkey = cert.getPublicKey();
        String publicKey = Base64.getEncoder().encodeToString(pubkey.getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(prikey.getEncoded());
        Map<String, String> keyMap = new HashMap(2);
        keyMap.put("publicKey", publicKey);
        keyMap.put("privateKey", privateKey);
        return keyMap;
    }
}
