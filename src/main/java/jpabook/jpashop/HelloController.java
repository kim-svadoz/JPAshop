package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    // Model : Model에다가 실어서 데이터를 뷰로 넘길 수 있는 Model
    // return "hello" : 화면 이름 // 자동으로 hello.html로 붙는다. (관례)
    // 서버사이드 렌더링 작업
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!!!");
        return "hello";
    }

    // 템플릿 엔진을 통해 화면에 내려준다.
    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value = "name") String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }

    // API 방식 1
    // Data 그대로 화면에 내려준다.
    // 쓸 일은 거의 없다.
    @GetMapping("hello-string")
    @ResponseBody
    public String helloString(@RequestParam("name") String name) {
        return "hello " + name; // "hello ${name}"
    }

    // API 방식 2
    // 이게 보통 우리가 말하는 api 방식
    /*
    @ResponseBody를 사용하면
    HTTP의 body에 문자 내용을 직접 반환
    viewResolver 대신 해 HttpMessageConverter가 동작한다.
    */
    // 객체를 전달할 때 사용하는 방식 -> JSON을 이용
    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
