package org.paingan.scheduler.service;

import java.util.List;

import org.paingan.scheduler.model.Member;

public interface MemberService {
	
	void save(Member e);
	
	List<Member> findAll();

}
