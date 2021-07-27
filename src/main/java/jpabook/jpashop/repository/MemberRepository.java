package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 해당 Annotation이 있으면 EntityManager를 Spring의 Bean에 주입을 해주게 된다.
    // EntityManagerFactory를 직접 주입할 수 도 있긴 하다.
    // Springboot(정확히는 Spring data-JPA)을 쓰면 @PersistenceContext를 @Autowired로 사용할 수 있고. 그렇다면? -> Lombok을 활용해 생성자 주입 가능
    //@PersistenceContext
    private final EntityManager em;

    // Member를 반환하지 않고 id만 반환하는 이유?
    // Command와 Query를 분리하라.
    // 저장을 하고 나면 가급적이면 리턴값을 만들지 않고, Id정도 있으면 다음에 다시 조회할 수 있으니까 아래 처럼 설계한다.
    // em.persist()할 때 영속성 컨텍스트에 해당 값이 들어 간다.(@GeneratedValue로 생성된 ID(key) 값이 들어가게 된다.)
    // JPA에서는 persist()를 할 때 기본적으로 insert문이 나가지 않는다.
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // SQL은 테이블을 대상으로 쿼리하고, JPQL은 엔티티 객체를 대상으로 뽑아온다.
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
