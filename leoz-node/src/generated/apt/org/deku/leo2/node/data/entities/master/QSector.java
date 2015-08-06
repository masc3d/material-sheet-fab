package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSector is a Querydsl query type for Sector
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSector extends EntityPathBase<Sector> {

    private static final long serialVersionUID = 1454439446L;

    public static final QSector sector = new QSector("sector");

    public final StringPath sectorFrom = createString("sectorFrom");

    public final StringPath sectorTo = createString("sectorTo");

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> validFrom = createDateTime("validFrom", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> validTo = createDateTime("validTo", java.sql.Timestamp.class);

    public final StringPath via = createString("via");

    public QSector(String variable) {
        super(Sector.class, forVariable(variable));
    }

    public QSector(Path<? extends Sector> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSector(PathMetadata<?> metadata) {
        super(Sector.class, metadata);
    }

}

