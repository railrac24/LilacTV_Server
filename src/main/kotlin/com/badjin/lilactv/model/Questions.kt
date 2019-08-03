package com.badjin.lilactv.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
data class Questions (
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "writer_id")
        @JsonProperty
        var writer: Users,
        @JsonProperty
        var title: String,
        @JsonProperty
        var content: String,
        var createDate: LocalDateTime,
        @OneToMany(mappedBy = "questions", cascade = [CascadeType.ALL])
        @OrderBy("id DESC")
        @JsonProperty
        var answers: MutableList<Answers>?,
        @JsonProperty
        @Id @GeneratedValue var id: Long? = null
) {
    constructor(writer: Users, title: String,content: String): this(writer, title, content, LocalDateTime.now(), null)
    fun getFormattedCreateDate(): String {
        return createDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
    }

    fun updateContent(title: String, content: String) {
        this.title = title
        this.content = content
    }
}