package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QRoutingLayer is a Querydsl query type for RoutingLayer
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRoutingLayer extends EntityPathBase<RoutingLayer> {

    private static final long serialVersionUID = 678719707L;

    public static final QRoutingLayer routingLayer = new QRoutingLayer("routingLayer");

    public final StringPath description = createString("description");

    public final NumberPath<Integer> layer = createNumber("layer", Integer.class);

    public final NumberPath<Integer> services = createNumber("services", Integer.class);

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public QRoutingLayer(String variable) {
        super(RoutingLayer.class, forVariable(variable));
    }

    public QRoutingLayer(Path<? extends RoutingLayer> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoutingLayer(PathMetadata<?> metadata) {
        super(RoutingLayer.class, metadata);
    }

}

