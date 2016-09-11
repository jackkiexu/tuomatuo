package com.lami.dcm.lbdatasource.dao;

import com.lami.dcm.lbdatasource.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xjk on 9/10/16.
 */
@Repository
public class UserDao extends JdbcDaoSupport {

    @Autowired
    @Qualifier("readWriteDataSource")
    public void setDS(DataSource dataSource){
        super.setDataSource(dataSource);
    }

    public void save(final User user){
        final String sql = "insert into user (name) values (?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getName());
                return ps;
            }
        }, generatedKeyHolder);
        user.setId(generatedKeyHolder.getKey().intValue());
    }

    public void update(User user){
        String sql = "update user set name = ? where id = ?";
        getJdbcTemplate().update(sql, user.getName(), user.getId());
    }

    public void delete(int id){
        String sql = "delete from user where id = ?";
        getJdbcTemplate().update(sql, id);
    }

    public User findById(int id){
        String sql = "select Id, name from user where id = ?";
        List<User> userList = getJdbcTemplate().query(sql, rowMapper, id);
        if(userList.size() == 0){
            return new User();
        }
        return userList.get(0);
    }


    public RowMapper<User> rowMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            return user;
        }
    };
}
