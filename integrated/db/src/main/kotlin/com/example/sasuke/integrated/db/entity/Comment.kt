package com.example.sasuke.integrated.db.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(value = [AuditingEntityListener::class])
@Table(name = "comment")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,
    @Column(nullable = false, length = 200)
    var content: String = "",

    @Column(nullable = false)
    val authorId: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post? = null,

    @Column
    val depth: Int? = 0,

    @OneToOne
    @JoinColumn(name = "comment_id")
    val comment: Comment? = null,

    @Column
    var deletedAt: LocalDateTime? = null,
): BaseTime() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Comment

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Comment(id='$id', content='$content', authorId='$authorId', deletedAt='$deletedAt'"
    }
}
