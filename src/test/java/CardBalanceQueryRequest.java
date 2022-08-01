import lombok.Data;

import java.io.Serializable;

/**
 * @author daifei
 * @date 2019/11/4
 */
@Data
public class CardBalanceQueryRequest implements Serializable {

    /**
     * 卡号
     */
    private String cardNo;

}
