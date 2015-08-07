/**
 * This class is generated by jOOQ
 */
package org.deku.leo2.central.data.entities.jooq.tables;


import org.deku.leo2.central.data.entities.jooq.Dekuclient;
import org.deku.leo2.central.data.entities.jooq.Keys;
import org.deku.leo2.central.data.entities.jooq.tables.records.MstRouteRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MstRoute extends TableImpl<MstRouteRecord> {

	private static final long serialVersionUID = 408190717;

	/**
	 * The reference instance of <code>dekuclient.mst_route</code>
	 */
	public static final MstRoute MST_ROUTE = new MstRoute();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MstRouteRecord> getRecordType() {
		return MstRouteRecord.class;
	}

	/**
	 * The column <code>dekuclient.mst_route.layer</code>.
	 */
	public final TableField<MstRouteRecord, Integer> LAYER = createField("layer", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>dekuclient.mst_route.country</code>.
	 */
	public final TableField<MstRouteRecord, String> COUNTRY = createField("country", org.jooq.impl.SQLDataType.VARCHAR.length(5).nullable(false), this, "");

	/**
	 * The column <code>dekuclient.mst_route.zipfrom</code>.
	 */
	public final TableField<MstRouteRecord, String> ZIPFROM = createField("zipfrom", org.jooq.impl.SQLDataType.VARCHAR.length(15).nullable(false), this, "");

	/**
	 * The column <code>dekuclient.mst_route.zipto</code>.
	 */
	public final TableField<MstRouteRecord, String> ZIPTO = createField("zipto", org.jooq.impl.SQLDataType.VARCHAR.length(15), this, "");

	/**
	 * The column <code>dekuclient.mst_route.valid_ctrl</code>.
	 */
	public final TableField<MstRouteRecord, Integer> VALID_CTRL = createField("valid_ctrl", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>dekuclient.mst_route.validfrom</code>.
	 */
	public final TableField<MstRouteRecord, Timestamp> VALIDFROM = createField("validfrom", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>dekuclient.mst_route.validto</code>.
	 */
	public final TableField<MstRouteRecord, Timestamp> VALIDTO = createField("validto", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>dekuclient.mst_route.timestamp</code>.
	 */
	public final TableField<MstRouteRecord, Timestamp> TIMESTAMP = createField("timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>dekuclient.mst_route.station</code>.
	 */
	public final TableField<MstRouteRecord, Integer> STATION = createField("station", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>dekuclient.mst_route.area</code>.
	 */
	public final TableField<MstRouteRecord, String> AREA = createField("area", org.jooq.impl.SQLDataType.VARCHAR.length(5), this, "");

	/**
	 * The column <code>dekuclient.mst_route.etod</code>.
	 */
	public final TableField<MstRouteRecord, Time> ETOD = createField("etod", org.jooq.impl.SQLDataType.TIME, this, "");

	/**
	 * The column <code>dekuclient.mst_route.ltop</code>.
	 */
	public final TableField<MstRouteRecord, Time> LTOP = createField("ltop", org.jooq.impl.SQLDataType.TIME, this, "");

	/**
	 * The column <code>dekuclient.mst_route.term</code>.
	 */
	public final TableField<MstRouteRecord, Integer> TERM = createField("term", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>dekuclient.mst_route.saturday_ok</code>.
	 */
	public final TableField<MstRouteRecord, Integer> SATURDAY_OK = createField("saturday_ok", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>dekuclient.mst_route.ltodsa</code>.
	 */
	public final TableField<MstRouteRecord, Time> LTODSA = createField("ltodsa", org.jooq.impl.SQLDataType.TIME, this, "");

	/**
	 * The column <code>dekuclient.mst_route.ltodholiday</code>.
	 */
	public final TableField<MstRouteRecord, Time> LTODHOLIDAY = createField("ltodholiday", org.jooq.impl.SQLDataType.TIME, this, "");

	/**
	 * The column <code>dekuclient.mst_route.island</code>.
	 */
	public final TableField<MstRouteRecord, Integer> ISLAND = createField("island", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>dekuclient.mst_route.holidayctrl</code>.
	 */
	public final TableField<MstRouteRecord, String> HOLIDAYCTRL = createField("holidayctrl", org.jooq.impl.SQLDataType.VARCHAR.length(20), this, "");

	/**
	 * Create a <code>dekuclient.mst_route</code> table reference
	 */
	public MstRoute() {
		this("mst_route", null);
	}

	/**
	 * Create an aliased <code>dekuclient.mst_route</code> table reference
	 */
	public MstRoute(String alias) {
		this(alias, MST_ROUTE);
	}

	private MstRoute(String alias, Table<MstRouteRecord> aliased) {
		this(alias, aliased, null);
	}

	private MstRoute(String alias, Table<MstRouteRecord> aliased, Field<?>[] parameters) {
		super(alias, Dekuclient.DEKUCLIENT, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MstRouteRecord> getPrimaryKey() {
		return Keys.KEY_MST_ROUTE_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MstRouteRecord>> getKeys() {
		return Arrays.<UniqueKey<MstRouteRecord>>asList(Keys.KEY_MST_ROUTE_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MstRoute as(String alias) {
		return new MstRoute(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MstRoute rename(String name) {
		return new MstRoute(name, null);
	}
}
