package io.github.clouderhem.legym.mapper;

import io.github.clouderhem.legym.model.AkDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 8:16 PM
 */
@SpringBootTest
public class AkMapperTest {

    @Autowired
    private AkMapper akMapper;

    @Test
    public void test() {
        String akId = UUID.randomUUID().toString();
        AkDO ak = new AkDO();
        ak.setAk(akId);
        ak.setUsageCount(0);
        ak.setTotalCount(10);
        long timeMillis = System.currentTimeMillis();
        ak.setUpdateTime(timeMillis);
        ak.setCreateTime(timeMillis);

        System.out.println(akMapper.insertAkDO(ak));
        System.out.println(akMapper.useAkDo(akId));
        System.out.println(akMapper.useAkDo(akId));
        System.out.println(akMapper.getAkDo(akId));


    }
    @Test
    public void test2(){
        System.out.println(akMapper.getAkDo("%%"));
        System.out.println(akMapper.getAkDo("id; select 1 --"));
    }
}
