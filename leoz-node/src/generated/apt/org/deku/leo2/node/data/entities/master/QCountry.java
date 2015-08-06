package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QCountry is a Querydsl query type for Country
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCountry extends EntityPathBase<Country> {

    private static final long serialVersionUID = 1125533926L;

    public static final QCountry country = new QCountry("country");

    public final StringPath code = createString("code");

    public final NumberPath<Integer> maxLen = createNumber("maxLen", Integer.class);

    public final NumberPath<Integer> minLen = createNumber("minLen", Integer.class);

    public final NumberPath<Integer> nameStringId = createNumber("nameStringId", Integer.class);

    public final NumberPath<Integer> routingTyp = createNumber("routingTyp", Integer.class);

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final StringPath zipFormat = createString("zipFormat");

    public QCountry(String variable) {
        super(Country.class, forVariable(variable));
    }

    public QCountry(Path<? extends Country> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCountry(PathMetadata<?> metadata) {
        super(Country.class, metadata);
    }

}

