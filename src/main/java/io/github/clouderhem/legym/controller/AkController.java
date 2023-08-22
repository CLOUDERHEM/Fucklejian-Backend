package io.github.clouderhem.legym.controller;

import io.github.clouderhem.legym.enums.Constant;
import io.github.clouderhem.legym.mapper.AkMapper;
import io.github.clouderhem.legym.model.AkDO;
import io.github.clouderhem.legym.model.vo.RequestAkVO;
import io.github.clouderhem.legym.util.EncryptUtils;
import io.github.clouderhem.legym.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 9:19 PM
 */
@RequestMapping("/api")
@RestController
public class AkController {


    private AkMapper akMapper;

    @Autowired
    public void setAkMapper(AkMapper akMapper) {
        this.akMapper = akMapper;
    }

    @PostMapping("/ak/generate")
    public ResultData<AkDO> generate(@RequestBody @Validated RequestAkVO akVO) {

        if (!Constant.PRIVATE_AK.equals(akVO.getAk())) {
            return ResultData.error("私人秘钥不正确");
        }
        AkDO result = new AkDO();
        result.setAk(EncryptUtils.hs(UUID.randomUUID().toString()));
        result.setUsageCount(0);
        result.setTotalCount(akVO.getCount());
        long timeMillis = System.currentTimeMillis();
        result.setCreateTime(timeMillis);
        result.setUpdateTime(timeMillis);

        Integer integer = akMapper.insertAkDO(result);
        if (integer == null || integer == 0) {
            return ResultData.error("ak创建失败");
        }
        return ResultData.success("ak创建成功", result);

    }

    @GetMapping("/ak/query")
    public ResultData<AkDO> queryOne(@RequestParam String ak) {
        AkDO akDo = akMapper.getAkDo(ak);
        if (akDo == null) {
            return ResultData.error("没有此邀请码");
        }
        return ResultData.success("查询成功", akDo);
    }
}
