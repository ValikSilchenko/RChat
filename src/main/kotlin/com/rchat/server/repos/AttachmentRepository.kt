package com.rchat.server.repos

import com.rchat.server.models.Attachment
import com.rchat.server.models.PersonalMessage
import org.springframework.data.jpa.repository.JpaRepository

interface AttachmentRepository : JpaRepository<Attachment, Int> {
    fun getAttachmentsByMessage(message: PersonalMessage)
}