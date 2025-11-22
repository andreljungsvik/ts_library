package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.radcortez.flyway.test.annotation.H2;
import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;

@Tag("integration")
@H2
public class UserDaoIntegrationTest {
    private static DataSource ds;

    @BeforeAll
    static void initDataSource() {
// this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        UserDaoIntegrationTest.ds = ds;
    }

    @Test
    void get_existingUser_returnsUser() {
// this data comes from the test migration files
        final String username = "test";
        final UserId userId = UserId.of(1);
        UserDao userDao = new UserDao(ds);
        Optional<User> maybeUser = userDao.get(Integer.toString(userId.getId()));
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.get().getName()).isEqualTo(username);
        assertThat(maybeUser.get().getId()).isEqualTo(userId);
    }

    @Test
    void get_nonExistingUser_returnsEmpty() {
        final String username = "test";
        final UserId userId = UserId.of(4);
        UserDao userDao = new UserDao(ds);
        Optional<User> maybeUser = userDao.get(Integer.toString(userId.getId()));
        assertThat(maybeUser).isEmpty();
    }

    @Test
    void isNameAvailable_existingName_returnsFalse() {
        UserDao dao = new UserDao(ds);
        assertThat(dao.isNameAvailable("test")).isFalse();
    }

    @Test
    void isNameAvailable_newName_returnsTrue() {
        UserDao dao = new UserDao(ds);
        assertThat(dao.isNameAvailable("bossetheman")).isTrue();
    }

    @Test
    void register_newUser_andGetLoginInfo_returnsCorrectUser() {
        UserDao dao = new UserDao(ds);
        String username = "bossetheman";
        String realname = "Bosse Bredsladd";
        String password = "abcde12345";

        boolean registered = dao.register(username, realname, password);
        assertThat(registered).isTrue();

        Optional<User> userOpt = dao.getLoginInfo(username).flatMap(info -> dao.get(Integer.toString(info.getUserId().getId())));
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getName()).isEqualTo(username);
        assertThat(userOpt.get().getRealname()).isEqualTo(realname);
    }
}