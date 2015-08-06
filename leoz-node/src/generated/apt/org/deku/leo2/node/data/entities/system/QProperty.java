package org.deku.leo2.node.data.entities.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QProperty is a Querydsl query type for Property
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProperty extends EntityPathBase<Property> {

    private static final long serialVersionUID = 2100118872L;

    public static final QProperty property = new QProperty("property");

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> station = createNumber("station", Integer.class);

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final StringPath value = createString("value");

    public QProperty(String variable) {
        super(Property.class, forVariable(variable));
    }

    public QProperty(Path<? extends Property> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProperty(PathMetadata<?> metadata) {
        super(Property.class, metadata);
    }

}

