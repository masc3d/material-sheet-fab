package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QStationSector is a Querydsl query type for StationSector
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStationSector extends EntityPathBase<StationSector> {

    private static final long serialVersionUID = 1065632522L;

    public static final QStationSector stationSector = new QStationSector("stationSector");

    public final NumberPath<Integer> routingLayer = createNumber("routingLayer", Integer.class);

    public final StringPath sector = createString("sector");

    public final NumberPath<Integer> stationNr = createNumber("stationNr", Integer.class);

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public QStationSector(String variable) {
        super(StationSector.class, forVariable(variable));
    }

    public QStationSector(Path<? extends StationSector> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStationSector(PathMetadata<?> metadata) {
        super(StationSector.class, metadata);
    }

}

