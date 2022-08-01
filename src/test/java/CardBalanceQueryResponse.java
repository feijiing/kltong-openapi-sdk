import dto.KltBaseResponse;
import lombok.Data;
import lombok.ToString;

/**
 * @author daifei
 * @date 2019/11/4
 */
@Data
@ToString(callSuper = true)
public class CardBalanceQueryResponse extends KltBaseResponse {

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 余额
     */
    private Long balance;
    /**
     * 卡状态(1-正常, 2-过期, 3-注销, 4-冻结, 5-挂失)
     */
    private String status;
    /**
     * 可用余额
     */
    private Long availableAmount;
    /**
     * 冻结金额
     */
    private Long frozenAmount;
}
