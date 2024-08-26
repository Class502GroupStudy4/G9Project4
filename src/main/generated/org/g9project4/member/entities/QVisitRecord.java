package org.g9project4.member.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVisitRecord is a Querydsl query type for VisitRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVisitRecord extends EntityPathBase<VisitRecord> {

    private static final long serialVersionUID = 2125274188L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVisitRecord visitRecord = new QVisitRecord("visitRecord");

    public final QVisitRecordsId id;

    public final ListPath<String, StringPath> keywords = this.<String, StringPath>createList("keywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final QMember member;

    public final org.g9project4.publicData.tour.entities.QTourPlace tourPlace;

    public final NumberPath<Integer> visitCount = createNumber("visitCount", Integer.class);

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QVisitRecord(String variable) {
        this(VisitRecord.class, forVariable(variable), INITS);
    }

    public QVisitRecord(Path<? extends VisitRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVisitRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVisitRecord(PathMetadata metadata, PathInits inits) {
        this(VisitRecord.class, metadata, inits);
    }

    public QVisitRecord(Class<? extends VisitRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QVisitRecordsId(forProperty("id")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.tourPlace = inits.isInitialized("tourPlace") ? new org.g9project4.publicData.tour.entities.QTourPlace(forProperty("tourPlace")) : null;
    }

}

