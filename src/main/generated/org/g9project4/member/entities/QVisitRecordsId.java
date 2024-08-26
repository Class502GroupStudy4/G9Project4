package org.g9project4.member.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVisitRecordsId is a Querydsl query type for VisitRecordsId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QVisitRecordsId extends BeanPath<VisitRecordsId> {

    private static final long serialVersionUID = 1930537250L;

    public static final QVisitRecordsId visitRecordsId = new QVisitRecordsId("visitRecordsId");

    public final NumberPath<Long> contentId = createNumber("contentId", Long.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QVisitRecordsId(String variable) {
        super(VisitRecordsId.class, forVariable(variable));
    }

    public QVisitRecordsId(Path<? extends VisitRecordsId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVisitRecordsId(PathMetadata metadata) {
        super(VisitRecordsId.class, metadata);
    }

}

