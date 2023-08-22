package io.github.clouderhem.legym.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Aaron Yeung
 * @date 9/8/2022 9:25 PM
 */
@SpringBootTest
public class NoticeMapperTest {

    @Autowired
    NoticeMapper noticeMapper;

    @Test
    public void test(){
        System.out.println(noticeMapper.getNotice());
    }
}
