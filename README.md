# [ JpaShop ]

---

>    SpringBoot 2.x
>
>    JPA

## 도메인 분석 설계

- 요구사항 분석
- 도메인모델과 테이블 설계
    - 엔티티 분석
    - 연관관계 매핑 분석
- 엔티티 클래스 개발
    - 회원, 주문, 주문상태, 주문상품, 상품, 배송, 배송상태, 카테고리 엔티티
    - `@ManyToMany`는 편리한 것 같지만, 중간 테이블에 컬럼을 추가할 수 없고 세밀하게 쿼리를 실행하기 어렵기 때문에, 실무에서는 중간엔티티(`CategoryItem`)을 만들고 `@ManyToOne`이나 `@OneToMany`로 매핑하자
    - **엔티티에는 가급적 Setter 사용하지 말자**
        - Setter가 모두 열려 있으면 변경 포인트가 많아서, 유지보수가 어렵다.
    - **모든 연관관계는 지연로딩으로 설정하자.**
        - 즉시로딩은(`EAGER`)은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다.
        - `@XToOne` 관계는 즉시로딩이 디폴트이므로, 직접 지연로딩으로 설정해야 한다.
    - **컬렉션을 필드에서 초기화 하자.**
        - 컬렉션은 필드에서 초기화 하는 것이 `null`문제에서 안전하다
        - 하이버네이트는 엔티티를 영속화 할때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경하기 때문에 필드레벨에서 생성하는 것이 안전하고, 코드도 간결하다.
    - **양방향 연관관계에서 외래키가 연관관계의 주인이다.**
        - 연관관계 주인쪽에서 값을 세팅해야 값이 변경된다. (반대쪽(거울쪽)은 그냥 단순히 조회용으로 사용된다.)

---

## 애플리케이션 구현

- 회원 기능

    - 회원 등록, 조회

- 상품 기능

    - 상품 등록, 수정, 조회

- 주문 기능

    - 상품 주문, 주문 내역 조회, 주문 취소

- 계층형 구조 사용

    - Controller : 웹 계층
    - Service : 비즈니스 로직, 트랜잭션 처리
    - Repository : JPA를 직접 사용하는 계층, 엔티티 매니저 사용
    - Domain : 엔티티가 모여 있는 계층, 모든 계층에서 사용

- **개발 순서는 서비스와 레포지토리 계층을 개발하고, 테스트케이스를 작성해서 검증한 뒤에 마지막으로 웹 계층을 적용한다.(도메인은 요구사항을 분석하며 엔티티 클래스의 뼈대을 먼저 진행했다.)**

---

## 도메인 개발

- 회원 도메인
    - 회원 엔티티
    - 회원 레포지토리
    - 회원 서비스
    - 회원 기능 테스트
- 상품 도메인
    - 상품 엔티티
    - 상품 레포지토리
    - 상품 서비스
    - 상품 테스트
- 주문 도메인
    - 주문 엔티티
    - 주문상품 엔티티
    - 주문 레포지토리
    - 주문 서비스
    - 주문 기능 테스트

*++ 주문 검색기능 개발 (JPA에서는 동적쿼리를 어떻게? -> `QueryDSL`)*

---

## 웹 계층 개발

- 홈 컨트롤러
- 회원 등록 컨트롤러
- 회원 목록 컨트롤러
- 상품 등록 컨트롤러
- 상품 목록 컨트롤러
- 상품 수정 컨트롤러
- 상품 주문 컨트롤러
- 주문 목록 검색, 취소 컨트롤러



## > 엔티티를 변경하는 방법

- 변경감지를 사용하라.
    - 컨트롤러에서 어설프게 엔티티를 생성하지 마라
    - 트랜잭션이 있는 서비스 계층에 식별자(`id`)와 변경할 데이터를 명확하게 전달하라.(파라미터 or DTO)
    - 트랜잭션이 있는 서비스 계층에서 영속상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하라.
    - 트랜잭션 커밋 시점에 변경 감지가 실행된다.

## > 질답

---

@Autowired 를 통하여 여러개의 Repository 를 하나의 메소드에서 처리해도되나요?

```bash
@RequiredArgsConstructor 는 final 키워드 혹은 @NonNull 붙은 필드의 생성자를 만들어주는 역할을 하는데요

생성자를 직접 만들어주셨으니 @RequiredArgsConstructor 없이 생성자 주입이 가능합니다.

또한 스프링 4.3부터 생성자가 하나일 때 @Autowired를 생략이 가능하며 올리신 코드에서 @Autowired를 생략해도 정상적으로 빈 주입이 됩니다.
```

---

[Controller와 Service 로직 구분에 대한 질문과 답변 1](https://www.inflearn.com/questions/55208)

안녕하세요 강사님! 강의 잘듣고 있습니다ㅎㅎ

비지니스 로직을 제가 Controller에 많이 넣는것 같습니다ㅜ

controller와 service의 구분이 잘 안되는것 같아서

혹시 Controller와 Service에 넣는 코드들은 어떤 기준이 있는지 여쭤봐도 될까요?!

(객체에 set해주는 부분들은 보통 controller에서 처리를 하나요 아니면 view에서 값을 controller에서 받아온후 service로 넘겨서 처리를 해주는건가요?)

```bash
안녕하세요. BeomJun Lee님^^

Controller는 사용자의 입력을 분석하고 사용자에게 제공될 뷰에 데이터를 전달하는 역할을 합니다.

좀 극단적이기는 한데요. 이렇게 딱 생각하시면 좋습니다!

컨트롤러같은 웹 계층이 없어도 애플리케이션이 동작해야 합니다.

컨트롤러는 웹 계층을 처리하기 위한 코드만 존재하는 것이 좋습니다. 예를 들어서 웹 계층이 없이 단순히 메인 메서드를 통해서 콘솔에서만 동작하는 애플리케이션을 추가로 개발해야 해도 대부분의 서비스 로직을 재사용할 수 있다면 좋겠지요?

반대로 이야기하면 웹 계층을 위한 폼 데이터를 처리하고, 화면에 뿌릴 데이터를 모아서 넘겨주고 이런 웹 계층 관련 일들은 모두 컨트롤러에서 담당해야겠지요?

이런 관점을 접근하시면 어느정도 고민을 덜 수 있을 거에요.
```

---

[Controller와 Service 로직 구분에 대한 질문과 답변 2](https://www.inflearn.com/questions/224823/)

안녕하세요 강사님! 항상 강의 잘보고 있습니다.

정확히 강의 관련된 질문은 아니지만 헷갈리는 부분이 있어서요.

spring에서 말하는 model이라는 개념이 domain, repository 등등을 포함하여 말하는 것인지 

아니면 controller에서 view에 값을 넘겨줄 때 쓰는 Model 클래스를 말하는 것인지 헷갈립니다!

찾아보니 블로그마다  다르게 명시가 되어있더라구요

java beans들을 model이라고 하는 사람도 있던데 뭐가 맞는것일까요??.. 

```bash
안녕하세요. 효진님

모델이라는 의미는 문맥에 따라서 다르게 사용됩니다.

mvc에서는 controller에서 view에 값을 넘겨줄 때 쓰는 Model 클래스를 말하고,

설계에서는 domain 모델등을 뜻합니다.

감사합니다.
```

---

setMember() 내부에서 member.getOrders().add(this)를 해주는 이유

```bash
Member에서 본인이 주문한 Order에 대해 접근하려면 Order에 대한 참조를 가지고 있어야 합니다. 그 참조가 Member의 orders입니다.

반대로 Order에서 주문자를 찾아가려면 Member에 대한 참조가 있어야 합니다. 그 참조가 Order의 member입니다.

DB상에서는 Order의 foreign key인 Member_id로 member를 찾아갈 수 있지만

객체상에서는 Member에서 order를 찾아갈 방법이 없습니다.

따라서 주문할 때 Member에는 order를, Order에는 member를 넣어줘서 서로 참조할 수 있게 하는 것입니다.

 

값이 들어가는 것은

1. DB상으로는 Order의 외래키로 Member_Id가 저장됩니다.

2. 객체상으로는 Member의 orders에 order가 추가되고, Order의 member에 member가 지정됩니다.
```

---

왜 JPA의 Entity는 기본 생성자를 가져야 하는가요?

https://hyeonic.tistory.com/191

```bash
정리하면 JPA의 구현체인 hibernate에서 제공하는 다양한 기능을 활용하기 위해서는 public이나 protected 기본 생성자가 필요하다. private로 생성자를 만들게 되면 이러한 기능들을 사용하는데 제약이 되기 때문이다. 

다만 안정성 측면에서 좀 더 작은 scope 가진 protected 기본 생성자를 주로 사용해야 겠다. 더 나아가 혼자가 아닌 다른 팀원들과 협업을 진행하게 된다면 객체 생성을 어떤 식으로 해결해야 할지 고민하고 관련 내용들을 공유하여 무분별한 생성을 막아야 겠다는 생각이 들었다.
```

https://flashy-toucan-1ed.notion.site/Builder-7b7077515d674958b76076485dc0117d

```bash
객체에 값을 초기화 하는 방법은 3가지 정도가 있습니다.  수정자를 이용하는것과, 생성자를 이용하는 것, 그리고 빌더 패턴을 이용하는 방법입니다. (객체를 생성하면 프로퍼티는 각각 0에 대응하는 값 또는 null로 초기화 됩니다. 이런 초기화는 논의에서 제외합니다)

- 프로퍼티는 불변으로 사용하는것이 좋습니다.
- 프로퍼티가 3개 이하면 생성자 초기화를 합니다.
- 프로퍼티가 4개 이상이면 빌더 패턴을 고려합니다.
```

---

 @AllArgsConstructor, @RequiredArgsConstructor 사용금지를 권하는 글을 보았습니다.

```bash
안녕하세요. edu님

@AllArgsConstructor, @RequiredArgsConstructor 뿐만 아니라 모든 롬백 에노테이션들은 이런 문제 때문에 잘 알고 사용해야합니다.

저의 경우 @AllArgsConstructor는 사용하지 않고, @RequiredArgsConstructor는 사용합니다.

추가 질문은 다음을 참고해주세요.
```

---

먼저 좋은 강의를 제공해주시것에 감사인사드립니다!

강의에서는 생성자에 @Qualifier를 주입하셨는데

![img](https://cdn.inflearn.com/public/files/posts/6ede2893-c53f-4c65-ab47-3cdafc16caa3/blob)

강의를 듣고난후 @RequireArgsConstructor와 

@Qualifier를 같이 사용했을 경우 

NoUniqueBeanDefinitionException 에러가 발생하였습니다.

@Qualifier를 사용할경우에는 @RequiredArgsConstructor를 사용할수 없는것인지 아님 다른 방법이 있는지 궁금합니다!

```bash
안녕하세요. vkdlxj3562님^^

먼저 lombok이 제공하는 @RequiredArgsConstructor는 애노테이션 까지 함께 포함해서 생성자를 만들지는 않습니다.

그런데 가능한 방법이 있습니다.

1. src/main/java/lombok.config 파일을 만들어주세요.(resources가 아닙니다. src/main/java입니다!)

2. lombok.config에 다음 내용을 넣어주세요.

lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier

3. 프로젝트를 다시 컴파일 한 다음 실행해주세요. IntelliJ를 사용하면, out이라는 폴더가 있는데 이 폴더를 꼭! 모두 지우고 다시 실행해주세요. gradle은 gradlew clean을 한번 해주고 실행해주세요^^!

해당 옵션을 적용한 후에 빌드된 .class 파일을 확인해보면 다음과 같이 @Qulifier가 포함 된 것을 확인할 수 있습니다.
```

```java
public OrderService(@Qualifier("mainDiscountPolicy") DiscountPolicy mainDiscountPolicy, @Qualifier("orderRepository") OrderRepository orderRepository) {
    this.mainDiscountPolicy = mainDiscountPolicy;
    this.orderRepository = orderRepository;
}

도움이 되셨길 바래요^^
```

---

안녕하세요 진짜 좋은 강의와 선생님의 답변으로 많이 배우고 있습니다. 강의를 따라 하다보니 몇가지 궁금증이 생겼습니다.

1.  controller에서 responseEntity 를 안쓰시던데 딱히 이유가 있을까요?
2.  2.계층끼리의 의존성을 낮쳐주기 위해서 dto를 저는 계층끼리 통신할때 쓴다고 알고있었고 entity 가 business layer인 서비스 계층을 벗어나면 좋지않다고 알고있었습니다. 그래서 service에서 controller 로 넘겨줄때 항상 entity를 dto로 만들어서 넘겨주는 방식으로 사용했습니다. 하지만 선생님은 service 계층에서는 controller에 값을 넘겨주지 않거나 id 정도만 넘겨주고 controller 에서 다시조회 하던지 해서 그값으로 resposne dto를 만들어주는 방식으로 하셨습니다. 제가 기존에 하던 방식은 잘못된건가요? 
3.  OrderSimpleApiController 같은경우는 orderSerivce를 전혀 사용하지 않았더라구요 대부분의 조회가 바로 OrderRepository에서 가능한 부분이었지만 이렇게 바로 가능하면 서비스 계층을 안통하고 하는방식이 나은건가요? 혹시 언제는 단순 위임이라도 서비스 계층을 통해 repository를 사용하는게 좋고 언제는 바로 조회하는게 좋은지 궁금합니다. 

https://www.inflearn.com/questions/30618

---

비즈니스 로직을 엔티티에 위임하는 기준이 궁금합니다!

```bash
도메인이 비즈니스 로직의 주도권을 가지고 개발하는 것을 도메인 주도 설계라 합니다. 이렇게 해두면 서비스의 많은 로직이 엔티티로 이동하고, 서비스는 엔티티를 호출하는 정도의 얇은 비즈니스 로직을 가지게 됩니다.

이렇게 하면 information expert pattern을 지키면서 개발할 수 있습니다.

(information expert pattern는 검색해보시면 도움이 되실거에요^^)

반대로 엔티티는 단순히 getter, setter만 제공하고, 서비스에 비즈니스 로직이 모두 있어도 됩니다.

이렇게 되면 서비스 로직이 커지고, 엔티티는 단순히 데이터를 전달하는 역할만 담당하게 됩니다.

전자는 엔티티를 객체로 사용하는 것이고, 후자는 엔티티를 자료 구조로 사용하는 방식이지요.

그러면 항상 전자가 정답인가? 라고 하면 그렇지는 않습니다. 둘다 장단점이 있기 때문에, 상황에 맞는 적절한 방법을 선택하는 것이 중요합니다.

관련해서 클린코드 6. 객체와 자료구조에 둘의 차이가 자세히 설명되어 있으니 한번 읽어보시길 추천드립니다.

감사합니다.
```

https://www.inflearn.com/questions/117315

---

엔티티 클래스가 역할과 책임을 가지고 주도적으로 일을 하는 클래스가 되어야 할까요

아니면 그저 DTO 같은 데이터 저장소로써 역할만 하는것이 나은지 궁금합니다!

```bash
안녕하세요. jungjin님

엔티티를 데이터 저장소로 사용하고 서비스 계층에서 대부분의 비즈니스 로직을 작성하는 것을 트랜잭션 스크립트 패턴이라 하고,

엔티티에 주도적으로 핵심 비즈니스 로직을 가지고 오는 것을 도메인 모델 패턴이라 합니다.

일반적으로 도메인 주도 설계 방식으로 가져가면 많은 부분을 도메인에 핵심 비즈니스 로직을 녹일 수 있어서 도메인 모델 패턴이 가능합니다.

두 패턴 모두 장단점이 있기 때문에, 한 프로젝트 안에서도 상황에 따라 적절하게 둘을 섞어 사용하는 것이 좋습니다.

참고로 다음 강좌인 활용편에서는 도메인 모델 패턴을 사용해서, 엔티티가 역할과 책임을 가지고 핵심 비즈니스 로직을 어떻게 풀어가는지 코드로 보여드릴 예정입니다^^

감사합니다.
```

https://www.inflearn.com/questions/13362

---

안녕하세요! 강의 잘 듣고 있습니다~

주문 취소와 주문 가격 조회 비즈니스 메서드를 엔티티 내부에서 선언하신 이유가 궁금합니다!

오더와 달리 멤버의 경우 엔티티에 비즈니스 로직을 작성하지 않은 건 리포지토리에서 사용한 save, find 외에 별다른 로직이 없기 때문인가요?

서비스가 아닌 엔티티에 비즈니스 로직을 위임하는 기준이 궁금합니다!

```bash
안녕하세요. 역삼님

객체지향 디자인 방법중에 GRASP라는 것이 있는데요.

https://en.wikipedia.org/wiki/GRASP_(object-oriented_design)

여기에 보면 information expert라는 것이 있습니다.

쉽게 이야기해서 정보를 가장 잘 알고 있는 곳에 로직(메서드)가 있어야 한다는 것이지요.

여기서 Order는 해당 필드 정보를 가장 잘 알기 때문에 Order가 스스로 해결할 수 있는 부분은 해결하도록 설계되어 있습니다.

반면에 회원의 경우 회원 엔티티가 필드 정보를 가지고 스스로 처리할 비즈니스 로직이 거의 없습니다.

도움이 되셨길 바래요.
```

https://www.inflearn.com/questions/146139

---

JPA 기본편 강의와 같이 듣고 있습니다.

좋은 강의 항상 감사드립니다.

(질문 도중 제가 잘못 이해 하고 있는 부분이 있다면 말씀주시기 바랍니다.)

다름 아니라 EntityManager는 요청이 들어올 때 생성되어 사용됬다가

해당 요청이 마무리 되면 삭제된다고 이해 하고 있습니다.

근데 @Repository를 DI를 통해 생성할 때 @RequiredArgsConstructor

를 통해 EntityManager도 생성해줍니다.

그럼 이 EntityManager 인스턴스는 @RequiredArgsConstructor

위 어노테이션으로 인해 필요할 때 마다 그 때 그 때 생성된다고 볼 수 있을까요? 

아니면 싱글톤 객체 안에 필드임으로 그 때 그 때 새로운 값을 할당 받는건가요?

감사합니다, 좋은 하루 되세요~

```bash
안녕하세요. 승범님

먼저 @RequiredArgsConstructor는 단순하게 생성자를 자동으로 만들어주는 롬복 애노테이션입니다.

스프링은 생성자가 하나이면 자동으로 @Autowired가 적용됩니다.

따라서 EntityManager가 생성자를 통해서 주입되고, 최종적으로 다음 필드에 담겨집니다.

private final EntityManager em;

결과적으로 주입된 EntityManager는 싱글톤이 맞습니다.

여기에서 싱글톤이니 동시성 문제가 될 수 있지요.

스프링 프레임워크는 여기에 실제 EntityManager를 주입하는 것이 아니라, 사실은 실제 EntityManager를 연결해주는 가짜 EntityManager를 주입해둡니다.

그리고 이 EntityManager를 호출하면, 현재 데이터베이스 트랜잭션과 관련된 실제 EntityManager를 호출해줍니다.

덕분에 개발자는 동시성 이슈에 대한 고민없이, 쉽게 개발할 수 있습니다.

관련해서 JPA 책 13.1 트랜잭션 범위의 영속성 컨텍스트를 참고해보시면 더 자세한 내용을 이해하실 수 있습니다.

추가로 생성자 하나, @RequiredArgsConstructor 주입 부분은 스프링 핵심 원리 - 기본편에서 자세히 설명드립니다^^

감사합니다.
```

https://www.inflearn.com/questions/158967

---

@GeneratedValue의 키생성 전략에 대한 질답

https://www.inflearn.com/questions/23993

---