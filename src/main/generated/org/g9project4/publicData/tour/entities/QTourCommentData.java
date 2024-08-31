package org.g9project4.publicData.tour.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTourCommentData is a Querydsl query type for TourCommentData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTourCommentData extends EntityPathBase<TourCommentData> {

    private static final long serialVersionUID = 1050708118L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTourCommentData tourCommentData = new QTourCommentData("tourCommentData");

    public final org.g9project4.global.entities.QBaseEntity _super = new org.g9project4.global.entities.QBaseEntity(this);

    public final StringPath commenter = createString("commenter");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath guestPw = createString("guestPw");

    public final StringPath ip = createString("ip");

    public final org.g9project4.member.entities.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final QTourPlace tourPlace;

    public final StringPath ua = createString("ua");

    public QTourCommentData(String variable) {
        this(TourCommentData.class, forVariable(variable), INITS);
    }

    public QTourCommentData(Path<? extends TourCommentData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTourCommentData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTourCommentData(PathMetadata metadata, PathInits inits) {
        this(TourCommentData.class, metadata, inits);
    }

    public QTourCommentData(Class<? extends TourCommentData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new org.g9project4.member.entities.QMember(forProperty("member")) : null;
        this.tourPlace = inits.isInitialized("tourPlace") ? new QTourPlace(forProperty("tourPlace")) : null;
    }

}

