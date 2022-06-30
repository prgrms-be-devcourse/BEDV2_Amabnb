package com.prgrms.amabnb.config;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryConfig.class)
public abstract class RepositoryTest {
}
