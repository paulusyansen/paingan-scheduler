package org.paingan.scheduler.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.paingan.scheduler.model.Member;
import org.paingan.scheduler.repository.MemberRepository;
import org.paingan.scheduler.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {
	
	@Resource
	private MemberRepository employeeRepository;

	@Override
	@Transactional(readOnly=false)
	public void save(Member e) {
		this.employeeRepository.save(e);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Member> findAll() {
		return this.employeeRepository.findAll();
	}

}
