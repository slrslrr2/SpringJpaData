package study.datajpa;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

@Controller
public class TestController {

    @PersistenceContext
    EntityManager em;

    @RequestMapping("/test")
    @Transactional
    public void test(){
//        Team team = Team.builder().name("develop").build();
        Team team2 = new Team("team3");

//        System.out.println(team.getMembers()); // NullPointException 발생,
                                                // Builder패턴의 경우 초기화를 안해줌!
        System.out.println(team2.getMembers());
    }
}
