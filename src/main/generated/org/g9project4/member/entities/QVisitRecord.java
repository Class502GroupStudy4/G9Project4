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

    public static final QVisitRecord visitRecord = new QVisitRecord("visitRecord");

    public final StringPath contentId = createString("contentId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> keywords = this.<String, StringPath>createList("keywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<Integer> visitCount = createNumber("visitCount", Integer.class);

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QVisitRecord(String variable) {
        super(VisitRecord.class, forVariable(variable));
    }

    public QVisitRecord(Path<? extends VisitRecord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVisitRecord(PathMetadata metadata) {
        super(VisitRecord.class, metadata);
    }

}

