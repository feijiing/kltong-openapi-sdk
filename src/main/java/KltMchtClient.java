import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import constant.SignTypeEnum;
import dto.Head;
import dto.KltBaseResponse;
import dto.KltRequest;
import org.bouncycastle.crypto.engines.SM2Engine;
import utils.JsonUtils;
import utils.RSAUtils;
import utils.SignUtils;
import utils.StringUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @ClassName:KltMchtClient
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:20
 * @Version: v1.0
 */

public class KltMchtClient {

    private String mchtId;
    private String merchantPrivateKey;
    private SignTypeEnum signType;
    private String kltPublicKey;

    @Deprecated
    public KltMchtClient(String mchtId, String privateKey) {
        if (StringUtils.isEmpty(mchtId)) {
            throw new IllegalArgumentException("商户号不能为null或者空字符串");
        } else if (StringUtils.isEmpty(privateKey)) {
            throw new IllegalArgumentException("商户私钥不能为null或者空字符串");
        } else {
            this.mchtId = mchtId;
            this.merchantPrivateKey = privateKey;
        }
    }

    @Deprecated
    public KltMchtClient(String mchtId, File pfx, String password) {
        if (StringUtils.isEmpty(mchtId)) {
            throw new IllegalArgumentException("商户号不能为null或者空字符串");
        } else {
            this.mchtId = mchtId;
            if (pfx == null) {
                throw new IllegalArgumentException("证书不能为null");
            } else if (StringUtils.isEmpty(password)) {
                throw new IllegalArgumentException("证书密码不能为null或者空字符串");
            } else {
                try {
                    Map<String, String> map = RSAUtils.loadKeyByFile(pfx, password);
                    this.merchantPrivateKey = map.get("privateKey");
                } catch (Exception var5) {
                    throw new RuntimeException("创建客户端出现异常！", var5);
                }
            }
        }
    }

    public KltMchtClient(String mchtId, String privateKey, String kltPublicKey, SignTypeEnum signType) {
        this(mchtId, privateKey);
        this.signType = signType;
        this.kltPublicKey = kltPublicKey;
    }

    public <T> String buildHttpBody(T content) {

        KltRequest<T> request = this.buildKltRequest(content);
        if (SignTypeEnum.SM2.equals(this.signType)) {
            String jsonContent = JSON.toJSONString(content);
            SM2 sm22 = new SM2(null, kltPublicKey);
            sm22.setMode(SM2Engine.Mode.C1C2C3);
            // hutool 加密后需要去除头部的00或者04字符
            String dataByte = sm22.encryptHex(jsonContent, KeyType.PublicKey).substring(2);
            KltRequest<String> sm2Request = this.buildKltRequest(request.getHead(), dataByte);
            return JSON.toJSONString(sm2Request);
        }
        return JSON.toJSONString(request);
    }

    public boolean checkKltResponse(KltBaseResponse response, String kltPublicKey) throws Exception {
        String signMsg = response.getSignMsg();
        String plaintext = this.getPlaintext(response);
        return SignUtils.verySign(plaintext, signMsg, kltPublicKey, this.signType);
    }
    public boolean checkKltResponse(KltBaseResponse response) throws Exception {
        return checkKltResponse(response, this.kltPublicKey);
    }

    /**
     * 对响应结果解码并封装响应结果，只有国密需要解码
     *
     * @param response
     * @return
     */
    public <T> T decodeAndRenderResponse(String response, Class<T> responseType) {
        if (SignTypeEnum.RSA.equals(signType)) {
            return JSON.parseObject(response, responseType);
        } else {
            response = response.replace("\"", "");
            SM2 sm22 = new SM2(merchantPrivateKey, null);
            sm22.setMode(SM2Engine.Mode.C1C2C3);
            // hutool 解密需要加04
            byte[] decrypt = sm22.decrypt("04" + response, KeyType.PrivateKey);
            response = new String(decrypt);
            return JSON.parseObject(response, responseType);
        }
    }

    private <T> KltRequest<T> buildKltRequest(T content) {
        KltRequest<T> request = new KltRequest();
        request.setContent(content);
        Head head = new Head(this.mchtId, this.signType.getCode());
        request.setHead(head);
        String sign = this.getSign(request);
        request.getHead().setSign(sign);
        return request;
    }

    private KltRequest<String> buildKltRequest(Head head, String content) {
        KltRequest<String> request = new KltRequest();
        request.setContent(content);
        request.setHead(head);
        return request;
    }

    private String getSign(KltRequest kltRequest) {
        this.checkKltRequest(kltRequest);
        String plaintext = this.getPlaintext(kltRequest);
        try {
            return SignUtils.sign(plaintext, this.merchantPrivateKey, this.signType);
        } catch (Exception var4) {
            throw new RuntimeException("加签失败！", var4);
        }
    }

    private void checkKltRequest(KltRequest kltRequest) {
        String error = null;
        if (kltRequest == null) {
            error = "KltRequest不能为null";
        } else if (kltRequest.getHead() == null) {
            error = "KltRequest中的Head不能为null";
        } else if (StringUtils.isEmpty(kltRequest.getHead().getMerchantId())) {
            error = "KltRequest中Head的merchantId不能为null或者空字符串";
        } else if (kltRequest.getContent() == null) {
            error = "KltRequest中的content不能为null";
        }

        if (error != null) {
            throw new IllegalArgumentException(error);
        }
    }

    private String getPlaintext(KltRequest kltRequest) {
        JSONObject json = (JSONObject) JSON.toJSON(kltRequest);
        json = JsonUtils.sort(json);
        JSONObject head = (JSONObject) json.get("head");
        head.remove("sign");
        TreeMap<String, Object> treeMap = new TreeMap(head.getInnerMap());
        Object content = json.get("content");
        if (content instanceof JSONArray) {
            String con = ((JSONArray) content).toJSONString();
            treeMap.put("content", con);
        } else if (content instanceof JSONObject) {
            JSONObject contentObject = (JSONObject) content;
            treeMap.putAll(contentObject.getInnerMap());
        }

        return this.getPlaintext(treeMap);
    }

    private String getPlaintext(KltBaseResponse response) {
        JSONObject json = (JSONObject) JSONObject.toJSON(response);
        json = JsonUtils.sort(json);
        json.remove("signMsg");
        TreeMap<String, Object> treeMap = new TreeMap();
        Iterator var4 = json.getInnerMap().entrySet().iterator();

        while (true) {
            Map.Entry entry;
            Object value;
            do {
                do {
                    if (!var4.hasNext()) {
                        return this.getPlaintext(treeMap);
                    }

                    entry = (Map.Entry) var4.next();
                    value = entry.getValue();
                } while (value == null);
            } while (value instanceof String && ((String) value).trim().isEmpty());

            if (!(value instanceof String)) {
                value = JSON.toJSONString(value);
            }

            treeMap.put((String) entry.getKey(), value);
        }
    }

    private String getPlaintext(TreeMap<String, Object> treeMap) {
        StringBuilder sb = new StringBuilder();
        Iterator var3 = treeMap.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) var3.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null && !value.toString().trim().isEmpty()) {
                sb.append(key).append("=").append(value).append("&");
            }
        }

        if (sb.length() == 0) {
            return sb.toString();
        } else {
            return sb.substring(0, sb.length() - 1);
        }
    }

}
