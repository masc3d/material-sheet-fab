/**
 * This class is generated by jOOQ
 */
package org.deku.leo2.central.data.entities.jooq;


import org.deku.leo2.central.data.entities.jooq.tables.*;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import javax.annotation.Generated;
import java.util.ArrayList;
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
public class Dekuclient extends SchemaImpl {

	private static final long serialVersionUID = -62789779;

	/**
	 * The reference instance of <code>dekuclient</code>
	 */
	public static final Dekuclient DEKUCLIENT = new Dekuclient();

	/**
	 * No further instances allowed
	 */
	private Dekuclient() {
		super("dekuclient");
	}

	@Override
	public final List<Table<?>> getTables() {
		List result = new ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final List<Table<?>> getTables0() {
		return Arrays.<Table<?>>asList(
			MstCountry.MST_COUNTRY,
			MstHolidayctrl.MST_HOLIDAYCTRL,
			MstNode.MST_NODE,
			MstRoute.MST_ROUTE,
			MstRoutinglayer.MST_ROUTINGLAYER,
			MstSector.MST_SECTOR,
			MstStation.MST_STATION,
			MstStationSector.MST_STATION_SECTOR,
			SysProperty.SYS_PROPERTY,
			SysRoutinglayer.SYS_ROUTINGLAYER,
			SysValues.SYS_VALUES,
			Tbldepotliste.TBLDEPOTLISTE);
	}
}
