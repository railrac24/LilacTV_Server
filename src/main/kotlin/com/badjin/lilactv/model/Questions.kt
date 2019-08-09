package com.badjin.lilactv.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
data class Questions (
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "writer_id")
        var writer: Users,
        var title: String,
        var content: String,
        var createDate: LocalDateTime,
        var countOfAnswers: Int,
        @JsonIgnore
        @OneToMany(mappedBy = "questions", cascade = [CascadeType.ALL])
        @OrderBy("id DESC")
        var answers: MutableList<Answers>?,
        @Id @GeneratedValue var id: Long? = null
) {
    constructor(writer: Users, title: String,content: String): this(writer, title, content, LocalDateTime.now(), 0,null)
    fun getFormattedCreateDate(): String {
        return createDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
    }

    fun updateContent(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun addAnswer() {
        this.countOfAnswers += 1
    }

    fun deleteAnswer() {
        this.countOfAnswers -= 1
    }
}