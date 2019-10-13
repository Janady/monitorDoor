package com.janady.database.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

@Table("bluetooth")
public class Bluetooth {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;

    private String mac;
    private String uuid;
    private String serviceUuid;
    private String writeUuid;
    private String notifyUuid;
    private String password;
    private boolean isFirst = true;
    public String name;
    @Mapping(Relation.OneToOne)
    public Door door;
}
