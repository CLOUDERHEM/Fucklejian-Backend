package com.lc.legym.mapper;

import com.lc.legym.model.AkDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 7:36 PM
 */
@Slf4j
@Component
public class AkMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer insertAkDO(AkDO ak) {
        return jdbcTemplate.execute("insert into legym( ak, usage_count, total_count, create_time ,update_time) values (?,?,?,?,?)",
                (PreparedStatementCallback<Integer>) ps -> {
                    ps.setString(1, ak.getAk());
                    ps.setInt(2, ak.getUsageCount());
                    ps.setInt(3, ak.getTotalCount());
                    ps.setLong(4, ak.getCreateTime());
                    ps.setLong(5, ak.getUpdateTime());
                    try {
                        ps.execute();
                    } catch (SQLException e) {
                        log.info("", e);
                        return 0;
                    }
                    return 1;
                });
    }


    public Integer useAkDo(String ak) {
        return jdbcTemplate.execute("update legym set usage_count = usage_count + 1, update_time = ? where ak=?",
                (PreparedStatementCallback<Integer>) ps -> {
                    ps.setLong(1, System.currentTimeMillis());
                    ps.setString(2, ak);
                    try {
                        ps.execute();
                    } catch (SQLException e) {
                        log.info("", e);
                        return null;
                    }
                    return 1;
                });
    }

    public AkDO getAkDo(String ak) {
        return jdbcTemplate.execute("select usage_count,total_count,update_time,create_time  from legym where ak=?",
                (PreparedStatementCallback<AkDO>) ps -> {

                    ps.setString(1, ak);

                    ResultSet resultSet = ps.executeQuery();
                    if (resultSet.next()) {
                        AkDO akDO = new AkDO();
                        akDO.setAk(ak);
                        akDO.setUsageCount(resultSet.getInt("usage_count"));
                        akDO.setTotalCount(resultSet.getInt("total_count"));
                        akDO.setUpdateTime(resultSet.getLong("update_time"));
                        akDO.setCreateTime(resultSet.getLong("create_time"));

                        return akDO;
                    }

                    return null;
                });
    }
}
