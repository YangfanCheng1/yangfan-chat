package com.yangfan.chat.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("private")
public class PrivateRoom extends Room {

    public PrivateRoom(int id) {
        super(id);
    }

    @OneToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @OneToOne
    @JoinColumn(name = "user2_id")
    private User user2;

}
