package dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName:KltRequest
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:16
 * @Version: v1.0
 */
@Data
public class KltRequest <T> implements Serializable {

    private Head head;
    private T content;
}
