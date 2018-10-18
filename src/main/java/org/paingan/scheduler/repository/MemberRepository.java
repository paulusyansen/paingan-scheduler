package org.paingan.scheduler.repository;

import org.paingan.scheduler.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
