package org.cxs.demo.controller;


import org.cxs.demo.dao.Demo02Mapper;
import org.cxs.demo.entity.Demo01;
import org.cxs.demo.entity.Demo02;
import org.cxs.demo.es.Demo01Repository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cxs
 * @since 2021-12-05
 */
@RestController
@RequestMapping("/demo02")
public class Demo02Controller {

    @Autowired
    Demo01Repository demo01Repository;

    @Autowired
    Demo02Mapper mapper;


    @PostMapping("delete")
    public void delete(@RequestParam Long id) {
        mapper.deleteById(id);
    }

    @PostMapping("update")
    public void update(@RequestBody Demo02 demo02) {
        if (demo02.getId() != null) {
            mapper.updateById(demo02);
        }
    }

    @PostMapping("add")
    public void add(@RequestBody Demo02 demo02) {
        mapper.insert(demo02);
    }

    @PostMapping("addByEsId")
    public void addByEsId(@RequestParam Long id) {
        Optional<Demo01> demo01 = demo01Repository.findById(id);
        if (demo01.isPresent()) {
            Demo02 demo02 = new Demo02();
            BeanUtils.copyProperties(demo01.get(), demo02);
            Demo02 search = mapper.selectById(id);
            if (search == null) {
                mapper.insert(demo02);
            } else {
                demo02.setId(id);
                mapper.updateById(demo02);
            }
        }
    }
}
