package com.rchat.server.repos

import com.rchat.server.models.Attachment
import org.springframework.data.jpa.repository.JpaRepository

interface AttachmentRepository : JpaRepository<Attachment, Int> {
}