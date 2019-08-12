package com.badjin.lilactv.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Subscription (
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "lilac_tv_id")
        var lilacTvId: Items?,
        var startDate: LocalDateTime,
        var endDate: LocalDateTime,
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "status_id")
        var status: Status,
        @Id @GeneratedValue var id: Long? = null
)

@Entity
data class Status (
        var state: String,
        @Id @GeneratedValue var id: Long? = null
)