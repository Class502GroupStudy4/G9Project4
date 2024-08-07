package org.g9project4.file.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileInfo is a Querydsl query type for FileInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileInfo extends EntityPathBase<FileInfo> {

    private static final long serialVersionUID = -1375664228L;

    public static final QFileInfo fileInfo = new QFileInfo("fileInfo");

    public final org.g9project4.global.entities.QBaseMemberEntity _super = new org.g9project4.global.entities.QBaseMemberEntity(this);

    public final StringPath contentType = createString("contentType");

    //inherited
    public final StringPath createBy = _super.createBy;

    public final BooleanPath done = createBoolean("done");

    public final StringPath extenstion = createString("extenstion");

    public final StringPath fileName = createString("fileName");

    public final StringPath gid = createString("gid");

    public final StringPath location = createString("location");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public QFileInfo(String variable) {
        super(FileInfo.class, forVariable(variable));
    }

    public QFileInfo(Path<? extends FileInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileInfo(PathMetadata metadata) {
        super(FileInfo.class, metadata);
    }

}

