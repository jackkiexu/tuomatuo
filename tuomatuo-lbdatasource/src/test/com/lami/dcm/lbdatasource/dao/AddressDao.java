package com.lami.dcm.lbdatasource.dao;

import com.lami.dcm.lbdatasource.model.Address;
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
public class AddressDao extends JdbcDaoSupport{

    @Autowired
    @Qualifier("readWriteDataSource")
    public void setDS(DataSource ds){
        setDataSource(ds);
    }

    public void save(final Address address){
        final String sql = "insert into address(userId, cityId values (?,?))";
        KeyHolder generatedHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, address.getUserId());
                ps.setString(2, address.getCityId());
                return ps;
            }
        }, generatedHolder);
        address.setId(generatedHolder.getKey().intValue());
    }

    public Address findByID(int id){
        String sql = "select id, userId, city from address where id = ?";
        List<Address> addressList = getJdbcTemplate().query(sql, rowMapper, id);
        if(addressList.size() == 0){
            return null;
        }
        return addressList.get(0);

    }

    private RowMapper<Address> rowMapper = new RowMapper<Address>() {
        public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
            Address address = new Address();
            address.setId(rs.getInt("id"));
            address.setUserId(rs.getInt("addressId"));
            address.setCityId(rs.getString("CityId"));
            return address;
        }
    };

}
