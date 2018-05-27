/**
############ Warning ############
Apply this migration to live only on sunday, before evening
 */

use dekuclient;

ALTER TABLE tblstatus MODIFY Infotext CHAR(60);