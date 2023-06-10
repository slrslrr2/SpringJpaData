package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{

    List<Member> findByUsername(String username);

    /**
         select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_
         from member member0_
         where member0_.username='member'
            and member0_.age>10;

        // 2개 query 넘어가면 JPQL 사용
        ==> 가독성 너무 안좋아서
   서 */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 동적쿼리는 QueryDSL 사용해야함 ㅎㅎ,, 다음강의!
    @Query("select m from Member m where m.username = :username and m.age >= :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findMember();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m.username from Member m where m.username in :names")
    List<String> findMemberByUsernames(@Param("names") List<String> names);

    List<Member> findCollectionByUsername(String name); //컬렉션

    Member findMemberByUsername(String name); //단건

    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    Page<Member> findPageByAgeGreaterThan(int age, Pageable pageable);

    Slice<Member> findSliceByAgeGreaterThan(int age, Pageable pageable);

   //TotalCount쿼리의 경우 기준으로 innerjoin 해오기에 성능상 Total카운트 쿼리를 분리 작성한다.
    @Query(value = "select m from Member m left join m.team",
            countQuery = "select m from Member m"
    )
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findFetchMember();

    @Query("select m from Member m left join fetch m.team")
    List<Member> findLeftMember();

    @EntityGraph(attributePaths = {"team"}) // JPQL 없이도 fetch join 해준다
    List<Member> findAll(); // select member0_.member_id as member_i1_0_0_, team1_.team_id as team_id1_1_1_, member0_.age as age2_0_0_, member0_.team_id as team_id4_0_0_, member0_.username as username3_0_0_, team1_.name as name2_1_1_
                            // from member member0_ left outer join team team1_
                            // on member0_.team_id=team1_.team_id;


    @EntityGraph(attributePaths = {"team"}) // JPQL 안넣어도 fetch join 해준다
    @Query("select m from Member m")
    List<Member> findMemberEntityGreph(); //select member0_.member_id as member_i1_0_0_, team1_.team_id as team_id1_1_1_, member0_.age as age2_0_0_, member0_.team_id as team_id4_0_0_, member0_.username as username3_0_0_, team1_.name as name2_1_1_
                                          // from member member0_ left outer join team team1_
                                          // on member0_.team_id=team1_.team_id;


    @EntityGraph(attributePaths = {"team"}) // JPQL 안넣어도 fetch join 해준다
    List<Member> findGraphByUsername(String username); // select member0_.member_id as member_i1_0_0_, team1_.team_id as team_id1_1_1_, member0_.age as age2_0_0_, member0_.team_id as team_id4_0_0_, member0_.username as username3_0_0_, team1_.name as name2_1_1_
                                                        // from member member0_ left outer join team team1_
                                                        // on member0_.team_id=team1_.team_id
                                                        // where member0_.username='gbitkim';


    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Member> findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String userName);
}
