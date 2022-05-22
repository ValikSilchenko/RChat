package com.rchat.server.repos;

import com.rchat.server.models.Member
import com.rchat.server.models.MemberId

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, MemberId> {
}