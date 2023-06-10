package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

// QueryDSL 사용시 다음과 같이 커스텀 하여
// MemberRepositoryCustom 상속하여 사용
// 해당 구현체는 ~Impl을 꼭 사용
// 하지만 너무 복잡한건 그냥 @Repository를 따로 만드는것이 좋다
//  1. 복잡한 Select 쿼리,
//  2.CRUD Repository implements JpaRepository, CumtomRepository
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
