package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    // 필드 주입
    //@Autowired
    // 원활한 수정(유지보수)를 위해서는 필드주입 보다는
    // 이 필드는 변경할 일이 없기 때문에 final로 하는 것이 안전하다.
    // final을 하면 컴파일 시점에 체크할 수 있기 때문에
    // 여기에 lombokk을 적용하면
    private final MemberRepository memberRepository;

    // Setter Injection
    // 장점 : 파라미터로 저렇게 주입할 수가 있따.
    // 단점 :
    // 따라서 생성자 주입을 쓰는게 더 좋다.

    // 생성자 주입
    // 생성자가 하나만 있는 경우에는 @Autowired를 생략해도 된다.
    //@Autowired
    // lombok(@RequiredArgsConstructor)을 적용하면 생성자주입을 쓰지 않아도 된다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원 가입
    // 기본적으로 readOnly = false이다.
    // 조회기능 같은 읽기전용인 것은 readOnly = true로 하는 것이 성능 최적화
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        // 멀티쓰레드 환경을 고려해, member의 name을 Unique 제약 조건으로 걸어 두는 것이 안전하다.
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    
    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
