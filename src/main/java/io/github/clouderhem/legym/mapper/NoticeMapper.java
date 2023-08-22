package io.github.clouderhem.legym.mapper;

import io.github.clouderhem.legym.model.NoticeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

/**
 * @author Aaron Yeung
 * @date 9/8/2022 9:07 PM
 */
@Component
public class NoticeMapper {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NoticeDO getNotice() {
        return jdbcTemplate.execute("select id, title, msg, author, type, `show` , create_time, update_time from notice limit 1",
                (PreparedStatementCallback<NoticeDO>) ps -> {
                    ResultSet resultSet = ps.executeQuery();
                    if (resultSet.next()) {
                        NoticeDO noticeDO = new NoticeDO();
                        noticeDO.setId(resultSet.getInt("id"));
                        noticeDO.setTitle(resultSet.getString("title"));
                        noticeDO.setMsg(resultSet.getString("msg"));
                        noticeDO.setAuthor(resultSet.getString("author"));
                        noticeDO.setType(resultSet.getInt("type"));
                        noticeDO.setShow(resultSet.getInt("show"));
                        noticeDO.setCreateTime(resultSet.getLong("create_time"));
                        noticeDO.setUpdateTime(resultSet.getLong("update_time"));

                        return noticeDO;
                    }
                    return null;
                });
    }
}
