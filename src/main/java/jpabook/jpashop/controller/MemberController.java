package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    /*
        폼 객체를 써야되나, 엔티티를 직접 써야 하는것인가?
        요구사항이 정말 간단하다면 폼 없이 엔티티를 그대로 사용해도 된다.
        하지만 실무에서는 요구사항이 단순하지 않기 때문에 폼객체를 만들어서 사용하는 것이 좋다.
        (화면에 종속적이기 때문에, 엔티티가 지저분해지고 유지보수가 어려워지기 때문이다.)
        (엔티티는 dependency 없이 오직 핵심 비즈니스 로직에만 디펜던시가 있도록 설계하는 것이 중요하다.)
        (애플리케이션이 점점 커져도, 엔티티를 여러 곳에서 사용하더라도 유지보수력이 높아진다!)
        (따라서 실무에서는 DTO로 변환해서 화면에 뿌리는 것을 추천한다.)

        - API를 만들때에는 이유를 불문하고, 엔티티를 절대로 외부로 반환하면 안된다.
        API라는것은 스펙이기 때문에 멤버 엔티티를 반환한다? 멤버가 추가된다면 문제가 생긴다.
        1. 패스워드가 노출이되고, 2.API 스펙이 변한다

        - 템플릿에서 사용하는 것은 엔티티를 사용해도 되지만 폼 객체로 사용하는 것이 제일 깔끔하다.
     */

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

}
