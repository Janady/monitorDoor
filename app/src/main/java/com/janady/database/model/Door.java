package com.janady.database.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

@Table("door")
public class Door {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;
    @Unique
    public String name;
    @Mapping(Relation.ManyToMany)
    public ArrayList<Camera> cameraList;

    @Mapping(Relation.ManyToOne)
    public Remote remote;
    @Mapping(Relation.OneToOne)
    public Bluetooth bluetooth;
}
