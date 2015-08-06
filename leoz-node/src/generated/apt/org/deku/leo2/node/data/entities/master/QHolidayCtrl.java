package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QHolidayCtrl is a Querydsl query type for HolidayCtrl
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QHolidayCtrl extends EntityPathBase<HolidayCtrl> {

    private static final long serialVersionUID = 180304947L;

    public static final QHolidayCtrl holidayCtrl = new QHolidayCtrl("holidayCtrl");

    public final StringPath country = createString("country");

    public final NumberPath<Integer> ctrlPos = createNumber("ctrlPos", Integer.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.sql.Timestamp> holiday = createDateTime("holiday", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public QHolidayCtrl(String variable) {
        super(HolidayCtrl.class, forVariable(variable));
    }

    public QHolidayCtrl(Path<? extends HolidayCtrl> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHolidayCtrl(PathMetadata<?> metadata) {
        super(HolidayCtrl.class, metadata);
    }

}

