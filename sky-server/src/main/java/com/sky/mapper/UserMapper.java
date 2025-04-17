package com.sky.mapper;

import com.sky.entity.User;
import com.sky.entity.UserCountByDate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     * @return
     */
    void insert(User user);

    /**
     * 根据id获取User
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 批量插入用户
     * @param users
     */
    void insertBatch(List<User> users);

    /**
     * 批量取出用户
     * @return
     */
    @Select("select DATE(create_time) as date, COUNT(*) as count from user group by date order by date asc")
    List<UserCountByDate> get();

    /**
     * 按照begin和end批量取出用户
     * @param begin
     * @param end
     * @return
     */
    @Select("select DATE(create_time) as date, COUNT(*) as count from user where create_time >= #{begin} && create_time <= #{end} group by date order by date asc")
    List<UserCountByDate> getByDate(LocalDateTime begin, LocalDateTime end);

    /**
     * 获取时间段内的用户数
     * @return
     */
    @Select("select Count(id) from user where create_time >= #{begin} and create_time < #{end}")
    Integer getUsersCount(LocalDateTime begin, LocalDateTime end);
}
