package com.crud.userswebfluxcoroutine

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.OracleContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
abstract class AbstractIntegrationTest {

    companion object {
        private val oracleContainer = OracleContainer(
            DockerImageName
                .parse("container-registry.oracle.com/database/express:21.3.0-xe")
                .asCompatibleSubstituteFor("gvenzl/oracle-xe")
        ).apply {
            withEnv("ORACLE_PWD", "123456")
            portBindings = listOf("1521:1521")
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            oracleContainer.start()
        }
    }

}
