package com.stars.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stars.common.model.entity.Card;

/**
 * 卡号服务接口
 * 提供卡号相关操作的接口定义，包括生成卡号等。
 *
 * @author stars
 */
public interface CardService extends IService<Card> {

    /**
     * 生成卡号
     * 生成一批卡号并将其存储到数据库中。
     *
     * @return 是否成功生成卡号
     */
    boolean generateCard();
}
