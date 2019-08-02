package com.badjin.lilactv.model

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
        @Id @GeneratedValue var id: Long? = null
) {
    constructor(writer: Users, title: String,content: String): this(writer, title, content, LocalDateTime.now())
    fun getFormattedCreateDate(): String {
        return createDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
    }

    fun isSameWriter(user: Users): Boolean {
        return (this.writer.id == user.id)
    }

    fun updateContent(title: String, content: String) {
        this.title = title
        this.content = content
    }
}