package org.deku.leoz.central.data

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import javax.inject.Inject

/**
 * Created by 27694066 on 25.04.2017.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class
))
class UserJooqRepositoryTest {

    @Inject
    private lateinit var userJooqRepository: JooqUserRepository

//    @Test
//    fun testCreateUser() {
//        userJooqRepository.createUser(User(
//                "foo@bar.com",
//                0,
//                null,
//                "prangenberg",
//                User.ROLE_ADMINISTRATOR,
//                "deku4711",
//                "Philipp",
//                "Prangenberg",
//                null,
//                true,
//                false,
//                "+66779582",
//                null
//        ))
//        assert(userJooqRepository.verifyCredentials("foo@bar.com", "deku4711"))
//    }
}