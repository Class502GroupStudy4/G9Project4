package org.g9project4.publicData.tour.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlaceDetail is a Querydsl query type for PlaceDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaceDetail extends EntityPathBase<PlaceDetail> {

    private static final long serialVersionUID = -1823023587L;

    public static final QPlaceDetail placeDetail = new QPlaceDetail("placeDetail");

    public final NumberPath<Long> contentId = createNumber("contentId", Long.class);

    public final StringPath contentTypeId = createString("contentTypeId");

    public final StringPath title = createString("title");

    public QPlaceDetail(String variable) {
        super(PlaceDetail.class, forVariable(variable));
    }

    public QPlaceDetail(Path<? extends PlaceDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlaceDetail(PathMetadata metadata) {
        super(PlaceDetail.class, metadata);
    }

}

