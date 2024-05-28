package com.xcs.spring;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class ScoresServiceImpl implements ScoresService {

    @Override
    @Transactional(
            readOnly = true,
            rollbackFor = Exception.class,
            isolation = Isolation.REPEATABLE_READ,
            timeout = 30
    )
    public void insertScore() {
        // TODO
    }
}
