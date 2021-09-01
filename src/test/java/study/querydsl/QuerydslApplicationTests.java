package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {


	@Autowired // @PersistentContext 같은 기능 .
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();

		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = new QHello("hello"); // 파라미터는 alias

		Hello result = query
				.selectFrom(qHello)
				.fetchOne();


		Assertions.assertThat(result).isEqualTo(hello);

	}

}
