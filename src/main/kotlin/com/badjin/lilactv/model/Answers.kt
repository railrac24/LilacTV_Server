package com.badjin.lilactv.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
data class Answers (
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "replier_id")
        var replier: Users,
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "question_id")
        var questions: Questions,
        var content: String,
        var createDate: LocalDateTime,
        @Id @GeneratedValue var id: Long? = null
) {
    constructor(writer: Users, questions: Questions, content: String): this(writer, questions, content, LocalDateTime.now())
    fun getFormattedCreateDate(): String {
        return createDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
    }

    fun updateContent(content: String) {
        this.content = content
    }
}