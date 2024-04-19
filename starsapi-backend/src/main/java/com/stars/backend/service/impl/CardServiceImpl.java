package com.stars.backend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stars.backend.mapper.CardMapper;
import com.stars.backend.service.CardService;
import com.stars.common.model.entity.Card;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 卡号服务实现
 * 提供卡号相关操作的接口定义，包括生成卡号等。
 *
 * @author stars
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    /**
     * 生成卡号
     * 生成一批卡号并将其存储到数据库中。
     *
     * @return 是否成功生成卡号
     */
    @Override
    public boolean generateCard() {
        String key = RandomUtil.randomNumbers(7);
        String password = RandomUtil.randomNumbers(7);
        String md5Key = SecureUtil.md5(key);
        String md5Password = SecureUtil.md5(password);
        Card card = new Card();
        card.setCardNumber(md5Key);
        card.setCardPassword(md5Password);
        boolean save = save(card);
        if (save) {
            BufferedWriter bufferedWriter = null;
            try {
                // todo 开发环境
                bufferedWriter = new BufferedWriter(
                        new FileWriter(
                                "D:\\ABD\\Desktop\\Projects\\starsapi-GIT\\starsapi\\starsapi-frontend\\public\\cardpassword.txt", true));
                // todo 线上环境
//                bufferedWriter = new BufferedWriter(new FileWriter("//root//services//starsapi-frontend//cardpassword.txt", true));
                bufferedWriter.newLine();
                bufferedWriter.write("" + key);
                bufferedWriter.write("             " + password);
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return save;
    }
}
