package dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName:KltBaseResponse
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:15
 * @Version: v1.0
 */
@Data
public class KltBaseResponse implements Serializable {

    private String responseCode;
    private String responseMsg;
    private String requestId;
    private String mchtId;
    private String signMsg;
    private String signType;
    private Long time;
    private String errorCode;
    private Object data;
}
