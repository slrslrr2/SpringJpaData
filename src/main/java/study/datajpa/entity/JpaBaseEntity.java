package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {
    @Column(updatable = false)
    private String creatAt;
    private LocalDateTime createdDate;
    private String updateAt;
    private LocalDateTime updatedDate;

    @PrePersist //저장 전 발생
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate //수정 전 발생
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}