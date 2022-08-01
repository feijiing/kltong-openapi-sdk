import constant.SignTypeEnum;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

/**
 * Hello world!
 */
public class AppMain {
    private static CloseableHttpClient httpClient = initHttpClient();

    public static void main(String[] args) throws Exception {

        // KltMchtClient可在系统初始化时创建，无须每次请求时都进行创建
        // rsa
//        String kltPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGPRZwDLYut6UXuPradRHyQVTg/Al12p8l0K7FaKGoH/mzfFXj8/2ee5JnwYpygc/6ZD2fDw5r4a2BDgF4zZfIAxwyfXajT+PvwWJZ9pa8cY7OpdbcSsxVlGAsXZjTcHagtGy5jxibzsnoV+nr88ouoUJFPeN/USyOPpkBKWM66tkBH+feMeUJc/6xXz7f+XJL9wD9Wm8rn1NadcVc0v3CLE6qOtMMDqxfHpUflqV0ar4Adn2C+s73GmQqRLXa6In6Lnu5E3H/1stMCiyWYFjHV13RiLpDsvnH4N1blGbnHrw6FGn270JZdzEzOsYUHBG5yOVx74sUawxSCorLXO2QIDAQAB";
//        String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDUMk3vUPsaEQ2MkLpoIDCQv4jl2y2bJdiEbdaxlTuxYG8u7znhkLKsnf+QXcD7TcfsCKTLL00luuaHn8Z4gfBCw6sYnfILue8tBvpOB7+Wlscpe8vleWNCSw1f/w75mYfD6VEm3geX3LysN8dK+c4ug2V884sGwN0zGON7zeBYEsIHwfLBAZ4fpYuP3gwwcRh3NQfJQNOG69jy+oTYM6/J0uxqKqrWGZ9D6YgfGE/MxPie8WIjmc1s/FbgWc2ZzAGdfyenhep2zFC4L8IbXVZllkuXtEFnrtPgNkMgcouMeKS+K8aYmG7esDiNXFKM6mIZERb+SLn5OJ2M4HGwwF8dAgMBAAECggEAIHQSIdYllVLC2vr+m3LC4rx8fEUliDtFTlm7Mn1DvCjm0EuTs2ktPoPKpKKGI5k9iJ64z3+MQcdAP0FjOiaLX7ar2g+8B2mYSC9vRFRgi7BDRRezM0U5tleis8ofWPLPhhnOyM4/5IzEyWUdhJzImSoM8UuROYbxPcGidxqhY26xtWrLMUn4WAf9Wg/+Zr8BYab4A4NR2YRPqgNf3BxnAU1REog/JVjJazYsQxy6XTgj7BIR+yTSe5SZ1mi4m0a0vo09+kS612gCJDkFQHa/nqnpT3n9jluEMNNx8bwhu+1dDgXOVtvvarOWkYnofI1WbXnXu3dNV2YpZuv3dRbkAQKBgQDqW5C7OV40FLKnvYbfXiYhaLqECL3ted8oqi0MypEAN0pkYW5DWZ09WArRU++I8ZOkY5vPstDQODTmgGbcqZvr7JFlIBT9u2Tv3VdshXM9fwTB4HDhI2uY+Xxrr0vL4UxW+OQf+Y84qVKMmzidT7dZwhdimN7emjsMMZd7tCosAQKBgQDnytQCkBgyAA9Z/ysS4PsnXNQHwLUGjjra4//LKa3EeoKy1M9jnvEfM/Mb4EZ2Q58s9SsB3MhCCMOY4ciBSZj5H636UoC86qHb1v2ci1JLYklV1T188+DIVICfqb9XiBpx6duMHl48XdxFBTMj5rRDIfPbYEKCl9ZjHpqV3PVjHQKBgQCNg9kzlQKzhEQVUjbdLqbryafHDthRTCRbE9e8P52rebesQcEpXjW60y1FFeAw21+CBrwRgTLiaO5YufRjDvAeWG27mNHUHZJn3UprN3JNEiQ3RmYjLRZN+2kMRYAJjvRgGE74l+PKHUTBrwFSzM40nyUr3o/F72/0fwWwJyzIAQKBgQDIxHEAS0j5vf2OCSuI7Z/8xzXMBUmHBRjLUaLUmSQuw6KDPTro4dmeGkQfSBIgC5BKw4xdz5sRP0AUIrSl71Z1qc/qux1RBLXvkfcXacF9FAPLOalJmn9/ZVoHcWMPcQ7ezh1g70jWZsIMcmWKWiCW6UisImNZdCS4s+BXoAxXrQKBgQCcnHz02aalHobTdQPDAdvF/vMT2KtmEDft3423ucVfn4VfZjs5270z5lYChopiZxt8PGghIJezmhdnXt8NxZwMD9ZG+uoCQI5JRR2KnVxH9zPgRMYIE14e8y5IVChnJAHTC1tmumiL3nvq7AnZR+GFWcYNqOB2vjxaUlKCfWZrPQ==";
//        KltMchtClient client = new KltMchtClient("999410755110001", privateKey,kltPublicKey, SignTypeEnum.RSA);
        // sm2
        String kltPublicKey = "04CA1BCD488F0DD6A44BF45258B05D51A6C4E15AFC4D119D8DF995DF137B316BA3EFEA401A89BEE3F2C5F559FC6CDF938D6B1AD09FFC86C2F8DD39CBEEEE9C373D";
        String privateKey = "CA7C57819157321E31849FAFA80BBD6591789024A37FBF9E091E6F2DA6DBD3D6";
        KltMchtClient client = new KltMchtClient("999000019032401", privateKey,kltPublicKey, SignTypeEnum.SM2);

        // 创建请求中的content
        CardBalanceQueryRequest param = new CardBalanceQueryRequest();
        param.setCardNo("1200061100001000782");
        // 通过content构建加签后的http请求body，用户可以直接使用进行HTTP请求
        String httpBody = client.buildHttpBody(param);

        //http方法为普通的HTTP请求方法，由用户自己实现
//        CardBalanceQueryResponse res = http("https://openapi.chinasmartpay.com/openapi/opc/queryBlance", httpBody, CardBalanceQueryResponse.class);
        String res = http("http://192.168.70.92:8080/opc-openapi/opc/queryBlance", httpBody);
        CardBalanceQueryResponse response =client.decodeAndRenderResponse(res,CardBalanceQueryResponse.class);
        System.out.println("响应结果:{}" + response.toString());

        // 调用KltMchtClient的checkKltResponse(Object response, String kltPublicKey)对返回结果进行验签
        // 对返回结果是否进行验签，由用户自己决定
        boolean checkResult = client.checkKltResponse(response);
        System.out.println(checkResult);

    }

    private static String  http(String url, String httpBody) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = new StringEntity(httpBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            throw new Exception("发送请求失败！==》" + httpBody, e);
        }
        // 请求失败
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("请求失败==>" + response);
        }
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("http响应："+result);
        return result;
    }

    private static CloseableHttpClient initHttpClient() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(null, new TrustAllStrategy()).build();
        } catch (Exception e) {
            throw new RuntimeException("初始化SSLContext出现异常！", e);
        }
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new String[]{"TLSv1"}, null,
                NoopHostnameVerifier.INSTANCE);

        // 配置同时支持 HTTP 和 HTPPS
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslsf)
                .build();
        // 初始化连接管理器
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 同时最多连接数
        poolConnManager.setMaxTotal(32);
        // 设置最大路由
        poolConnManager.setDefaultMaxPerRoute(16);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(60000)
                .build();
        // 初始化httpClient
        return HttpClients.custom()
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
