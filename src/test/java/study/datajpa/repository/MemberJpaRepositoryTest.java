package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    TeamJpaRepository teamJpaRepository;

    @Test
    public void member(){
        Member member = new Member("member", 1, new Team("team"));
        Member saveMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        assertThat(findMember).isEqualTo(member); // JPA에서는 같은 Transaction안에 같은 인스턴스임을 보장
    }

    @Test
    public void basicCRUD() {
        Team team = new Team("team");
        Team team2 = new Team("team2");
        teamJpaRepository.save(team);
        teamJpaRepository.save(team2);

        Member member = new Member("member", 1, team);
        Member member2 = new Member("member2", 1, team);
        Member member3 = new Member("member3", 1, team);
        Member member4 = new Member("member4", 1, team);

        memberJpaRepository.save(member);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);

        memberJpaRepository.delete(member);

        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(member2).isEqualTo(findMember2);

        findMember2.changeTeam(team2);

        List<Member> all = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();
        assertThat(all.size()).isEqualTo(count);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        setMemberData();

        List<Member> findMembers = memberJpaRepository.findByUsernameAndAgeGreaterThen("member", 10);
        System.out.println(findMembers.size());
        for (Member findMember : findMembers) {
            System.out.println(findMember);
        }

        assertThat(findMembers.size()).isEqualTo(3);
    }

    @Test
    public void paging() {
        setMemberData();

        int age = 10;
        int offset = 1; //1번째부터
        int limit = 2; // 2개를 가지고 오겠다.

        // select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_
        // from member member0_ where member0_.age>10
        // order by member0_.username desc limit 1, 2;
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(2);
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    public void bulkUpdate(){
        setMemberData();
        int resultCount = memberJpaRepository.bulkAgePlus(5);
        assertThat(resultCount).isEqualTo(4);
    }

    private void setMemberData() {
        Team team = new Team("team");
        teamJpaRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member", 11, team);
        Member member3 = new Member("member", 12, team);
        Member member4 = new Member("member", 13, team);

        memberJpaRepository.save(member);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
    }


}