package com.rchat.server.repos;

import com.rchat.server.models.Channel
import com.rchat.server.models.Member
import com.rchat.server.models.MemberId
import com.rchat.server.models.Users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, MemberId> {
    @Query("select max(mbr.participatingNum) from Member mbr where mbr.channel = :channel")
    fun getMaxParticipatingNum(channel: Channel): Int

    @Query("select mbr.user from Member mbr where mbr.channel = :channel" +
            " and mbr.participatingNum = (select min(mbr.participatingNum)" +
            " from mbr where mbr.channel = :channel)")
    fun getFirstParticipated(channel: Channel): Users?
}