package com.stars.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.vo.InterfVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口数据库操作
 *
 * @author stars
 */
public interface InterfMapper extends BaseMapper<Interf> {

    List<InterfVO> selectMyInterfByPage(
            @Param("userId") long userId,
            @Param("start") int start,
            @Param("pageSize") long pageSize,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("interfDescription") String interfDescription);

    int selectMyInterfCount(long userId);
}
