package com.example.sasuke.integrated.db.entity

import com.example.sasuke.integrated.db.model.User
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(value = [AuditingEntityListener::class])
@Table(name = "heart")
data class Heart (
    @Id
    @GeneratedValue
    val id: Long = 0L,

    @Column
    val userId: Long? = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    val post: Post? = null
): BaseTime() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Heart

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Heart(id=$id, userId='$userId', createdAt='$createdAt', modifiedAt='$modifiedAt')"
    }
}