package com.rchat.server.services


data class EmailContext(val from: String, val to: String, val subject: String, val body: String)