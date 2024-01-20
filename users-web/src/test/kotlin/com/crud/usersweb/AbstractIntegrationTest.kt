package com.crud.usersweb

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.OracleContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
abstract class AbstractIntegrationTest {

    companion object {
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        private val oracleContainer = OracleContainer(
            DockerImageName
                .parse("container-registry.oracle.com/database/express:21.3.0-xe")
                .asCompatibleSubstituteFor("gvenzl/oracle-xe")
        )
            .withPassword("123456")
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            if (configurableApplicationContext.environment.activeProfiles.any { it == "oracle" }) {
                oracleContainer.start()

            } else {
                postgreSQLContainer.start()
                TestPropertyValues.of(
                    "spring.datasource.url=${postgreSQLContainer.jdbcUrl}",
                    "spring.datasource.username=${postgreSQLContainer.username}",
                    "spring.datasource.password=${postgreSQLContainer.password}"
                ).applyTo(configurableApplicationContext.environment)
            }
        }
    }

}
