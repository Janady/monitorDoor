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

    public String mac;
    public String uuid;
    public String serviceUuid;
    public String writeUuid;
    public String notifyUuid;
    public String password;
    public boolean isFirst = true;
    public String name;
    public String sceneName;

    @Mapping(Relation.OneToOne)
    public Door door;
}
