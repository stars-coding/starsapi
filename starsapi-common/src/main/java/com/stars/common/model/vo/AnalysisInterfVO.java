package com.stars.common.model.vo;

import com.stars.common.model.entity.Interf;
import lombok.Data;

/**
 * 分析接口视图
 *
 * @author stars
 */
@Data
public class AnalysisInterfVO extends Interf {

    /**
     * 总计调用次数
     */
    private Long totalInvokeNum;
}
