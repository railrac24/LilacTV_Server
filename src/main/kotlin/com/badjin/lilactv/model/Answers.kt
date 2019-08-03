package com.badjin.lilactv.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
data class Answers (
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "replier_id")
        @JsonProperty
        var replier: Users,
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "question_id")
        @JsonProperty
        var questions: Questions,
        @JsonProperty
        var content: String,
        var createDate: LocalDateTime,
        @JsonProperty
        @Id @GeneratedValue var id: Long? = null
) {
    constructor(writer: Users, questions: Questions, content: String): this(writer, questions, content, LocalDateTime.now())
    fun getFormattedCreateDate(): String {
        return createDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
    }

    fun updateContent(title: String, content: String) {
        this.content = content
    }
}