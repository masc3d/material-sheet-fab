package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QRoute is a Querydsl query type for Route
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRoute extends EntityPathBase<Route> {

    private static final long serialVersionUID = -1893353575L;

    public static final QRoute route = new QRoute("route");

    public final StringPath area = createString("area");

    public final StringPath country = createString("country");

    public final TimePath<java.sql.Time> etod = createTime("etod", java.sql.Time.class);

    public final StringPath holidayCtrl = createString("holidayCtrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> island = createNumber("island", Integer.class);

    public final NumberPath<Integer> layer = createNumber("layer", Integer.class);

    public final TimePath<java.sql.Time> ltodholiday = createTime("ltodholiday", java.sql.Time.class);

    public final TimePath<java.sql.Time> ltodsa = createTime("ltodsa", java.sql.Time.class);

    public final TimePath<java.sql.Time> ltop = createTime("ltop", java.sql.Time.class);

    public final NumberPath<Integer> saturdayOK = createNumber("saturdayOK", Integer.class);

    public final NumberPath<Integer> station = createNumber("station", Integer.class);

    public final NumberPath<Integer> term = createNumber("term", Integer.class);

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final NumberPath<Integer> validCRTR = createNumber("validCRTR", Integer.class);

    public final DateTimePath<java.sql.Timestamp> validFrom = createDateTime("validFrom", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> validTo = createDateTime("validTo", java.sql.Timestamp.class);

    public final StringPath zipFrom = createString("zipFrom");

    public final StringPath zipTo = createString("zipTo");

    public QRoute(String variable) {
        super(Route.class, forVariable(variable));
    }

    public QRoute(Path<? extends Route> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoute(PathMetadata<?> metadata) {
        super(Route.class, metadata);
    }

}

