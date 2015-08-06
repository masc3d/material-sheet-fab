package org.deku.leo2.node.data.entities.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QStation is a Querydsl query type for Station
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStation extends EntityPathBase<Station> {

    private static final long serialVersionUID = -1729432956L;

    public static final QStation station = new QStation("station");

    public final StringPath address1 = createString("address1");

    public final StringPath address2 = createString("address2");

    public final StringPath billingAddress1 = createString("billingAddress1");

    public final StringPath billingAddress2 = createString("billingAddress2");

    public final StringPath billingCity = createString("billingCity");

    public final StringPath billingCountry = createString("billingCountry");

    public final StringPath billingHouseNr = createString("billingHouseNr");

    public final StringPath billingStreet = createString("billingStreet");

    public final StringPath billingZip = createString("billingZip");

    public final StringPath city = createString("city");

    public final StringPath contactPerson1 = createString("contactPerson1");

    public final StringPath contactPerson2 = createString("contactPerson2");

    public final StringPath country = createString("country");

    public final StringPath email = createString("email");

    public final StringPath houseNr = createString("houseNr");

    public final StringPath mobile = createString("mobile");

    public final StringPath phone1 = createString("phone1");

    public final StringPath phone2 = createString("phone2");

    public final NumberPath<Double> posLat = createNumber("posLat", Double.class);

    public final NumberPath<Double> posLong = createNumber("posLong", Double.class);

    public final StringPath sector = createString("sector");

    public final StringPath servicePhone1 = createString("servicePhone1");

    public final StringPath servicePhone2 = createString("servicePhone2");

    public final NumberPath<Integer> stationNr = createNumber("stationNr", Integer.class);

    public final NumberPath<Integer> strang = createNumber("strang", Integer.class);

    public final StringPath street = createString("street");

    public final StringPath telefax = createString("telefax");

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final StringPath uStId = createString("uStId");

    public final StringPath webAddress = createString("webAddress");

    public final StringPath zip = createString("zip");

    public QStation(String variable) {
        super(Station.class, forVariable(variable));
    }

    public QStation(Path<? extends Station> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStation(PathMetadata<?> metadata) {
        super(Station.class, metadata);
    }

}

