package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity(){
        Team team = new Team("team");
        Team team2 = new Team("team2");
        em.persist(team);
        em.persist(team2);

        Member member = new Member("member", 1, team);
        Member member2 = new Member("member2", 2, team);

        em.persist(member);
        em.persist(member2);

        member.changeTeam(team2);
        em.persist(member);



        List<Member> members = member.getTeam().getMembers();
        for (Member member1 : members) {
//            System.out.println(member1);
        }

        List<Member> members2 = member2.getTeam().getMembers();
        for (Member member1 : members2) {
//            System.out.println(member1);
        }

        em.flush();
        em.clear(); // 영속성 컨텍스트를 초기화

        List<Member> query = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
        for (Member member1 : query) {
            System.out.println(member1.toString());
            System.out.println(member1.getTeam());
             System.out.println(member1.getTeam().getMembers().size());
        }

    }
}