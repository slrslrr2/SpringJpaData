package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public void changeTeam(Team team) {
        this.team = team;
        Member member = this;
        List<Member> members = team.getMembers();
        members.add(member);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        this.team = team;

        changeTeam(team);
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
