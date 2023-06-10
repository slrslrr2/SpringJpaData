package study.datajpa.repository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("member", 1, new Team("team"));
        Member saveMember = memberRepository.save(member);
        Optional<Member> findMember = memberRepository.findById(member.getId());

        assertThat(findMember.get()).isEqualTo(member);
        assertThat(findMember.get()).isEqualTo(saveMember);
    }

    @Test
    public void basicCRUD() {
        Team team = new Team("team");
        Team team2 = new Team("team2");
        teamRepository.save(team);
        teamRepository.save(team2);

        Member member = new Member("member", 1, team);
        Member member2 = new Member("member2", 1, team);
        Member member3 = new Member("member3", 1, team);
        Member member4 = new Member("member4", 1, team);

        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        memberRepository.delete(member);

        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(member2).isEqualTo(findMember2);

        findMember2.changeTeam(team2);

        List<Member> all = memberRepository.findAll();
        long count = memberRepository.count();
        assertThat(all.size()).isEqualTo(count);
    }

    @Test
    public void etcMethod(){
        // select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_
        // from member member0_ where member0_.username='member';
        List<Member> member = memberRepository.findByUsername("member");
        System.out.println(member);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Team team = new Team("team");
        teamRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member", 11, team);
        Member member3 = new Member("member", 12, team);
        Member member4 = new Member("member", 13, team);

        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("member", 10);
        System.out.println(findMembers.size());
        for (Member findMember : findMembers) {
            System.out.println(findMember);
        }

        assertThat(findMembers.size()).isEqualTo(3);
    }

    @Test
    public void testQuery() {
        Team team = new Team("team");
        teamRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member", 11, team);

        memberRepository.save(member);
        memberRepository.save(member2);
        List<Member> findMembers = memberRepository.findUser("member", 10);
        assertThat(findMembers.get(0)).isEqualTo(member);
    }

    @Test
    public void testMemberQuery() {
        Team team = new Team("team");
        teamRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member", 11, team);

        memberRepository.save(member);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println(dto);
        }

        List<String> memberByUsernames = memberRepository.findMemberByUsernames(Arrays.asList("member", "member2"));
        for (String username: memberByUsernames) {
            System.out.println(username);
        }
    }

    @Test
    public void variousMemberQuery() {
        Team team = new Team("team");
        teamRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member2", 11, team);

        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> findMember1 = memberRepository.findCollectionByUsername("assadf");
        Member findMember2 = memberRepository.findMemberByUsername("assadf");
        Optional<Member> findMember3 = memberRepository.findOptionalByUsername("assadf");

        System.out.println(findMember1); // []
        System.out.println(findMember2); // null
        System.out.println(findMember3); // Optional.empty
    }

    @Test
    public void dupleMemberQuery() {
        Team team = new Team("team");
        teamRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member", 11, team);

        memberRepository.save(member);
        memberRepository.save(member2);

        // IncorrectResultSizeDataAccessException: query did not return a unique result: 2
        Optional<Member> findMember3 = memberRepository.findOptionalByUsername("member");

        System.out.println(findMember3);
    }

    @Test
    public void paging() {
        setMemberData();

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findPageByAgeGreaterThan(age, pageRequest);

        // 실무에서는 꼭 Dto로 변환하여 전달
        Page<MemberDto> memberDtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));

        assertThat(memberDtoPage.getTotalElements()).isEqualTo(3);   // 자동으로 TotalCount도 가지고 온다.
        assertThat(memberDtoPage.hasNext()).isTrue(); // 다음 페이지 존재하냐
        assertThat(memberDtoPage.getNumber()).isEqualTo(0); // 페이지 번호를 가져옴!
        assertThat(memberDtoPage.isFirst()).isTrue(); // 첫번째 페이지냐
        assertThat(memberDtoPage.getTotalPages()); // 전체 페이지 수
    }

    @Test
    public void slice() {
        setMemberData();

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Slice<Member> slice = memberRepository.findSliceByAgeGreaterThan(age, pageRequest);

        List<Member> members = slice.getContent();

        for (Member member1 : members) {
            System.out.println(member1);
        }

//        assertThat(slice.getTotalElements()).isEqualTo(3);   // 자동으로 TotalCount도 가지고 온다.
        assertThat(slice.hasNext()).isTrue(); // 다음 페이지 존재하냐
        assertThat(slice.getNumber()).isEqualTo(0); // 페이지 번호를 가져옴!
        assertThat(slice.isFirst()).isTrue(); // 첫번째 페이지냐
//        assertThat(slice.getTotalPages()); // 전체 페이지 수

        // slice는 limit +1 하여 미리 다음페이지 첫번째 내용이 있다면 처리 등 해온다
        // getTotalElements, getTotalPages 기능은 없다.
    }

    @Test
    public void bulkUpdate(){
        setMemberData();
        Member member4 = new Member("member5", 10); // 영속성 컨텍스트에 나이가 10인 member5
        memberRepository.save(member4);

        int resultCount = memberRepository.bulkAgePlus(5);

//        em.flush();
//        em.clear();

        List<Member> findByMember5 = memberRepository.findByUsername("member5");
        System.out.println(findByMember5.get(0).getAge());  // 10
                                                            // Bulk하여 나이를 더해주었지만,
                                                            // 영속성컨텍스트에 저장했을때 상태로 데이터가 남아있어
                                                            // 벌크계산 나이는 아직 반영 안되어있다.
                                                            // 그렇기에 em.flush()로 데이터 쌓고
                                                            // em.clear()로 영속성 컨텍스트 초기화한다.
                                                            // 아니면 @Modifying(clearAutomatically = true)
    }

    private void setMemberData() {
        Team team = new Team("team");
        teamRepository.save(team);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member", 11, team);
        Member member3 = new Member("member", 11, team);
        Member member4 = new Member("member", 11, team);

        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }

    @Test
    public void findMemberLazy(){
        Team team = new Team("team");
        Team team2 = new Team("team2");
        teamRepository.save(team);
        teamRepository.save(team2);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member2", 11, team2);

        memberRepository.save(member);
        memberRepository.save(member2);

        em.flush(); // DB반영
        em.clear(); // 영속성컨텍스트 비우기

        List<Member> members = memberRepository.findAll();
        for (Member entity : members) {
            System.out.println(entity.getUsername());
            System.out.println(entity.getTeam()); // class study.datajpa.entity.Team$HibernateProxy$W8LIZTTo
            System.out.println(entity.getTeam().getName()); // 이때 Team 쿼리도 함께 나감 (지연로딩)
                                                            // N+1 (쿼리를 한번 날렸는데 여러번 나감)
                                                            // select team0_.team_id as team_id1_1_0_, team0_.name as name2_1_0_ from team team0_ where team0_.team_id=2;
        }
    }

    @Test
    public void test(){

        Team team = new Team("team");
        Team team2 = new Team("team2");
        teamRepository.save(team);
        teamRepository.save(team2);

        Member member = new Member("member", 10, team);
        Member member2 = new Member("member2", 11, team2);

        memberRepository.save(member);
        memberRepository.save(member2);

        em.flush(); // DB반영
        em.clear(); // 영속성컨텍스트 비우기

        List<Member> members = memberRepository.findFetchMember(); // fetch는 모두 조인하며 모두 컬럼 가져온다
                                                                    // select member0_.member_id as member_i1_0_0_, team1_.team_id as team_id1_1_1_, member0_.age as age2_0_0_, member0_.team_id as team_id4_0_0_, member0_.username as username3_0_0_, team1_.name as name2_1_1_ from member member0_
                                                                     // inner join team team1_ on member0_.team_id=team1_.team_id

//        List<Member> members = memberRepository.findLeftMember(); // left의 경우 join 하지만, 컬럼도 함께 명시해야함
        for (Member member1 : members) {
            System.out.println(member1.getTeam().getId());
        }
    }

    @Test
    public void entityGraphTest(){
        setMemberData();

        memberRepository.findAll();
        memberRepository.findMemberEntityGreph();
        memberRepository.findGraphByUsername("gbitkim");
    }

    @Test
    public void readOnlyTest(){
        Team team = new Team("team");
        Team team2 = new Team("team2");
        teamRepository.save(team);
        teamRepository.save(team2);
        memberRepository.save(new Member("gbitkim", 10, team));

        em.flush();
        em.clear();

        List<Member> gbitkim = memberRepository.findReadOnlyByUsername("gbitkim");
        gbitkim.get(0).changeTeam(team2);

        // team2로 나오지만 update문은 실행되지않는다
       // System.out.println(gbitkim.get(0).getTeam().getName());

    }

    @Test
    public void lockTest(){
        Team team = new Team("team");
        teamRepository.save(team);
        memberRepository.save(new Member("gbitkim", 10, team));

        em.flush();
        em.clear();

        List<Member> gbitkim = memberRepository.findLockByUsername("gbitkim");
    }
}