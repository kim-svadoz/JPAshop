package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // Member를 반환하지 않고 id만 반환하는 이유?
    // Command와 Query를 분리하라.
    // 저장을 하고 나면 가급적이면 리턴값을 만들지 않고, Id정도 있으면 다음에 다시 조회할 수 있으니까 아래 처럼 설계한다.
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
