/*password=test*/

INSERT INTO mst_user (debitor_id, email, password, alias, role, active, firstname, lastname, id) VALUES
  ((SELECT debitor_id
    FROM mst_station
    WHERE station_nr = 20), 'max@deku.org',
   'd0b599626680e260619b78aeabf72f745ea7905d', 'max', 'USER', -1, 'Max', 'Mustermann', 2);


INSERT INTO mst_user (debitor_id, email, password, alias, role, active, firstname, lastname, id) VALUES
  ((SELECT debitor_id
    FROM mst_station
    WHERE station_nr = 20), 'erika@deku.org',
   'd24684bb5ccf6240aae18da602a68152f0a092b5', 'erika', 'ADMIN', -1, 'Erika', 'Musterfrau', 3);

/*
-- Query: select * from tad_node_geoposition
LIMIT 0, 1000

-- Date: 2017-06-01 11:02
*/
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (8, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9283, 9.0164, '2017-05-24 00:23:12', 5.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (9, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01547, '2017-05-24 00:25:07', 4.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (10, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92675, 9.01399, '2017-05-24 00:47:03', 27.7, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (11, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92717, 9.01536, '2017-05-24 00:49:44', 2.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (12, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92815, 9.01568, '2017-05-24 00:50:44', 24.6, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (13, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92749, 9.01434, '2017-05-24 00:52:32', 10.9, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (14, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92782, 9.01582, '2017-05-24 00:54:04', 3.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (15, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92702, 9.01483, '2017-05-24 01:05:46', 30.2, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (16, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92783, 9.01564, '2017-05-24 01:06:46', 2.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (17, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92719, 9.01444, '2017-05-24 01:09:54', 3.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (18, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92786, 9.01541, '2017-05-24 01:13:24', 4.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (19, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9275, 9.01407, '2017-05-24 01:42:27', 12.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (20, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01572, '2017-05-24 01:43:29', 3.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (21, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01712, '2017-05-24 02:01:50', 8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (22, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.0156, '2017-05-24 02:03:44', 9.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (23, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92757, 9.01697, '2017-05-24 02:08:52', 1.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (24, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92748, 9.01524, '2017-05-24 02:11:03', 2.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (25, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01389, '2017-05-24 02:14:21', 12.7, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (26, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.0153, '2017-05-24 02:17:15', 5.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (27, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01729, '2017-05-24 02:18:45', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (28, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01551, '2017-05-24 02:20:09', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (29, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01699, '2017-05-24 02:24:09', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (30, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01554, '2017-05-24 02:25:20', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (31, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9273, 9.01698, '2017-05-24 03:37:43', 15.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (32, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92786, 9.01564, '2017-05-24 03:38:52', 3.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (33, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9274, 9.01369, '2017-05-24 03:57:42', 5.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (34, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01559, '2017-05-24 03:58:42', 0.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (35, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92786, 9.017, '2017-05-24 04:36:15', 6.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (36, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.0157, '2017-05-24 04:37:49', 2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (37, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92788, 9.01705, '2017-05-24 04:45:29', 19.9, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (38, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.0157, '2017-05-24 04:46:52', 2.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (39, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92792, 9.01786, '2017-05-24 05:34:54', 8.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (40, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92744, 9.01584, '2017-05-24 05:36:56', 12.5, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (41, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92783, 9.01712, '2017-05-24 05:41:07', 20.8, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (42, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01566, '2017-05-24 05:42:07', 4.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (43, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01426, '2017-05-24 06:23:18', 10.8, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (44, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92736, 9.01572, '2017-05-24 06:24:52', 3.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (45, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9279, 9.01454, '2017-05-24 06:30:54', 4.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (46, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92712, 9.01532, '2017-05-24 06:31:59', 1.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (47, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92745, 9.014, '2017-05-24 06:33:01', 7.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (48, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01553, '2017-05-24 06:34:01', 12.6, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (49, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92743, 9.01407, '2017-05-24 06:44:56', 29.9, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (50, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01543, '2017-05-24 06:45:56', 2.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (51, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92716, 9.01421, '2017-05-24 06:56:14', 17.6, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (52, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92786, 9.01676, '2017-05-24 06:59:57', 3.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (53, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92755, 9.01497, '2017-05-24 07:00:58', 10.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (54, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92797, 9.01632, '2017-05-24 07:06:38', 2.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (55, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01463, '2017-05-24 07:07:38', 5.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (56, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92786, 9.01618, '2017-05-24 07:08:43', 0.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (57, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92917, 9.01854, '2017-05-24 07:19:33', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (58, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92783, 9.01657, '2017-05-24 07:20:52', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (59, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92836, 9.01787, '2017-05-24 07:27:51', 22.8, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (60, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92749, 9.01651, '2017-05-24 07:28:51', 9.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (61, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92855, 9.01574, '2017-05-24 07:29:52', 2.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (62, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01571, '2017-05-24 07:30:56', 31.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (63, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92693, 9.01407, '2017-05-24 07:31:56', 8.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (64, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92746, 9.01543, '2017-05-24 07:32:56', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (65, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92784, 9.01679, '2017-05-24 07:36:41', 18.5, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (66, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01555, '2017-05-24 07:37:43', 9.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (67, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92794, 9.01671, '2017-05-24 07:48:31', 5.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (68, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01542, '2017-05-24 07:50:44', 2.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (69, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92831, 9.01615, '2017-05-24 08:16:11', 2.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (70, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9275, 9.01543, '2017-05-24 08:30:04', 16.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (71, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92566, 9.01496, '2017-05-24 08:31:04', 27.3, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (72, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.923, 9.01625, '2017-05-24 08:32:04', 53.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (73, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92575, 9.03202, '2017-05-24 08:33:04', 67.5, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (74, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92503, 9.04278, '2017-05-24 08:34:04', 57.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (75, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93016, 9.04279, '2017-05-24 08:35:04', 27.1, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (76, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9349, 9.04103, '2017-05-24 08:36:04', 19.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (77, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93762, 9.03543, '2017-05-24 08:37:04', 40.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (78, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93575, 9.03433, '2017-05-24 08:38:04', 5.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (79, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93423, 9.03524, '2017-05-24 08:39:04', 3.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (80, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93514, 9.03544, '2017-05-24 08:41:39', 19.6, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (81, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93744, 9.03475, '2017-05-24 08:42:39', 39.3, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (82, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9353, 9.04067, '2017-05-24 08:43:39', 42.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (83, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93803, 9.05069, '2017-05-24 08:44:39', 64.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (84, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93463, 9.06218, '2017-05-24 08:45:39', 70.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (85, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93109, 9.06731, '2017-05-24 08:46:39', 19.5, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (86, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93008, 9.06754, '2017-05-24 08:53:15', 51, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (87, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92905, 9.07822, '2017-05-24 08:54:15', 62.3, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (88, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92613, 9.08336, '2017-05-24 08:55:15', 49.2, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (89, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9219, 9.08417, '2017-05-24 08:56:15', 20.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (90, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92096, 9.08435, '2017-05-24 08:59:44', 29.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (91, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91768, 9.08096, '2017-05-24 09:00:44', 21.1, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (92, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91775, 9.07341, '2017-05-24 09:01:44', 35.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (93, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91382, 9.06421, '2017-05-24 09:02:44', 58.2, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (94, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90963, 9.06015, '2017-05-24 09:03:44', 38.8, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (95, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90737, 9.05852, '2017-05-24 09:04:44', 30.2, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (96, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90166, 9.05443, '2017-05-24 09:05:44', 53.9, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (97, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89517, 9.04637, '2017-05-24 09:06:44', 47.8, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (98, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89302, 9.03923, '2017-05-24 09:07:44', 16.8, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (99, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89305, 9.04065, '2017-05-24 09:10:59', 34.4, NULL,
            NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (100, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89504, 9.04633, '2017-05-24 09:11:59', 46.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (101, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90264, 9.05473, '2017-05-24 09:12:59', 66.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (102, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90799, 9.05982, '2017-05-24 09:13:59', 27.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (103, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91152, 9.06189, '2017-05-24 09:14:59', 38.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (104, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91652, 9.06907, '2017-05-24 09:15:59', 42.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (105, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91748, 9.07733, '2017-05-24 09:16:59', 34.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (106, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91766, 9.08094, '2017-05-24 09:17:59', 30.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (107, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91667, 9.09133, '2017-05-24 09:18:59', 80, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (108, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91505, 9.10982, '2017-05-24 09:19:59', 73, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (109, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9088, 9.11983, '2017-05-24 09:20:59', 103.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (110, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89353, 9.13187, '2017-05-24 09:21:59', 111.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (111, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87872, 9.14361, '2017-05-24 09:22:59', 116.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (112, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86342, 9.15528, '2017-05-24 09:23:59', 107.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (113, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84757, 9.15231, '2017-05-24 09:24:59', 102.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (114, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83717, 9.14551, '2017-05-24 09:25:59', 42.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (115, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83678, 9.15559, '2017-05-24 09:26:59', 56.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (116, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83503, 9.15758, '2017-05-24 09:27:59', 41.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (117, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82498, 9.15483, '2017-05-24 09:28:59', 72.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (118, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81437, 9.15686, '2017-05-24 09:29:59', 65.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (119, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8081, 9.15645, '2017-05-24 09:30:59', 41.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (120, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80805, 9.15504, '2017-05-24 09:37:35', 15.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (121, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80688, 9.14971, '2017-05-24 09:38:35', 29.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (122, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80818, 9.14577, '2017-05-24 09:39:35', 0.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (123, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80894, 9.14663, '2017-05-24 09:44:03', 35, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (124, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80797, 9.15436, '2017-05-24 09:45:03', 56.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (125, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80575, 9.16164, '2017-05-24 09:46:03', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (126, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8039, 9.16392, '2017-05-24 09:47:03', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (127, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80163, 9.16489, '2017-05-24 09:48:03', 22, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (128, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79896, 9.17632, '2017-05-24 09:49:03', 70.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (129, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79621, 9.18955, '2017-05-24 09:50:03', 44, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (130, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79445, 9.19487, '2017-05-24 09:51:03', 28.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (131, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7949, 9.19623, '2017-05-24 09:54:25', 37.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (132, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79485, 9.2016, '2017-05-24 09:55:25', 0.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (133, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79329, 9.20117, '2017-05-24 09:56:25', 49.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (134, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78504, 9.19943, '2017-05-24 09:57:25', 58.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (135, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78377, 9.18855, '2017-05-24 09:58:25', 39.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (136, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78388, 9.18257, '2017-05-24 09:59:25', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (137, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78157, 9.17692, '2017-05-24 10:00:25', 42.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (138, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78343, 9.17087, '2017-05-24 10:01:25', 56.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (139, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78251, 9.17079, '2017-05-24 10:04:08', 9.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (140, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78346, 9.17106, '2017-05-24 10:05:34', 52.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (141, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78127, 9.17763, '2017-05-24 10:06:34', 36.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (142, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7806, 9.18284, '2017-05-24 10:07:34', 22.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (143, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.777, 9.18398, '2017-05-24 10:08:34', 32.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (144, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77542, 9.18459, '2017-05-24 10:09:34', 1.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (145, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77455, 9.18415, '2017-05-24 10:11:58', 44.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (146, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76779, 9.18401, '2017-05-24 10:12:58', 17.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (147, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76687, 9.18396, '2017-05-24 10:16:23', 40.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (148, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76015, 9.18259, '2017-05-24 10:17:23', 47.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (149, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75069, 9.1803, '2017-05-24 10:18:23', 73.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (150, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74014, 9.18826, '2017-05-24 10:19:23', 82.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (151, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73159, 9.20216, '2017-05-24 10:20:23', 82.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (152, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72922, 9.21602, '2017-05-24 10:21:23', 47.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (153, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72811, 9.22224, '2017-05-24 10:22:23', 23.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (154, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72872, 9.22588, '2017-05-24 10:23:23', 22.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (155, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73121, 9.226, '2017-05-24 10:24:23', 35.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (156, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72751, 9.22194, '2017-05-24 10:25:23', 3.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (157, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73057, 9.22529, '2017-05-24 10:26:23', 60.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (158, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.728, 9.22507, '2017-05-24 10:27:23', 29, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (159, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72624, 9.2246, '2017-05-24 10:28:23', 35.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (160, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72376, 9.22675, '2017-05-24 10:29:23', 45.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (161, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71905, 9.22698, '2017-05-24 10:30:23', 13.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (162, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71375, 9.21854, '2017-05-24 10:31:23', 44.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (163, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70513, 9.22033, '2017-05-24 10:32:23', 77.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (164, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70154, 9.22069, '2017-05-24 10:33:23', 45.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (165, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71039, 9.21829, '2017-05-24 10:34:23', 41.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (166, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71186, 9.22023, '2017-05-24 10:35:23', 10.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (167, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71099, 9.21988, '2017-05-24 10:38:01', 43.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (168, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71491, 9.21991, '2017-05-24 10:39:01', 59.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (169, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71883, 9.22755, '2017-05-24 10:40:01', 52.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (170, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71964, 9.2325, '2017-05-24 10:41:01', 57.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (171, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71188, 9.23962, '2017-05-24 10:42:01', 24.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (172, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71306, 9.24272, '2017-05-24 10:43:01', 0.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (173, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71251, 9.24151, '2017-05-24 10:45:52', 28.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (174, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7113, 9.23976, '2017-05-24 10:46:52', 46.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (175, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70565, 9.24504, '2017-05-24 10:47:52', 31.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (176, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70459, 9.25084, '2017-05-24 10:48:52', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (177, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7039, 9.25177, '2017-05-24 10:51:39', 44.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (178, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70318, 9.2568, '2017-05-24 10:52:39', 0.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (179, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70449, 9.2584, '2017-05-24 10:53:39', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (180, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7036, 9.26071, '2017-05-24 10:54:39', 36.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (181, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7066, 9.26451, '2017-05-24 10:55:39', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (182, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70758, 9.26258, '2017-05-24 10:56:39', 35.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (183, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71627, 9.26467, '2017-05-24 10:57:39', 75.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (184, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72043, 9.27048, '2017-05-24 10:58:39', 30.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (185, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71842, 9.27082, '2017-05-24 10:59:39', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (186, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71927, 9.27139, '2017-05-24 11:02:55', 38.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (187, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71925, 9.26894, '2017-05-24 11:03:55', 34.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (188, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7149, 9.26842, '2017-05-24 11:04:55', 32.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (189, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70943, 9.26727, '2017-05-24 11:05:55', 48.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (190, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70569, 9.26357, '2017-05-24 11:06:55', 3.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (191, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70416, 9.25954, '2017-05-24 11:07:55', 18.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (192, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70453, 9.25823, '2017-05-24 11:08:59', 31.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (193, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70307, 9.25299, '2017-05-24 11:09:59', 39.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (194, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70473, 9.25068, '2017-05-24 11:10:59', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (195, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70539, 9.24961, '2017-05-24 11:16:44', 24.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (196, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70874, 9.24121, '2017-05-24 11:17:44', 56.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (197, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71761, 9.23501, '2017-05-24 11:18:44', 71, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (198, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72283, 9.22796, '2017-05-24 11:19:44', 28.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (199, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72579, 9.22385, '2017-05-24 11:20:44', 8.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (200, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72773, 9.22855, '2017-05-24 11:21:44', 39.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (201, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73082, 9.23277, '2017-05-24 11:22:44', 19.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (202, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73302, 9.23048, '2017-05-24 11:23:44', 41.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (203, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74068, 9.23485, '2017-05-24 11:24:44', 63.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (204, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74946, 9.2414, '2017-05-24 11:25:44', 70.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (205, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76122, 9.24441, '2017-05-24 11:26:44', 72.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (206, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76978, 9.24773, '2017-05-24 11:27:44', 40.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (207, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7741, 9.24751, '2017-05-24 11:28:44', 43.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (208, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78186, 9.25687, '2017-05-24 11:29:44', 78.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (209, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79074, 9.26281, '2017-05-24 11:30:44', 43.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (210, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79398, 9.26273, '2017-05-24 11:31:44', 67.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (211, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80009, 9.25207, '2017-05-24 11:32:44', 67.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (212, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80716, 9.24389, '2017-05-24 11:33:44', 70.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (213, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81545, 9.24199, '2017-05-24 11:34:44', 35.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (214, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81808, 9.25203, '2017-05-24 11:35:44', 41, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (215, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8207, 9.25631, '2017-05-24 11:36:44', 1.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (216, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82, 9.2576, '2017-05-24 11:37:44', 4.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (217, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82175, 9.25581, '2017-05-24 11:38:44', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (218, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82232, 9.25463, '2017-05-24 11:42:22', 38.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (219, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82548, 9.25422, '2017-05-24 11:43:22', 44.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (220, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83079, 9.26022, '2017-05-24 11:44:22', 59.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (221, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83862, 9.27159, '2017-05-24 11:45:22', 73.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (222, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84864, 9.27438, '2017-05-24 11:46:22', 69.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (223, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85497, 9.27844, '2017-05-24 11:47:22', 41.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (224, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86103, 9.28631, '2017-05-24 11:48:22', 56.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (225, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86689, 9.28742, '2017-05-24 11:49:22', 66.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (226, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87614, 9.29228, '2017-05-24 11:50:22', 62.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (227, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88166, 9.29375, '2017-05-24 11:51:22', 26.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (228, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88442, 9.28623, '2017-05-24 11:52:22', 55.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (229, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88662, 9.27882, '2017-05-24 11:53:22', 29.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (230, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88648, 9.27744, '2017-05-24 11:54:23', 0.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (231, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88663, 9.27891, '2017-05-24 11:59:58', 25.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (232, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88586, 9.28559, '2017-05-24 12:00:58', 45.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (233, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88243, 9.29258, '2017-05-24 12:01:58', 33.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (234, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87975, 9.28884, '2017-05-24 12:02:58', 41.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (235, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87259, 9.29017, '2017-05-24 12:03:58', 63.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (236, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86342, 9.28713, '2017-05-24 12:04:58', 3.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (237, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86283, 9.28825, '2017-05-24 12:33:32', 39.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (238, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85765, 9.28242, '2017-05-24 12:34:32', 47.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (239, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85363, 9.27752, '2017-05-24 12:35:32', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (240, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8528, 9.27684, '2017-05-24 12:41:01', 45.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (241, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84362, 9.27312, '2017-05-24 12:42:01', 78.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (242, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8342, 9.2658, '2017-05-24 12:43:01', 67.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (243, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82752, 9.25641, '2017-05-24 12:44:01', 37.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (244, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82665, 9.25554, '2017-05-24 12:45:01', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (245, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82588, 9.25459, '2017-05-24 13:38:11', 39.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (246, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82149, 9.25442, '2017-05-24 13:39:11', 42, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (247, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81561, 9.24295, '2017-05-24 13:40:11', 69.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (248, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81861, 9.22828, '2017-05-24 13:41:11', 77.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (249, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82688, 9.21662, '2017-05-24 13:42:11', 79.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (250, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83078, 9.20661, '2017-05-24 13:43:11', 40, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (251, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83403, 9.19762, '2017-05-24 13:44:11', 60.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (252, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83854, 9.18408, '2017-05-24 13:45:11', 70.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (253, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84179, 9.17402, '2017-05-24 13:46:11', 31.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (254, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8366, 9.16549, '2017-05-24 13:47:11', 52.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (255, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83654, 9.15875, '2017-05-24 13:48:11', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (256, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83708, 9.15356, '2017-05-24 13:49:11', 49.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (257, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84287, 9.15096, '2017-05-24 13:50:11', 94.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (258, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85762, 9.15548, '2017-05-24 13:51:11', 104.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (259, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87088, 9.14997, '2017-05-24 13:52:11', 94.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (260, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88348, 9.14004, '2017-05-24 13:53:11', 91.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (261, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89622, 9.12997, '2017-05-24 13:54:11', 91.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (262, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90802, 9.12064, '2017-05-24 13:55:11', 83.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (263, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91969, 9.11141, '2017-05-24 13:56:11', 92.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (264, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92996, 9.10326, '2017-05-24 13:57:11', 70.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (265, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93807, 9.0933, '2017-05-24 13:58:11', 61, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (266, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93453, 9.08946, '2017-05-24 13:59:11', 76.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (267, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.929, 9.07966, '2017-05-24 14:00:11', 63.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (268, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92931, 9.07181, '2017-05-24 14:01:11', 39.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (269, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92598, 9.06116, '2017-05-24 14:02:11', 72, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (270, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92189, 9.05172, '2017-05-24 14:03:11', 69, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (271, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92465, 9.0399, '2017-05-24 14:04:11', 61.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (272, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92383, 9.02524, '2017-05-24 14:05:11', 67.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (273, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92341, 9.01441, '2017-05-24 14:06:11', 35.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (274, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01561, '2017-05-24 14:07:11', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (275, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92824, 9.01361, '2017-05-24 15:13:13', 17.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (276, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01526, '2017-05-24 15:14:13', 1.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (277, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92728, 9.01732, '2017-05-24 15:23:59', 30.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (278, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9274, 9.01577, '2017-05-24 15:24:59', 6.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (279, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92663, 9.01495, '2017-05-24 15:36:37', 4.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (280, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01577, '2017-05-24 15:37:37', 2.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (281, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92629, 9.0162, '2017-05-24 15:39:11', 1.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (282, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01572, '2017-05-24 15:40:11', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (283, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92836, 9.01488, '2017-05-24 16:10:55', 27.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (284, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.0158, '2017-05-24 16:11:55', 2.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (285, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92669, 9.01613, '2017-05-24 16:13:40', 9.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (286, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01572, '2017-05-24 16:14:40', 0.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (287, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.927, 9.01482, '2017-05-24 16:35:50', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (288, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01568, '2017-05-24 16:37:00', 8.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (289, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92739, 9.01435, '2017-05-24 16:42:21', 9.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (290, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01562, '2017-05-24 16:44:39', 1.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (291, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92735, 9.01436, '2017-05-24 17:10:10', 3.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (292, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92791, 9.0186, '2017-05-24 17:11:46', 36.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (293, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92819, 9.0158, '2017-05-24 17:12:46', 0.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (294, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01374, '2017-05-24 17:25:29', 16.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (295, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92803, 9.01555, '2017-05-24 17:26:29', 2.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (296, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01709, '2017-05-24 17:37:44', 32.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (297, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01561, '2017-05-24 17:38:48', 8.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (298, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01723, '2017-05-24 17:40:03', 4.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (299, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01534, '2017-05-24 17:41:30', 4.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (300, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92861, 9.01555, '2017-05-24 17:50:27', 12.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (301, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92757, 9.01571, '2017-05-24 17:51:34', 2.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (302, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92666, 9.01394, '2017-05-24 18:40:42', 9.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (303, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9273, 9.01562, '2017-05-24 18:41:44', 17.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (304, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92819, 9.01598, '2017-05-24 19:09:10', 3.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (305, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92681, 9.01451, '2017-05-24 19:10:41', 35.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (306, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01569, '2017-05-24 19:18:33', 11.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (307, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92809, 9.01694, '2017-05-24 19:22:11', 21.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (308, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93331, 9.03517, '2017-05-24 19:23:11', 96.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (309, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92724, 9.01426, '2017-05-24 19:26:58', 26.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (310, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.0155, '2017-05-24 19:28:12', 0.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (311, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92717, 9.01431, '2017-05-24 19:54:22', 8.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (312, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92486, 9.00781, '2017-05-24 19:55:22', 24.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (313, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01534, '2017-05-24 19:56:22', 3.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (314, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92656, 9.01552, '2017-05-24 20:40:37', 19.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (315, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9275, 9.01535, '2017-05-24 20:41:37', 2.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (316, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92651, 9.0152, '2017-05-24 20:48:24', 2.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (317, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01523, '2017-05-24 20:49:54', 5.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (318, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92671, 9.01544, '2017-05-24 20:51:28', 2.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (319, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01544, '2017-05-24 20:52:37', 6.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (320, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9264, 9.01492, '2017-05-24 20:53:40', 8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (321, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92741, 9.01564, '2017-05-24 20:54:40', 6.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (322, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92845, 9.01417, '2017-05-24 20:56:39', 3.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (323, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01501, '2017-05-24 20:57:51', 10.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (324, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92647, 9.01618, '2017-05-24 21:01:32', 4.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (325, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9275, 9.01503, '2017-05-24 21:02:32', 2.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (326, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92845, 9.01514, '2017-05-24 21:19:16', 8.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (327, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92664, 9.0153, '2017-05-24 21:20:16', 8.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (328, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01495, '2017-05-24 21:22:27', 8.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (329, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92641, 9.01547, '2017-05-24 21:24:13', 12.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (330, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92761, 9.01527, '2017-05-24 21:27:17', 6.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (331, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92629, 9.01445, '2017-05-24 22:01:50', 25.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (332, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01585, '2017-05-24 22:02:50', 2.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (333, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92666, 9.01657, '2017-05-24 22:09:38', 4.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (334, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01522, '2017-05-24 22:11:06', 2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (335, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92833, 9.01622, '2017-05-24 22:28:52', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (336, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01526, '2017-05-24 22:32:59', 2.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (337, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01672, '2017-05-24 22:35:08', 22.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (338, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92738, 9.01536, '2017-05-24 22:41:52', 11.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (339, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.0139, '2017-05-24 22:43:22', 24.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (340, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92713, 9.01579, '2017-05-24 22:45:20', 2.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (341, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92799, 9.01634, '2017-05-24 22:46:38', 13, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (342, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92731, 9.01541, '2017-05-24 22:50:38', 13.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (343, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92791, 9.01693, '2017-05-24 23:09:17', 6.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (344, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01562, '2017-05-24 23:14:03', 3.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (345, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92848, 9.01533, '2017-05-25 00:14:58', 6.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (346, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01553, '2017-05-25 00:20:39', 1.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (347, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92838, 9.01628, '2017-05-25 00:49:44', 22.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (348, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.0152, '2017-05-25 00:51:14', 2.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (349, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92832, 9.01608, '2017-05-25 01:00:26', 9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (350, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92735, 9.01537, '2017-05-25 01:01:27', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (351, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92824, 9.01632, '2017-05-25 01:06:35', 13.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (352, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92733, 9.01528, '2017-05-25 01:07:45', 10.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (353, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01391, '2017-05-25 01:35:26', 3.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (354, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01569, '2017-05-25 01:36:26', 4.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (355, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92687, 9.01282, '2017-05-25 02:12:06', 48.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (356, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92733, 9.01846, '2017-05-25 02:13:06', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (357, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01601, '2017-05-25 02:14:06', 3.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (358, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92779, 9.01467, '2017-05-25 03:00:17', 7.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (359, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92762, 9.01606, '2017-05-25 03:01:32', 4.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (360, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92709, 9.01492, '2017-05-25 03:44:18', 16.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (361, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.01596, '2017-05-25 03:50:13', 5.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (362, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.928, 9.01728, '2017-05-25 04:47:34', 7.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (363, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92762, 9.01554, '2017-05-25 04:48:34', 5.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (364, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92723, 9.01337, '2017-05-25 04:49:34', 9.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (365, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92744, 9.01485, '2017-05-25 04:50:54', 5.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (366, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01617, '2017-05-25 04:57:00', 7.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (367, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92743, 9.01484, '2017-05-25 05:07:37', 5.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (368, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01627, '2017-05-25 05:10:50', 14.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (369, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01432, '2017-05-25 05:12:15', 1.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (370, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01575, '2017-05-25 05:15:53', 1.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (371, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.01432, '2017-05-25 06:20:29', 4.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (372, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.928, 9.01599, '2017-05-25 06:22:15', 1.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (373, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01516, '2017-05-25 06:26:43', 2.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (374, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92814, 9.01549, '2017-05-25 06:28:11', 7.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (375, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92728, 9.01593, '2017-05-25 06:29:32', 5.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (376, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01472, '2017-05-25 06:34:09', 4.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (377, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01612, '2017-05-25 06:42:35', 9.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (378, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92728, 9.01486, '2017-05-25 07:14:26', 2.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (379, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.0161, '2017-05-25 07:20:19', 6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (380, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92738, 9.01478, '2017-05-25 07:23:39', 5.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (381, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92557, 9.01301, '2017-05-25 07:28:45', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (382, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92733, 9.01497, '2017-05-25 07:30:15', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (383, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.928, 9.01594, '2017-05-25 07:48:32', 10.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (384, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92818, 9.01733, '2017-05-25 07:53:12', 15.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (385, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92736, 9.01432, '2017-05-25 07:54:20', 14.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (386, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92791, 9.0165, '2017-05-25 07:55:20', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (387, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92715, 9.01363, '2017-05-25 07:57:09', 14.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (388, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01552, '2017-05-25 07:58:09', 13, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (389, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92734, 9.01411, '2017-05-25 08:16:11', 13.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (390, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01572, '2017-05-25 08:22:46', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (391, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01361, '2017-05-25 08:23:52', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (392, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01554, '2017-05-25 08:24:52', 9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (393, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92792, 9.01394, '2017-05-25 09:04:36', 21.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (394, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92757, 9.01599, '2017-05-25 09:05:36', 22.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (395, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92824, 9.01458, '2017-05-25 09:08:39', 20.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (396, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01578, '2017-05-25 09:09:39', 2.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (397, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.0142, '2017-05-25 09:25:30', 1.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (398, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01578, '2017-05-25 09:26:32', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (399, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.0144, '2017-05-25 10:18:51', 9.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (400, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01583, '2017-05-25 10:19:52', 4.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (401, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92771, 9.01831, '2017-05-25 10:20:52', 3.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (402, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92785, 9.01675, '2017-05-25 10:21:52', 7.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (403, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01534, '2017-05-25 10:23:51', 25.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (404, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01677, '2017-05-25 10:26:17', 9.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (405, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01532, '2017-05-25 10:28:01', 9.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (406, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92716, 9.01651, '2017-05-25 10:32:13', 6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (407, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01468, '2017-05-25 10:35:50', 6.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (408, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01625, '2017-05-25 10:36:56', 1.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (409, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92787, 9.01479, '2017-05-25 10:41:37', 6.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (410, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01623, '2017-05-25 10:43:14', 10, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (411, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.01458, '2017-05-25 10:45:14', 12.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (412, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01579, '2017-05-25 10:46:14', 6.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (413, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92825, 9.0167, '2017-05-25 11:14:48', 11.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (414, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01567, '2017-05-25 11:15:48', 4.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (415, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92651, 9.01433, '2017-05-25 11:16:48', 9.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (416, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92582, 9.01332, '2017-05-25 11:18:18', 9.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (417, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92788, 9.01584, '2017-05-25 11:19:18', 2.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (418, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92695, 9.01548, '2017-05-25 11:48:35', 6.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (419, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92832, 9.01528, '2017-05-25 11:50:47', 17.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (420, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92738, 9.01523, '2017-05-25 11:51:47', 5.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (421, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92813, 9.01603, '2017-05-25 11:59:11', 9.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (422, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92643, 9.01505, '2017-05-25 12:00:17', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (423, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92732, 9.01522, '2017-05-25 12:01:30', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (424, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92684, 9.01645, '2017-05-25 12:04:53', 4.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (425, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.01587, '2017-05-25 12:13:51', 6.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (426, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92683, 9.01577, '2017-05-25 12:19:58', 6.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (427, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92785, 9.01578, '2017-05-25 12:24:19', 9.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (428, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92721, 9.01474, '2017-05-25 13:24:31', 15.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (429, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92771, 9.01592, '2017-05-25 13:26:15', 8.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (430, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92804, 9.01456, '2017-05-25 15:02:48', 36.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (431, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01592, '2017-05-25 15:03:58', 23.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (432, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92861, 9.01466, '2017-05-25 15:19:23', 12.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (433, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.01544, '2017-05-25 15:20:23', 5.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (434, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92793, 9.01682, '2017-05-25 15:25:34', 10.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (435, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.0155, '2017-05-25 15:27:56', 16.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (436, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9267, 9.0162, '2017-05-25 15:40:12', 3.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (437, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92796, 9.01536, '2017-05-25 15:41:12', 4.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (438, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92707, 9.01594, '2017-05-25 15:42:58', 15.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (439, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92813, 9.01559, '2017-05-25 15:44:15', 15.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (440, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92696, 9.01607, '2017-05-25 15:45:21', 13, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (441, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92805, 9.01531, '2017-05-25 15:46:33', 4.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (442, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92688, 9.01637, '2017-05-25 15:47:58', 4.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (443, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01541, '2017-05-25 16:05:43', 4.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (444, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.0172, '2017-05-25 22:23:15', 2.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (445, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01581, '2017-05-25 22:26:02', 1.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (446, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9274, 9.01723, '2017-05-25 23:10:30', 57.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (447, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92835, 9.01456, '2017-05-25 23:12:34', 8.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (448, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92735, 9.01524, '2017-05-25 23:13:34', 1.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (449, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92825, 9.01517, '2017-05-25 23:20:15', 24.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (450, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92746, 9.01583, '2017-05-25 23:22:50', 4.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (451, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92655, 9.01587, '2017-05-26 00:06:00', 10.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (452, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93356, 9.01433, '2017-05-26 00:11:27', 2.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (453, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92943, 9.01538, '2017-05-26 00:12:27', 9.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (454, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01561, '2017-05-26 00:13:27', 4.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (455, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92845, 9.01544, '2017-05-26 00:15:42', 18.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (456, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92744, 9.01559, '2017-05-26 00:16:42', 1.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (457, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92861, 9.01524, '2017-05-26 00:17:42', 5.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (458, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.01553, '2017-05-26 00:19:46', 14.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (459, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92648, 9.01583, '2017-05-26 00:20:46', 3.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (460, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01536, '2017-05-26 00:21:47', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (461, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01391, '2017-05-26 02:03:43', 11.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (462, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01538, '2017-05-26 02:05:06', 6.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (463, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92793, 9.01409, '2017-05-26 02:07:23', 13.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (464, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01562, '2017-05-26 02:08:23', 7.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (465, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01704, '2017-05-26 02:09:46', 7.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (466, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01563, '2017-05-26 02:13:07', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (467, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01711, '2017-05-26 02:14:12', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (468, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92774, 9.01555, '2017-05-26 02:15:12', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (469, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01711, '2017-05-26 02:16:20', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (470, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92762, 9.01573, '2017-05-26 02:19:34', 2.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (471, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9278, 9.0143, '2017-05-26 02:58:36', 18.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (472, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92725, 9.01687, '2017-05-26 03:00:00', 22.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (473, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92749, 9.01546, '2017-05-26 03:01:00', 4.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (474, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92842, 9.01297, '2017-05-26 03:20:22', 17.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (475, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01494, '2017-05-26 03:21:22', 6.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (476, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01639, '2017-05-26 03:28:19', 9.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (477, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01508, '2017-05-26 03:29:44', 8.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (478, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92698, 9.01607, '2017-05-26 03:30:44', 4.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (479, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92815, 9.01506, '2017-05-26 03:33:13', 6.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (480, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9273, 9.01568, '2017-05-26 03:34:15', 4.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (481, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92817, 9.01515, '2017-05-26 03:36:23', 20.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (482, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92719, 9.01603, '2017-05-26 03:37:28', 1.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (483, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.01526, '2017-05-26 03:39:04', 15.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (484, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92711, 9.01603, '2017-05-26 03:40:35', 10.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (485, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92802, 9.01491, '2017-05-26 03:41:37', 10.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (486, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92685, 9.0161, '2017-05-26 03:43:19', 3.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (487, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01516, '2017-05-26 03:44:30', 17.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (488, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92647, 9.01666, '2017-05-26 03:45:50', 29.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (489, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9279, 9.01537, '2017-05-26 03:49:14', 9.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (490, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92682, 9.01488, '2017-05-26 03:55:07', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (491, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92803, 9.01514, '2017-05-26 03:56:07', 3.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (492, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92712, 9.01495, '2017-05-26 04:00:02', 13, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (493, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01599, '2017-05-26 04:59:09', 3.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (494, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92739, 9.01392, '2017-05-26 05:10:19', 12.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (495, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01584, '2017-05-26 05:11:19', 16.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (496, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01462, '2017-05-26 05:16:38', 3.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (497, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.01601, '2017-05-26 05:19:56', 4.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (498, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01455, '2017-05-26 05:22:55', 2.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (499, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01595, '2017-05-26 05:24:10', 8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (500, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01547, '2017-05-26 07:35:34', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (501, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01552, '2017-05-26 07:36:00', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (502, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92774, 9.01553, '2017-05-26 07:37:26', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (503, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01575, '2017-05-26 07:38:39', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (504, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01569, '2017-05-26 07:39:52', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (505, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01567, '2017-05-26 07:41:05', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (506, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.01566, '2017-05-26 07:42:13', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (507, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.0157, '2017-05-26 07:43:22', 0.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (508, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01574, '2017-05-26 07:44:30', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (509, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01573, '2017-05-26 07:45:38', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (510, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01578, '2017-05-26 07:46:47', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (511, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.0157, '2017-05-26 07:47:55', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (512, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01567, '2017-05-26 07:49:08', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (513, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01571, '2017-05-26 07:50:17', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (514, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01572, '2017-05-26 07:51:26', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (515, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01562, '2017-05-26 07:52:34', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (516, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01566, '2017-05-26 07:53:42', 0.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (517, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01558, '2017-05-26 07:54:48', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (518, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9278, 9.0156, '2017-05-26 07:55:59', 0.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (519, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01563, '2017-05-26 07:57:08', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (520, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92774, 9.01564, '2017-05-26 07:58:16', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (521, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01575, '2017-05-26 07:59:30', 0.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (522, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01571, '2017-05-26 08:00:37', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (523, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01571, '2017-05-26 08:01:46', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (524, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92757, 9.01566, '2017-05-26 08:02:56', 0.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (525, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92782, 9.01568, '2017-05-26 08:04:05', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (526, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01562, '2017-05-26 08:05:12', 0.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (527, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01568, '2017-05-26 08:06:05', 1.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (528, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9278, 9.01558, '2017-05-26 08:07:31', 0.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (529, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92791, 9.01599, '2017-05-26 08:08:40', 0.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (530, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92829, 9.01467, '2017-05-26 08:09:48', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (531, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92793, 9.01526, '2017-05-26 08:10:55', 2.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (532, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92788, 9.01563, '2017-05-26 08:11:52', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (533, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01533, '2017-05-26 08:13:13', 1.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (534, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92781, 9.01571, '2017-05-26 08:14:10', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (535, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01547, '2017-05-26 08:15:31', 1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (536, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9286, 9.01639, '2017-05-26 08:16:31', 26, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (537, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92755, 9.01624, '2017-05-26 08:17:31', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (538, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92846, 9.01642, '2017-05-26 09:12:24', 34.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (539, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92496, 9.01521, '2017-05-26 09:13:24', 39.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (540, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92183, 9.00927, '2017-05-26 09:14:24', 39.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (541, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91954, 9.00568, '2017-05-26 09:15:24', 11.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (542, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91214, 9.00446, '2017-05-26 09:16:24', 61.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (543, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90506, 9.00792, '2017-05-26 09:17:24', 18, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (544, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89677, 9.01084, '2017-05-26 09:18:24', 69.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (545, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88974, 9.01574, '2017-05-26 09:19:24', 30.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (546, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88898, 9.02144, '2017-05-26 09:20:24', 36.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (547, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88954, 9.02449, '2017-05-26 09:21:24', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (548, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8904, 9.02509, '2017-05-26 09:22:53', 39.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (549, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89156, 9.0296, '2017-05-26 09:23:53', 1.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (550, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89065, 9.03021, '2017-05-26 09:27:05', 38.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (551, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89284, 9.03909, '2017-05-26 09:28:05', 32.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (552, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89526, 9.04649, '2017-05-26 09:29:05', 44.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (553, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90184, 9.05452, '2017-05-26 09:30:05', 65.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (554, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90699, 9.058, '2017-05-26 09:31:05', 37.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (555, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90826, 9.06, '2017-05-26 09:32:05', 0.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (556, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91016, 9.06066, '2017-05-26 09:33:05', 34.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (557, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91514, 9.0661, '2017-05-26 09:34:05', 50.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (558, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91791, 9.07479, '2017-05-26 09:35:05', 36.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (559, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91767, 9.08152, '2017-05-26 09:36:05', 27.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (560, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91673, 9.09231, '2017-05-26 09:37:05', 78.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (561, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91473, 9.11184, '2017-05-26 09:38:05', 80.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (562, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9136, 9.12592, '2017-05-26 09:39:05', 46.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (563, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91376, 9.13581, '2017-05-26 09:40:05', 15.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (564, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91485, 9.1399, '2017-05-26 09:41:05', 1.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (565, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91624, 9.14155, '2017-05-26 09:42:05', 6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (566, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91722, 9.14159, '2017-05-26 09:44:05', 37.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (567, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91639, 9.13586, '2017-05-26 09:45:05', 41.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (568, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91408, 9.12998, '2017-05-26 09:46:05', 42.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (569, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91371, 9.11936, '2017-05-26 09:47:05', 59.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (570, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91105, 9.11803, '2017-05-26 09:48:05', 82.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (571, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89779, 9.12849, '2017-05-26 09:49:05', 107.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (572, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88357, 9.13975, '2017-05-26 09:50:05', 105.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (573, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8696, 9.15076, '2017-05-26 09:51:05', 97.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (574, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86222, 9.15539, '2017-05-26 09:52:05', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (575, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85916, 9.1557, '2017-05-26 09:53:05', 85.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (576, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84574, 9.15176, '2017-05-26 09:54:05', 94.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (577, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83764, 9.14515, '2017-05-26 09:55:05', 11.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (578, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83688, 9.15506, '2017-05-26 09:56:05', 52.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (579, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83646, 9.1577, '2017-05-26 09:57:05', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (580, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8321, 9.15688, '2017-05-26 09:58:05', 65.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (581, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82309, 9.15496, '2017-05-26 09:59:05', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (582, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82138, 9.15582, '2017-05-26 10:00:05', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (583, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82253, 9.15526, '2017-05-26 10:01:05', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (584, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82603, 9.15262, '2017-05-26 10:02:05', 14.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (585, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82662, 9.15153, '2017-05-26 10:04:43', 21.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (586, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82605, 9.14812, '2017-05-26 10:05:43', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (587, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82693, 9.14787, '2017-05-26 10:06:51', 37.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (588, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83175, 9.14789, '2017-05-26 10:07:51', 20.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (589, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83078, 9.14782, '2017-05-26 10:14:36', 31.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (590, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82631, 9.14785, '2017-05-26 10:15:36', 7.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (591, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82608, 9.15261, '2017-05-26 10:16:36', 30.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (592, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82142, 9.15571, '2017-05-26 10:17:36', 53.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (593, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81136, 9.15725, '2017-05-26 10:18:36', 58.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (594, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80612, 9.16114, '2017-05-26 10:19:36', 35.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (595, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80012, 9.16464, '2017-05-26 10:20:36', 40.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (596, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79396, 9.1689, '2017-05-26 10:21:36', 58, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (597, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78765, 9.17883, '2017-05-26 10:22:36', 46.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (598, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78319, 9.18226, '2017-05-26 10:23:36', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (599, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78195, 9.18073, '2017-05-26 10:24:36', 14.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (600, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77958, 9.1834, '2017-05-26 10:25:36', 27.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (601, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77471, 9.18418, '2017-05-26 10:26:36', 37.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (602, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76866, 9.18402, '2017-05-26 10:27:36', 46.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (603, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76831, 9.18966, '2017-05-26 10:28:36', 32.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (604, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76962, 9.1898, '2017-05-26 10:29:36', 1.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (605, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76887, 9.19078, '2017-05-26 10:31:30', 37.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (606, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76559, 9.19333, '2017-05-26 10:32:30', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (607, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76469, 9.19363, '2017-05-26 10:34:21', 26.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (608, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76443, 9.19222, '2017-05-26 10:35:52', 29.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (609, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76484, 9.18601, '2017-05-26 10:36:52', 33.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (610, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76069, 9.18278, '2017-05-26 10:37:52', 50, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (611, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74972, 9.18059, '2017-05-26 10:38:52', 87.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (612, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73828, 9.18999, '2017-05-26 10:39:52', 91.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (613, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73072, 9.20535, '2017-05-26 10:40:52', 86.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (614, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73171, 9.21506, '2017-05-26 10:41:52', 31.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (615, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73291, 9.22356, '2017-05-26 10:42:52', 43.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (616, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73159, 9.23128, '2017-05-26 10:43:52', 23.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (617, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72754, 9.22749, '2017-05-26 10:44:52', 24, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (618, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72566, 9.22398, '2017-05-26 10:45:52', 21.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (619, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72257, 9.22827, '2017-05-26 10:46:52', 36.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (620, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71725, 9.2353, '2017-05-26 10:47:52', 72.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (621, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71329, 9.23504, '2017-05-26 10:48:52', 7.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (622, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71349, 9.2365, '2017-05-26 10:50:58', 35.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (623, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7192, 9.23314, '2017-05-26 10:51:58', 71.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (624, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71889, 9.22662, '2017-05-26 10:52:58', 22.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (625, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71576, 9.21959, '2017-05-26 10:53:58', 29.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (626, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71478, 9.22098, '2017-05-26 10:54:58', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (627, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71553, 9.22, '2017-05-26 10:57:21', 38, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (628, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71063, 9.21803, '2017-05-26 10:58:21', 66.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (629, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70076, 9.21975, '2017-05-26 10:59:21', 34.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (630, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69991, 9.21908, '2017-05-26 11:02:16', 37.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (631, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70523, 9.20968, '2017-05-26 11:03:16', 70.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (632, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70516, 9.20509, '2017-05-26 11:04:16', 51.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (633, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70389, 9.20413, '2017-05-26 11:05:16', 50.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (634, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69809, 9.19619, '2017-05-26 11:06:16', 75.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (635, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69113, 9.18687, '2017-05-26 11:07:16', 30.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (636, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69168, 9.18806, '2017-05-26 11:11:05', 38.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (637, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69915, 9.19724, '2017-05-26 11:12:05', 75, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (638, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70245, 9.20894, '2017-05-26 11:13:05', 36.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (639, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70718, 9.20147, '2017-05-26 11:14:05', 9.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (640, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70199, 9.21305, '2017-05-26 11:15:05', 65.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (641, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69978, 9.2208, '2017-05-26 11:16:05', 39.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (642, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70864, 9.219, '2017-05-26 11:17:05', 60.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (643, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70863, 9.22761, '2017-05-26 11:18:05', 59.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (644, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70419, 9.23682, '2017-05-26 11:19:05', 12.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (645, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70472, 9.2355, '2017-05-26 11:21:22', 48.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (646, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70998, 9.22322, '2017-05-26 11:22:22', 74.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (647, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71411, 9.21257, '2017-05-26 11:23:22', 70.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (648, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72099, 9.20361, '2017-05-26 11:24:22', 68.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (649, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72945, 9.19363, '2017-05-26 11:25:22', 75.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (650, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73803, 9.18277, '2017-05-26 11:26:22', 70.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (651, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74745, 9.17628, '2017-05-26 11:27:22', 71.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (652, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75871, 9.17577, '2017-05-26 11:28:22', 75, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (653, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76964, 9.176, '2017-05-26 11:29:22', 76.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (654, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78004, 9.16234, '2017-05-26 11:30:22', 102.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (655, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79053, 9.14617, '2017-05-26 11:31:22', 98.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (656, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8049, 9.13804, '2017-05-26 11:32:22', 99.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (657, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81892, 9.13494, '2017-05-26 11:33:22', 96.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (658, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83272, 9.14146, '2017-05-26 11:34:22', 91.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (659, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84634, 9.15221, '2017-05-26 11:35:22', 102.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (660, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86145, 9.15606, '2017-05-26 11:36:22', 95.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (661, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8748, 9.14696, '2017-05-26 11:37:22', 100.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (662, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88748, 9.13696, '2017-05-26 11:38:22', 95.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (663, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89985, 9.12715, '2017-05-26 11:39:22', 94.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (664, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91252, 9.11713, '2017-05-26 11:40:22', 89.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (665, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92468, 9.10748, '2017-05-26 11:41:22', 82.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (666, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93564, 9.09678, '2017-05-26 11:42:22', 85.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (667, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93849, 9.09155, '2017-05-26 11:43:22', 60.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (668, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9322, 9.08773, '2017-05-26 11:44:22', 58.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (669, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92896, 9.08136, '2017-05-26 11:45:22', 52.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (670, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92929, 9.07119, '2017-05-26 11:46:22', 51.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (671, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92544, 9.06068, '2017-05-26 11:47:22', 60.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (672, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92194, 9.05159, '2017-05-26 11:48:22', 77.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (673, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92447, 9.04088, '2017-05-26 11:49:22', 49.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (674, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92442, 9.02817, '2017-05-26 11:50:22', 53.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (675, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92299, 9.01504, '2017-05-26 11:51:22', 46.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (676, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01561, '2017-05-26 11:52:22', 11.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (677, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01565, '2017-05-26 12:53:43', 1.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (678, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01581, '2017-05-26 12:54:52', 1.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (679, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01572, '2017-05-26 12:56:03', 1.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (680, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01576, '2017-05-26 12:57:12', 2.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (681, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01576, '2017-05-26 12:58:20', 1.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (682, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01582, '2017-05-26 12:59:29', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (683, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01591, '2017-05-26 13:00:36', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (684, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01591, '2017-05-26 13:01:45', 1.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (685, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01577, '2017-05-26 13:02:54', 1.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (686, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01578, '2017-05-26 13:04:07', 1.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (687, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01585, '2017-05-26 13:05:16', 1.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (688, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01571, '2017-05-26 13:06:24', 1.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (689, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01574, '2017-05-26 13:07:32', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (690, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01574, '2017-05-26 13:08:44', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (691, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01573, '2017-05-26 13:09:52', 0.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (692, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01558, '2017-05-26 13:11:04', 1.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (693, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92744, 9.01574, '2017-05-26 13:12:29', 2.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (694, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01566, '2017-05-26 13:12:55', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (695, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92732, 9.01568, '2017-05-26 13:14:04', 1.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (696, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01576, '2017-05-26 13:15:13', 1.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (697, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01575, '2017-05-26 13:16:22', 0.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (698, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01578, '2017-05-26 13:17:32', 1.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (699, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01567, '2017-05-26 13:18:40', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (700, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92762, 9.01572, '2017-05-26 13:19:50', 1.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (701, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01572, '2017-05-26 13:20:59', 1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (702, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01557, '2017-05-26 13:22:26', 3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (703, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01569, '2017-05-26 13:23:38', 0.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (704, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01572, '2017-05-26 13:24:46', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (705, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92745, 9.0157, '2017-05-26 13:25:53', 3.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (706, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01567, '2017-05-26 13:27:03', 1.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (707, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01567, '2017-05-26 13:28:17', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (708, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01569, '2017-05-26 13:29:24', 0.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (709, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01577, '2017-05-26 13:30:33', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (710, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92831, 9.01567, '2017-05-26 13:31:42', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (711, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01568, '2017-05-26 13:32:50', 1.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (712, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92755, 9.01566, '2017-05-26 13:33:59', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (713, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01569, '2017-05-26 13:35:08', 0.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (714, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01568, '2017-05-26 13:36:21', 1.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (715, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01563, '2017-05-26 13:37:28', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (716, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01571, '2017-05-26 13:38:43', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (717, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.0157, '2017-05-26 13:39:50', 0.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (718, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.0157, '2017-05-26 13:41:05', 0.8, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (719, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01544, '2017-05-26 13:42:12', 1.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (720, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01524, '2017-05-26 13:43:21', 2.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (721, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.01563, '2017-05-26 13:44:29', 1.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (722, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01631, '2017-05-26 13:45:42', 3.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (723, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.0157, '2017-05-26 13:46:50', 1.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (724, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.0157, '2017-05-26 13:47:58', 2.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (725, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9278, 9.01561, '2017-05-26 13:49:06', 2.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (726, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.0157, '2017-05-26 13:50:20', 3.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (727, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01579, '2017-05-26 13:51:33', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (728, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.0157, '2017-05-26 13:52:43', 1.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (729, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.0158, '2017-05-26 13:53:51', 0.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (730, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.01568, '2017-05-26 13:54:58', 1.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (731, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01561, '2017-05-26 13:56:07', 0.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (732, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92687, 9.01476, '2017-05-26 16:26:45', 13.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (733, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01574, '2017-05-26 16:28:03', 1.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (734, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92837, 9.01524, '2017-05-26 17:11:40', 20.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (735, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92743, 9.01585, '2017-05-26 17:12:40', 3.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (736, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92823, 9.01518, '2017-05-26 17:15:21', 16.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (737, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92737, 9.0158, '2017-05-26 17:18:44', 3.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (738, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92807, 9.01476, '2017-05-26 17:29:49', 3.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (739, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01596, '2017-05-26 17:30:49', 2.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (740, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92796, 9.01467, '2017-05-26 17:37:53', 26.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (741, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01601, '2017-05-26 17:40:13', 6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (742, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.0146, '2017-05-26 17:53:24', 14.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (743, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.0184, '2017-05-26 17:55:30', 9.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (744, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01562, '2017-05-26 17:57:24', 32.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (745, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01748, '2017-05-26 18:06:45', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (746, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01593, '2017-05-26 18:16:07', 5.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (747, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92712, 9.01486, '2017-05-26 18:20:09', 11.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (748, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92667, 9.01915, '2017-05-26 18:21:13', 5.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (749, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92699, 9.01775, '2017-05-26 18:23:23', 9.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (750, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01633, '2017-05-26 18:24:58', 12.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (751, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.0148, '2017-05-26 18:28:21', 15, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (752, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9274, 9.01676, '2017-05-26 18:32:46', 2.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (753, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01539, '2017-05-26 18:36:34', 4.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (754, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.0168, '2017-05-26 18:42:42', 17.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (755, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01533, '2017-05-26 18:43:53', 15.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (756, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92761, 9.01387, '2017-05-26 18:46:21', 10.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (757, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01529, '2017-05-26 18:47:32', 3.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (758, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92739, 9.01388, '2017-05-26 18:49:06', 33.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (759, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92774, 9.01647, '2017-05-26 18:50:28', 13, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (760, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.01854, '2017-05-26 18:52:25', 38.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (761, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92761, 9.01607, '2017-05-26 18:53:25', 5.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (762, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.927, 9.01459, '2017-05-26 18:55:05', 9.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (763, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.927, 9.01316, '2017-05-26 18:56:43', 19.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (764, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92737, 9.01572, '2017-05-26 22:41:14', 7.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (765, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01385, '2017-05-26 23:52:59', 3.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (766, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.01527, '2017-05-26 23:54:27', 4.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (767, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92818, 9.01138, '2017-05-27 02:11:55', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (768, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92781, 9.01362, '2017-05-27 02:13:06', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (769, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92761, 9.0153, '2017-05-27 02:15:47', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (770, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92805, 9.01199, '2017-05-27 02:19:13', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (771, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92797, 9.0142, '2017-05-27 02:20:50', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (772, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01581, '2017-05-27 02:21:51', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (773, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01779, '2017-05-27 02:22:51', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (774, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92745, 9.01619, '2017-05-27 02:26:28', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (775, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92781, 9.0148, '2017-05-27 02:27:36', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (776, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01662, '2017-05-27 02:28:39', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (777, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01517, '2017-05-27 02:29:40', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (778, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.0177, '2017-05-27 02:30:41', 4.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (779, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01524, '2017-05-27 02:31:41', 10.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (780, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92735, 9.01675, '2017-05-27 02:32:41', 15.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (781, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01425, '2017-05-27 02:33:41', 27.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (782, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.01583, '2017-05-27 02:34:41', 5.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (783, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9279, 9.01399, '2017-05-27 02:36:06', 19.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (784, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01666, '2017-05-27 02:37:06', 10.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (785, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.01522, '2017-05-27 02:38:06', 6.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (786, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92734, 9.01691, '2017-05-27 02:39:06', 11.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (787, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01478, '2017-05-27 02:40:35', 35.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (788, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92787, 9.01293, '2017-05-27 02:41:35', 8.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (789, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01507, '2017-05-27 02:42:43', 6.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (790, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92739, 9.01689, '2017-05-27 02:45:01', 19, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (791, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01516, '2017-05-27 02:46:01', 28.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (792, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92734, 9.01714, '2017-05-27 02:51:18', 18.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (793, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92783, 9.01549, '2017-05-27 02:52:18', 5.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (794, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01408, '2017-05-27 02:54:40', 7.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (795, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.01631, '2017-05-27 02:55:45', 21.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (796, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01813, '2017-05-27 02:57:40', 4.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (797, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92753, 9.01663, '2017-05-27 02:58:40', 1.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (798, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92702, 9.01837, '2017-05-27 02:59:43', 34.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (799, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01627, '2017-05-27 03:05:12', 7.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (800, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9286, 9.01452, '2017-05-27 03:06:43', 22.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (801, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01562, '2017-05-27 05:56:31', 6.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (802, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92679, 9.01592, '2017-05-27 06:49:21', 20.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (803, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92769, 9.01608, '2017-05-27 06:50:23', 3.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (804, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92699, 9.01511, '2017-05-27 07:02:11', 34.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (805, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9225, 9.01205, '2017-05-27 07:03:11', 46.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (806, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92226, 9.00808, '2017-05-27 07:04:11', 8.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (807, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92238, 9.00954, '2017-05-27 07:12:19', 31.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (808, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92333, 9.01992, '2017-05-27 07:13:19', 98.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (809, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9245, 9.04072, '2017-05-27 07:14:19', 89.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (810, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92137, 9.0577, '2017-05-27 07:15:19', 73.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (811, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91257, 9.06259, '2017-05-27 07:16:19', 47.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (812, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90867, 9.05898, '2017-05-27 07:17:19', 0.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (813, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90939, 9.05989, '2017-05-27 07:24:55', 19.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (814, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91502, 9.06151, '2017-05-27 07:25:55', 70.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (815, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92359, 9.05947, '2017-05-27 07:26:55', 83.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (816, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9292, 9.07357, '2017-05-27 07:27:55', 73.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (817, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93043, 9.08624, '2017-05-27 07:28:55', 84.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (818, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93783, 9.09179, '2017-05-27 07:29:55', 69.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (819, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92304, 9.10854, '2017-05-27 07:30:55', 143.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (820, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90367, 9.12395, '2017-05-27 07:31:55', 145.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (821, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88424, 9.13928, '2017-05-27 07:32:55', 143.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (822, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86822, 9.15192, '2017-05-27 07:33:55', 127, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (823, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85333, 9.15413, '2017-05-27 07:34:55', 76, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (824, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84161, 9.15019, '2017-05-27 07:35:55', 76, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (825, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83761, 9.14715, '2017-05-27 07:36:55', 32.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (826, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83652, 9.15774, '2017-05-27 07:37:55', 15.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (827, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83852, 9.15848, '2017-05-27 07:38:55', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (828, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84166, 9.15863, '2017-05-27 07:39:55', 43.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (829, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84462, 9.1617, '2017-05-27 07:40:55', 31.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (830, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84604, 9.16372, '2017-05-27 07:41:55', 25.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (831, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84779, 9.16463, '2017-05-27 07:42:55', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (832, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84841, 9.16576, '2017-05-27 07:50:11', 38.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (833, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84534, 9.17066, '2017-05-27 07:51:11', 28.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (834, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8417, 9.17499, '2017-05-27 07:52:11', 42.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (835, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83652, 9.19082, '2017-05-27 07:53:11', 78, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (836, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83058, 9.20354, '2017-05-27 07:54:11', 48.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (837, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82891, 9.21399, '2017-05-27 07:55:11', 52.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (838, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82068, 9.22209, '2017-05-27 07:56:11', 81.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (839, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81528, 9.2406, '2017-05-27 07:57:11', 64.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (840, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80514, 9.2434, '2017-05-27 07:58:11', 90.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (841, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79731, 9.25749, '2017-05-27 07:59:11', 92.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (842, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79265, 9.26613, '2017-05-27 08:00:11', 21, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (843, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78925, 9.2613, '2017-05-27 08:01:11', 49.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (844, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77934, 9.25364, '2017-05-27 08:02:11', 94.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (845, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77303, 9.24682, '2017-05-27 08:03:11', 27.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (846, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76819, 9.24749, '2017-05-27 08:04:11', 65.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (847, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75762, 9.24398, '2017-05-27 08:05:11', 84.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (848, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74634, 9.23706, '2017-05-27 08:06:11', 92.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (849, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73564, 9.23313, '2017-05-27 08:07:11', 72.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (850, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73366, 9.23151, '2017-05-27 08:08:11', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (851, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.733, 9.23041, '2017-05-27 08:09:45', 34.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (852, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72929, 9.2236, '2017-05-27 08:10:45', 23.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (853, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72679, 9.22205, '2017-05-27 08:11:45', 0.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (854, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72768, 9.22187, '2017-05-27 08:13:05', 17.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (855, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72799, 9.22509, '2017-05-27 08:14:05', 32.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (856, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72628, 9.22281, '2017-05-27 08:15:05', 5.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (857, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72718, 9.22278, '2017-05-27 08:17:45', 0.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (858, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72726, 9.22422, '2017-05-27 08:24:40', 19.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (859, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72681, 9.22607, '2017-05-27 08:25:40', 4.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (860, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72477, 9.22532, '2017-05-27 08:26:40', 23.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (861, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71961, 9.23251, '2017-05-27 08:27:40', 77.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (862, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71008, 9.24043, '2017-05-27 08:28:40', 68.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (863, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70663, 9.24666, '2017-05-27 08:29:40', 56, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (864, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71035, 9.25363, '2017-05-27 08:30:40', 3.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (865, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7108, 9.25239, '2017-05-27 08:39:57', 10.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (866, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70913, 9.2514, '2017-05-27 08:40:57', 37.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (867, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70603, 9.24405, '2017-05-27 08:41:57', 31.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (868, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71514, 9.2374, '2017-05-27 08:42:57', 53, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (869, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71876, 9.22963, '2017-05-27 08:43:57', 75.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (870, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71524, 9.22045, '2017-05-27 08:44:57', 54.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (871, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71346, 9.21776, '2017-05-27 08:45:57', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (872, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7153, 9.21765, '2017-05-27 08:46:57', 36.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (873, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71969, 9.21009, '2017-05-27 08:47:57', 34.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (874, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72562, 9.19966, '2017-05-27 08:48:57', 27.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (875, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73467, 9.1868, '2017-05-27 08:49:57', 90.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (876, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74482, 9.17752, '2017-05-27 08:50:57', 80.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (877, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75624, 9.17522, '2017-05-27 08:51:57', 73, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (878, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76745, 9.17701, '2017-05-27 08:52:57', 74.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (879, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78, 9.16241, '2017-05-27 08:53:57', 135.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (880, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79623, 9.14375, '2017-05-27 08:54:57', 124.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (881, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81366, 9.13465, '2017-05-27 08:55:57', 137.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (882, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8323, 9.14076, '2017-05-27 08:56:57', 119.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (883, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84781, 9.15261, '2017-05-27 08:57:57', 130.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (884, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86319, 9.15561, '2017-05-27 08:58:57', 91.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (885, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8771, 9.14508, '2017-05-27 08:59:57', 92.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (886, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89072, 9.13431, '2017-05-27 09:00:57', 104.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (887, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90705, 9.12136, '2017-05-27 09:01:57', 145, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (888, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92472, 9.10743, '2017-05-27 09:02:57', 135.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (889, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93829, 9.09331, '2017-05-27 09:03:57', 64.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (890, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.94407, 9.09833, '2017-05-27 09:04:57', 32.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (891, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9483, 9.1053, '2017-05-27 09:05:57', 55.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (892, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95406, 9.11622, '2017-05-27 09:06:57', 44.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (893, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95821, 9.12277, '2017-05-27 09:07:57', 45, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (894, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96292, 9.12956, '2017-05-27 09:08:57', 37.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (895, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96748, 9.1351, '2017-05-27 09:09:57', 19, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (896, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96547, 9.1404, '2017-05-27 09:10:57', 47.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (897, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96794, 9.15209, '2017-05-27 09:11:57', 59.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (898, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97407, 9.16057, '2017-05-27 09:12:57', 49.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (899, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9805, 9.15673, '2017-05-27 09:13:57', 40.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (900, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.98096, 9.15406, '2017-05-27 09:14:57', 29.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (901, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97916, 9.14877, '2017-05-27 09:15:57', 2.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (902, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97958, 9.14742, '2017-05-27 09:16:59', 22.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (903, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97975, 9.146, '2017-05-27 09:25:43', 28.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (904, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.98002, 9.14246, '2017-05-27 09:26:43', 19, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (905, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97982, 9.13455, '2017-05-27 09:27:43', 53.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (906, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97984, 9.12868, '2017-05-27 09:28:43', 4.5, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (907, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97697, 9.12765, '2017-05-27 09:29:43', 57.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (908, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96944, 9.13349, '2017-05-27 09:30:43', 64.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (909, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96575, 9.14219, '2017-05-27 09:31:43', 41.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (910, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96709, 9.14255, '2017-05-27 09:32:43', 2.2, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (911, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96617, 9.14255, '2017-05-27 09:46:08', 15.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (912, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96525, 9.14281, '2017-05-27 09:47:30', 39.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (913, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96327, 9.15007, '2017-05-27 09:48:30', 35.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (914, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95997, 9.15619, '2017-05-27 09:49:30', 45.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (915, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95688, 9.16087, '2017-05-27 09:50:30', 33.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (916, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95522, 9.16444, '2017-05-27 09:51:30', 8.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (917, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95084, 9.16593, '2017-05-27 09:52:30', 11, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (918, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95173, 9.16568, '2017-05-27 09:56:41', 9.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (919, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95101, 9.16655, '2017-05-27 09:58:54', 6.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (920, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95177, 9.16731, '2017-05-27 10:02:36', 35.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (921, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.95472, 9.16447, '2017-05-27 10:03:36', 23.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (922, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9589, 9.1626, '2017-05-27 10:04:36', 29.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (923, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96005, 9.15604, '2017-05-27 10:05:36', 22.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (924, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96312, 9.15179, '2017-05-27 10:06:36', 29.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (925, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9646, 9.14695, '2017-05-27 10:07:36', 0.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (926, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96579, 9.14163, '2017-05-27 10:08:36', 21.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (927, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.96792, 9.1358, '2017-05-27 10:09:36', 68.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (928, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.97558, 9.12835, '2017-05-27 10:10:36', 58.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (929, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.98073, 9.12423, '2017-05-27 10:11:36', 50.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (930, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.98133, 9.10639, '2017-05-27 10:12:36', 90.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (931, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.98877, 9.08463, '2017-05-27 10:13:36', 112.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (932, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.99341, 9.07157, '2017-05-27 10:14:36', 43.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (933, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.99746, 9.06571, '2017-05-27 10:15:36', 51.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (934, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.00317, 9.05851, '2017-05-27 10:16:36', 52.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (935, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.00812, 9.05585, '2017-05-27 10:17:36', 27.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (936, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.01739, 9.05068, '2017-05-27 10:18:36', 79.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (937, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.02323, 9.04746, '2017-05-27 10:19:36', 62.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (938, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.03242, 9.04045, '2017-05-27 10:20:36', 69.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (939, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.03858, 9.03497, '2017-05-27 10:21:36', 47.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (940, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.04389, 9.02715, '2017-05-27 10:22:36', 51, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (941, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.05027, 9.01904, '2017-05-27 10:23:36', 73.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (942, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.05108, 9.01403, '2017-05-27 10:24:36', 39.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (943, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.05059, 9.01286, '2017-05-27 10:26:01', 2.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (944, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.05047, 9.01145, '2017-05-27 10:29:19', 15.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (945, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.0556, 9.01274, '2017-05-27 10:30:19', 43.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (946, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.04842, 9.02042, '2017-05-27 10:31:19', 65.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (947, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.04297, 9.02874, '2017-05-27 10:32:19', 40.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (948, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.03814, 9.03526, '2017-05-27 10:33:19', 54.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (949, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 50.03189, 9.04086, '2017-05-27 10:34:19', 60.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (950, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92774, 9.01576, '2017-05-29 07:45:03', 0.3, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (951, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92893, 9.01635, '2017-05-29 07:46:03', 17.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (952, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.01626, '2017-05-29 07:47:03', 9.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (953, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92874, 9.01652, '2017-05-29 07:48:03', 1.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (954, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92782, 9.01624, '2017-05-29 07:59:48', 13.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (955, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92879, 9.0164, '2017-05-29 08:41:28', 25.4, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (956, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92425, 9.01475, '2017-05-29 08:42:28', 39.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (957, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92383, 9.02525, '2017-05-29 08:43:28', 89.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (958, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92404, 9.04238, '2017-05-29 08:44:28', 36.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (959, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92138, 9.05709, '2017-05-29 08:45:28', 66.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (960, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91406, 9.06215, '2017-05-29 08:46:28', 62.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (961, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90917, 9.0596, '2017-05-29 08:47:28', 16.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (962, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90902, 9.05816, '2017-05-29 08:53:07', 27.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (963, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9111, 9.06161, '2017-05-29 08:54:07', 43.9, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (964, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91615, 9.06812, '2017-05-29 08:55:07', 22.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (965, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91786, 9.07591, '2017-05-29 08:56:07', 25.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (966, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91878, 9.082, '2017-05-29 08:57:07', 22.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (967, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92011, 9.08233, '2017-05-29 08:58:07', 0.1, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (968, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91921, 9.08237, '2017-05-29 09:00:48', 23.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (969, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9171, 9.08642, '2017-05-29 09:01:48', 36.7, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (970, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91639, 9.09543, '2017-05-29 09:02:48', 65.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (971, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91489, 9.11069, '2017-05-29 09:03:48', 67.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (972, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90906, 9.11961, '2017-05-29 09:04:48', 88.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (973, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89619, 9.12976, '2017-05-29 09:05:48', 99.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (974, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88183, 9.14111, '2017-05-29 09:06:48', 99.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (975, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87047, 9.15004, '2017-05-29 09:07:48', 80.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (976, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85924, 9.15571, '2017-05-29 09:08:48', 68.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (977, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84768, 9.15236, '2017-05-29 09:09:48', 83.5, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (978, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83737, 9.14678, '2017-05-29 09:10:48', 47.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (979, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8389, 9.14316, '2017-05-29 09:11:48', 31.8, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (980, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83803, 9.1435, '2017-05-29 09:17:22', 9.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (981, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83259, 9.13887, '2017-05-29 09:18:22', 47.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (982, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83357, 9.12983, '2017-05-29 09:19:22', 51.2, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (983, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83526, 9.11902, '2017-05-29 09:20:22', 42.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (984, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83696, 9.10733, '2017-05-29 09:21:22', 73, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (985, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84368, 9.09433, '2017-05-29 09:22:22', 65.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (986, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85002, 9.08972, '2017-05-29 09:23:22', 39.1, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (987, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85472, 9.08639, '2017-05-29 09:24:22', 21.7, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (988, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8516, 9.08276, '2017-05-29 09:25:22', 7.5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (989, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85221, 9.08386, '2017-05-29 09:27:41', 23.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (990, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85255, 9.08862, '2017-05-29 09:28:41', 50.8, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (991, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84516, 9.09201, '2017-05-29 09:29:41', 62.4, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (992, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83795, 9.10368, '2017-05-29 09:30:41', 67.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (993, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83558, 9.11713, '2017-05-29 09:31:41', 50.6, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (994, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83398, 9.12741, '2017-05-29 09:32:41', 10.3, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (995, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8312, 9.1389, '2017-05-29 09:33:41', 63.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (996, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83756, 9.14804, '2017-05-29 09:34:41', 1.6, NULL,
             NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (997, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8366, 9.15722, '2017-05-29 09:35:41', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (998, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83852, 9.15849, '2017-05-29 09:36:41', 36, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (999, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84408, 9.16004, '2017-05-29 09:37:41', 27.9, NULL,
        NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1000, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84313, 9.16589, '2017-05-29 09:38:41', 50.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1001, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84638, 9.16815, '2017-05-29 09:39:41', 39.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1002, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84775, 9.1646, '2017-05-29 09:40:41', 8.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1003, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84768, 9.16464, '2017-05-29 09:44:01', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1004, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84834, 9.1658, '2017-05-29 09:46:27', 53.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1005, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84541, 9.17066, '2017-05-29 09:47:27', 17.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1006, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84195, 9.17398, '2017-05-29 09:48:27', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1007, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83872, 9.18346, '2017-05-29 09:49:27', 48.4, NULL,
         NULL, NULL);
/*
-- Query: select * from tad_node_geoposition where position_id>1000
LIMIT 0, 1000

-- Date: 2017-06-01 11:31
*/
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1008, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83582, 9.19413, '2017-05-29 09:50:27', 52, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1009, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83067, 9.20335, '2017-05-29 09:51:27', 43.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1010, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83017, 9.21152, '2017-05-29 09:52:27', 38.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1011, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82336, 9.21797, '2017-05-29 09:53:27', 24.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1012, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81832, 9.22879, '2017-05-29 09:54:27', 92.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1013, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81668, 9.24739, '2017-05-29 09:55:27', 61.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1014, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82217, 9.25499, '2017-05-29 09:56:27', 24.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1015, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82271, 9.25387, '2017-05-29 09:57:44', 19.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1016, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82759, 9.25653, '2017-05-29 09:58:44', 50.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1017, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8346, 9.26702, '2017-05-29 09:59:44', 69.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1018, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84356, 9.27318, '2017-05-29 10:00:44', 66.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1019, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84781, 9.27504, '2017-05-29 10:01:44', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1020, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8471, 9.27394, '2017-05-29 10:18:27', 53.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1021, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83494, 9.26826, '2017-05-29 10:19:27', 90.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1022, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82828, 9.25756, '2017-05-29 10:20:27', 42.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1023, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8231, 9.25305, '2017-05-29 10:21:27', 35.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1024, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81779, 9.24987, '2017-05-29 10:22:27', 50.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1025, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81431, 9.24151, '2017-05-29 10:23:27', 51, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1026, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80521, 9.2434, '2017-05-29 10:24:27', 64.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1027, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79859, 9.25579, '2017-05-29 10:25:27', 65.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1028, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79278, 9.26582, '2017-05-29 10:26:27', 44.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1029, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78854, 9.26112, '2017-05-29 10:27:27', 57, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1030, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77897, 9.25296, '2017-05-29 10:28:27', 78, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1031, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77492, 9.2484, '2017-05-29 10:29:27', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1032, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77412, 9.24754, '2017-05-29 10:32:01', 43.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1033, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76988, 9.24752, '2017-05-29 10:33:01', 38.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1034, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76234, 9.24521, '2017-05-29 10:34:01', 62.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1035, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75213, 9.24302, '2017-05-29 10:35:01', 71, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1036, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74309, 9.23442, '2017-05-29 10:36:01', 72.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1037, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73508, 9.2328, '2017-05-29 10:37:01', 60.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1038, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73157, 9.23125, '2017-05-29 10:38:01', 19.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1039, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7276, 9.22757, '2017-05-29 10:39:01', 31.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1040, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72497, 9.22497, '2017-05-29 10:40:01', 29.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1041, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72184, 9.22939, '2017-05-29 10:41:01', 27, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1042, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71547, 9.237, '2017-05-29 10:42:01', 65.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1043, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71395, 9.23954, '2017-05-29 10:43:01', 35.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1044, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71447, 9.23838, '2017-05-29 10:44:01', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1045, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71244, 9.24061, '2017-05-29 10:45:01', 26.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1046, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70693, 9.24231, '2017-05-29 10:46:01', 59.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1047, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70457, 9.2508, '2017-05-29 10:47:01', 1.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1048, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70096, 9.25506, '2017-05-29 10:48:01', 41.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1049, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69979, 9.2485, '2017-05-29 10:49:01', 52.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1050, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70236, 9.24003, '2017-05-29 10:50:01', 41.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1051, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70763, 9.229, '2017-05-29 10:51:01', 83.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1052, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71251, 9.21584, '2017-05-29 10:52:01', 67.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1053, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71916, 9.20389, '2017-05-29 10:53:01', 73.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1054, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72805, 9.19553, '2017-05-29 10:54:01', 84, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1055, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73614, 9.185, '2017-05-29 10:55:01', 69.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1056, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74488, 9.1775, '2017-05-29 10:56:01', 64.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1057, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75532, 9.17505, '2017-05-29 10:57:01', 68.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1058, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76575, 9.17696, '2017-05-29 10:58:01', 65.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1059, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77666, 9.16736, '2017-05-29 10:59:01', 98.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1060, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78606, 9.14976, '2017-05-29 11:00:01', 105.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1061, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80152, 9.14113, '2017-05-29 11:01:01', 112.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1062, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81631, 9.1348, '2017-05-29 11:02:01', 103.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1063, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83159, 9.14003, '2017-05-29 11:03:01', 107, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1064, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84567, 9.15198, '2017-05-29 11:04:01', 111.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1065, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86272, 9.15579, '2017-05-29 11:05:01', 114.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1066, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87814, 9.14421, '2017-05-29 11:06:01', 117.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1067, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8945, 9.13134, '2017-05-29 11:07:01', 124.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1068, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90995, 9.11913, '2017-05-29 11:08:01', 108.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1069, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92461, 9.10752, '2017-05-29 11:09:01', 111.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1070, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93755, 9.09354, '2017-05-29 11:10:01', 78.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1071, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93673, 9.09077, '2017-05-29 11:11:01', 0.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1072, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92907, 9.08284, '2017-05-29 11:12:01', 84.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1073, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93136, 9.0717, '2017-05-29 11:13:01', 25.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1074, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93402, 9.07286, '2017-05-29 11:14:01', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1075, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93386, 9.07138, '2017-05-29 11:19:04', 35.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1076, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93086, 9.06709, '2017-05-29 11:20:04', 30.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1077, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92683, 9.06216, '2017-05-29 11:21:04', 83.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1078, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92228, 9.05053, '2017-05-29 11:22:04', 88.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1079, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92531, 9.0351, '2017-05-29 11:23:04', 98.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1080, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92324, 9.0191, '2017-05-29 11:24:04', 69.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1081, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9268, 9.01506, '2017-05-29 11:25:04', 43.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1082, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01576, '2017-05-29 11:26:04', 0.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1083, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92681, 9.01586, '2017-05-29 12:12:24', 11, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1084, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01571, '2017-05-29 12:17:21', 1.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1085, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89226, 9.02896, '2017-05-30 08:47:30', 10.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1086, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8914, 9.02994, '2017-05-30 08:48:43', 6.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1087, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89051, 9.0303, '2017-05-30 08:49:57', 34, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1088, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89254, 9.03504, '2017-05-30 08:50:57', 0.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1089, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89187, 9.0341, '2017-05-30 08:58:21', 18.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1090, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89312, 9.04069, '2017-05-30 08:59:21', 26.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1091, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89493, 9.04622, '2017-05-30 09:00:21', 30.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1092, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89576, 9.04688, '2017-05-30 09:03:01', 36.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1093, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89645, 9.04779, '2017-05-30 09:04:50', 32.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1094, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89726, 9.04862, '2017-05-30 09:06:03', 42.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1095, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9039, 9.05556, '2017-05-30 09:07:03', 55.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1096, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90829, 9.06016, '2017-05-30 09:08:03', 12.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1097, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91217, 9.06247, '2017-05-30 09:09:03', 42.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1098, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92079, 9.05854, '2017-05-30 09:10:03', 41.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1099, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.06369, '2017-05-30 09:11:03', 62.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1100, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93, 9.07245, '2017-05-30 09:12:03', 37.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1101, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9304, 9.07371, '2017-05-30 09:16:38', 18.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1102, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93046, 9.07232, '2017-05-30 09:23:15', 15.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1103, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9337, 9.07042, '2017-05-30 09:24:15', 22.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1104, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93321, 9.06921, '2017-05-30 09:27:25', 16, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1105, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93302, 9.0706, '2017-05-30 09:30:36', 30.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1106, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93086, 9.06691, '2017-05-30 09:31:36', 13.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1107, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9317, 9.0674, '2017-05-30 09:33:33', 0.3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1108, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9317, 9.06591, '2017-05-30 09:36:23', 51.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1109, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93904, 9.05463, '2017-05-30 09:37:23', 10, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1110, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93615, 9.04498, '2017-05-30 09:38:23', 55.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1111, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93714, 9.03935, '2017-05-30 09:39:23', 32.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1112, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93664, 9.03192, '2017-05-30 09:40:23', 36.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1113, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93894, 9.03112, '2017-05-30 09:41:23', 14.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1114, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93855, 9.02979, '2017-05-30 09:43:04', 17.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1115, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93693, 9.02963, '2017-05-30 09:44:04', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1116, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93658, 9.031, '2017-05-30 09:47:25', 30.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1117, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93717, 9.03933, '2017-05-30 09:48:25', 46.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1118, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93688, 9.04644, '2017-05-30 09:49:25', 65.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1119, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93676, 9.05889, '2017-05-30 09:50:25', 61.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1120, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92988, 9.06775, '2017-05-30 09:51:25', 43, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1121, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92924, 9.07344, '2017-05-30 09:52:25', 58.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1122, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92902, 9.08238, '2017-05-30 09:53:25', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1123, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9344, 9.0894, '2017-05-30 09:54:25', 57.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1124, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93657, 9.09075, '2017-05-30 09:55:25', 14.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1125, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93432, 9.09852, '2017-05-30 09:56:25', 79.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1126, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92315, 9.10844, '2017-05-30 09:57:25', 84.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1127, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9136, 9.11567, '2017-05-30 09:58:25', 28.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1128, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9139, 9.11728, '2017-05-30 09:59:25', 51.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1129, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91367, 9.12665, '2017-05-30 10:00:25', 32.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1130, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91428, 9.12778, '2017-05-30 10:01:25', 2.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1131, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91369, 9.12668, '2017-05-30 10:05:06', 50.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1132, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91406, 9.11655, '2017-05-30 10:06:06', 62.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1133, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91028, 9.11866, '2017-05-30 10:07:06', 76.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1134, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89871, 9.12781, '2017-05-30 10:08:06', 86.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1135, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88683, 9.13719, '2017-05-30 10:09:06', 92.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1136, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87314, 9.14799, '2017-05-30 10:10:06', 104.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1137, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85975, 9.15582, '2017-05-30 10:11:06', 86.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1138, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84685, 9.15214, '2017-05-30 10:12:06', 93, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1139, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83737, 9.14522, '2017-05-30 10:13:06', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1140, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83892, 9.14384, '2017-05-30 10:14:06', 17.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1141, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83799, 9.14352, '2017-05-30 10:16:47', 36, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1142, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83208, 9.13825, '2017-05-30 10:17:47', 29.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1143, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83367, 9.12937, '2017-05-30 10:18:47', 50.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1144, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83556, 9.11755, '2017-05-30 10:19:47', 46.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1145, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8376, 9.10533, '2017-05-30 10:20:47', 68.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1146, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84518, 9.09204, '2017-05-30 10:21:47', 66.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1147, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85134, 9.08902, '2017-05-30 10:22:47', 36.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1148, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85406, 9.08594, '2017-05-30 10:23:47', 41.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1149, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85163, 9.0826, '2017-05-30 10:24:47', 0.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1150, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85211, 9.08382, '2017-05-30 10:26:13', 28.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1151, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8524, 9.0887, '2017-05-30 10:27:13', 47.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1152, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8447, 9.09252, '2017-05-30 10:28:13', 82.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1153, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83679, 9.10766, '2017-05-30 10:29:13', 82.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1154, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83493, 9.11963, '2017-05-30 10:30:13', 55.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1155, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83269, 9.13221, '2017-05-30 10:31:13', 60.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1156, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83434, 9.14377, '2017-05-30 10:32:13', 72.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1157, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83695, 9.15443, '2017-05-30 10:33:13', 58.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1158, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83892, 9.1585, '2017-05-30 10:34:13', 15.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1159, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84044, 9.1605, '2017-05-30 10:35:13', 4.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1160, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83998, 9.16172, '2017-05-30 10:38:25', 14.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1161, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84198, 9.15865, '2017-05-30 10:39:25', 37.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1162, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84447, 9.16286, '2017-05-30 10:40:25', 1.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1163, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8425, 9.17096, '2017-05-30 10:41:25', 70.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1164, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83827, 9.18498, '2017-05-30 10:42:25', 70.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1165, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83678, 9.19413, '2017-05-30 10:43:25', 1.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1166, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8362, 9.19542, '2017-05-30 10:44:54', 50, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1167, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83154, 9.20077, '2017-05-30 10:45:54', 41.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1168, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.829, 9.20323, '2017-05-30 10:46:54', 13, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1169, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82934, 9.20457, '2017-05-30 10:48:50', 39.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1170, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83186, 9.20016, '2017-05-30 10:49:50', 48.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1171, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83747, 9.18739, '2017-05-30 10:50:50', 67, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1172, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84258, 9.17468, '2017-05-30 10:51:50', 51.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1173, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84691, 9.17663, '2017-05-30 10:52:50', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1174, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84765, 9.17552, '2017-05-30 10:53:50', 55, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1175, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85545, 9.1735, '2017-05-30 10:54:50', 76.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1176, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85591, 9.1651, '2017-05-30 10:55:50', 26.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1177, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8563, 9.16384, '2017-05-30 11:02:00', 27.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1178, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8559, 9.17174, '2017-05-30 11:03:00', 74.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1179, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84992, 9.17508, '2017-05-30 11:04:00', 60.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1180, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84237, 9.17452, '2017-05-30 11:05:00', 36.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1181, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83748, 9.16862, '2017-05-30 11:06:00', 60.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1182, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83654, 9.1584, '2017-05-30 11:07:00', 0.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1183, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83672, 9.15639, '2017-05-30 11:08:00', 42.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1184, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83782, 9.14516, '2017-05-30 11:09:00', 17.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1185, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82984, 9.13869, '2017-05-30 11:10:00', 102.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1186, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81459, 9.13453, '2017-05-30 11:11:00', 105.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1187, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80009, 9.14191, '2017-05-30 11:12:00', 99.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1188, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78606, 9.14965, '2017-05-30 11:13:00', 103.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1189, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77779, 9.16573, '2017-05-30 11:14:00', 46.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1190, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78328, 9.17063, '2017-05-30 11:15:00', 60, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1191, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78312, 9.17608, '2017-05-30 11:16:00', 41.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1192, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78187, 9.18094, '2017-05-30 11:17:00', 30.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1193, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77667, 9.18402, '2017-05-30 11:18:00', 50.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1194, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76974, 9.18402, '2017-05-30 11:19:00', 48.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1195, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76782, 9.18395, '2017-05-30 11:20:00', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1196, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76877, 9.1841, '2017-05-30 11:23:47', 47.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1197, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77624, 9.18414, '2017-05-30 11:24:47', 42.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1198, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7808, 9.18244, '2017-05-30 11:25:47', 38.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1199, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78281, 9.17627, '2017-05-30 11:26:47', 51.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1200, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78183, 9.16801, '2017-05-30 11:27:47', 78.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1201, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77474, 9.16916, '2017-05-30 11:28:47', 78.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1202, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7637, 9.17679, '2017-05-30 11:29:47', 99.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1203, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74991, 9.17516, '2017-05-30 11:30:47', 90.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1204, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73792, 9.18273, '2017-05-30 11:31:47', 83.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1205, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72784, 9.19575, '2017-05-30 11:32:47', 72.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1206, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71888, 9.20392, '2017-05-30 11:33:47', 71, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1207, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71333, 9.2142, '2017-05-30 11:34:47', 54.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1208, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71251, 9.21794, '2017-05-30 11:35:47', 62.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1209, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71884, 9.22664, '2017-05-30 11:36:47', 49.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1210, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71808, 9.22562, '2017-05-30 11:37:52', 66.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1211, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71454, 9.22131, '2017-05-30 11:38:52', 40.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1212, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71019, 9.22672, '2017-05-30 11:39:52', 48.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1213, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70903, 9.23031, '2017-05-30 11:40:52', 29.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1214, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70825, 9.23106, '2017-05-30 11:41:53', 14.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1215, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7085, 9.22965, '2017-05-30 11:45:14', 24.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1216, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71028, 9.22664, '2017-05-30 11:46:14', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1217, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71096, 9.2257, '2017-05-30 11:47:27', 35, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1218, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71632, 9.21932, '2017-05-30 11:48:27', 29.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1219, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71145, 9.21674, '2017-05-30 11:49:27', 39.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1220, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70631, 9.23086, '2017-05-30 11:50:27', 80.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1221, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70091, 9.24342, '2017-05-30 11:51:27', 46.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1222, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69962, 9.248, '2017-05-30 11:52:27', 15.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1223, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69941, 9.25141, '2017-05-30 11:53:27', 6.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1224, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69988, 9.25265, '2017-05-30 11:54:57', 31.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1225, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7031, 9.25722, '2017-05-30 11:55:57', 21.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1226, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70401, 9.25189, '2017-05-30 11:56:57', 42.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1227, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7047, 9.25068, '2017-05-30 11:57:57', 0.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1228, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70537, 9.24956, '2017-05-30 12:40:16', 40.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1229, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70681, 9.24673, '2017-05-30 12:41:16', 25.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1230, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70806, 9.24471, '2017-05-30 12:42:16', 0.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1231, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71038, 9.24528, '2017-05-30 12:43:16', 1.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1232, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71004, 9.24657, '2017-05-30 12:48:40', 39, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1233, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70649, 9.24587, '2017-05-30 12:49:40', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1234, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71099, 9.23996, '2017-05-30 12:50:40', 59.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1235, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72012, 9.23201, '2017-05-30 12:51:40', 76.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1236, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71716, 9.22426, '2017-05-30 12:52:40', 56.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1237, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71203, 9.21715, '2017-05-30 12:53:40', 15.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1238, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71584, 9.20671, '2017-05-30 12:54:40', 70.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1239, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72588, 9.19866, '2017-05-30 12:55:40', 75.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1240, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73547, 9.18585, '2017-05-30 12:56:40', 88.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1241, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74601, 9.17698, '2017-05-30 12:57:40', 79.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1242, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75773, 9.1756, '2017-05-30 12:58:40', 82.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1243, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76926, 9.17627, '2017-05-30 12:59:40', 73.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1244, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77931, 9.16393, '2017-05-30 13:00:40', 97.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1245, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78834, 9.14755, '2017-05-30 13:01:40', 90.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1246, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80223, 9.14062, '2017-05-30 13:02:40', 100.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1247, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8152, 9.13474, '2017-05-30 13:03:40', 93.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1248, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82981, 9.13888, '2017-05-30 13:04:40', 107.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1249, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84374, 9.15127, '2017-05-30 13:05:40', 109.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1250, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85976, 9.15598, '2017-05-30 13:06:40', 105.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1251, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87416, 9.14741, '2017-05-30 13:07:40', 112.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1252, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88925, 9.13549, '2017-05-30 13:08:40', 112.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1253, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90302, 9.12462, '2017-05-30 13:09:40', 103, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1254, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91568, 9.11461, '2017-05-30 13:10:40', 88.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1255, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92696, 9.1057, '2017-05-30 13:11:40', 91.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1256, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93696, 9.09445, '2017-05-30 13:12:40', 73.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1257, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93628, 9.09059, '2017-05-30 13:13:40', 49.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1258, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92902, 9.08226, '2017-05-30 13:14:40', 40.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1259, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92927, 9.0685, '2017-05-30 13:15:40', 17.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1260, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92245, 9.05893, '2017-05-30 13:16:40', 68.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1261, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92368, 9.04433, '2017-05-30 13:17:40', 64.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1262, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92533, 9.03001, '2017-05-30 13:18:40', 83.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1263, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92296, 9.01392, '2017-05-30 13:19:40', 43, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1264, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92166, 9.00703, '2017-05-30 13:20:40', 23.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1265, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92244, 9.00471, '2017-05-30 13:21:40', 1.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1266, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92154, 9.00496, '2017-05-30 13:27:41', 24.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1267, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92173, 9.00915, '2017-05-30 13:28:41', 39.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1268, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92614, 9.01499, '2017-05-30 13:29:41', 51.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1269, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01563, '2017-05-30 13:30:41', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1270, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92856, 9.01481, '2017-05-30 13:45:17', 1.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1271, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01571, '2017-05-30 13:46:17', 1.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1272, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92767, 9.014, '2017-05-30 14:25:44', 26.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1273, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92764, 9.0156, '2017-05-30 14:26:44', 1.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1274, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92839, 9.01648, '2017-05-30 15:08:05', 16.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1275, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92734, 9.01578, '2017-05-30 15:09:17', 12.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1276, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9283, 9.01554, '2017-05-30 15:24:25', 6.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1277, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92685, 9.01608, '2017-05-30 15:26:38', 19, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1278, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.01578, '2017-05-30 15:28:12', 18.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1279, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92831, 9.01709, '2017-05-30 15:33:46', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1280, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01569, '2017-05-30 15:35:49', 8.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1281, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92805, 9.01686, '2017-05-30 15:37:47', 28.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1282, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9273, 9.01576, '2017-05-30 15:40:26', 8.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1283, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92806, 9.01751, '2017-05-30 16:38:22', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1284, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92695, 9.0144, '2017-05-30 16:39:33', 11.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1285, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.0158, '2017-05-30 16:41:44', 47.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1286, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92612, 9.01519, '2017-05-30 16:42:57', 9.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1287, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01556, '2017-05-30 16:44:00', 2.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1288, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92669, 9.01584, '2017-05-30 17:34:05', 27.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1289, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01554, '2017-05-30 17:35:05', 14.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1290, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92858, 9.01772, '2017-05-30 17:41:58', 8.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1291, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9279, 9.01605, '2017-05-30 17:42:58', 6.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1292, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01443, '2017-05-30 17:55:28', 29.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1293, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92798, 9.01576, '2017-05-30 18:00:12', 10.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1294, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92732, 9.01715, '2017-05-30 18:34:21', 21.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1295, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9279, 9.01602, '2017-05-30 19:12:52', 40.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1296, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92867, 9.01687, '2017-05-30 19:31:41', 22.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1297, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92741, 9.01535, '2017-05-30 19:33:34', 9.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1298, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.0168, '2017-05-30 20:02:16', 5.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1299, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01488, '2017-05-30 20:03:16', 3.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1300, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92771, 9.0163, '2017-05-30 20:11:44', 7.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1301, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92701, 9.01522, '2017-05-30 20:26:05', 5.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1302, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9279, 9.01537, '2017-05-30 20:42:19', 4.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1303, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92664, 9.01509, '2017-05-30 20:54:52', 31.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1304, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01502, '2017-05-30 20:56:33', 6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1305, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92724, 9.01656, '2017-05-30 20:58:41', 6.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1306, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01525, '2017-05-30 21:48:03', 3.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1307, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92835, 9.01671, '2017-05-30 22:08:20', 9.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1308, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92759, 9.01519, '2017-05-30 22:12:27', 8.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1309, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92818, 9.01681, '2017-05-30 22:13:42', 18.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1310, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01545, '2017-05-30 22:14:42', 8.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1311, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92802, 9.01672, '2017-05-30 22:31:40', 24.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1312, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92895, 9.0168, '2017-05-30 22:32:44', 28.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1313, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9275, 9.0155, '2017-05-30 22:38:08', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1314, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92841, 9.01553, '2017-05-30 23:42:59', 14.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1315, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01555, '2017-05-30 23:44:48', 2.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1316, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92861, 9.01548, '2017-05-30 23:45:48', 16.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1317, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92745, 9.01561, '2017-05-30 23:46:48', 3.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1318, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92798, 9.01676, '2017-05-31 00:04:41', 13.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1319, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01538, '2017-05-31 00:05:41', 3.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1320, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92811, 9.01667, '2017-05-31 00:14:51', 19.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1321, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.0156, '2017-05-31 00:16:06', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1322, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92737, 9.01398, '2017-05-31 00:22:06', 5.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1323, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01577, '2017-05-31 00:23:06', 4.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1324, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92844, 9.01672, '2017-05-31 00:27:13', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1325, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92777, 9.01579, '2017-05-31 00:28:13', 0.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1326, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9284, 9.01718, '2017-05-31 00:29:46', 4.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1327, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92786, 9.01574, '2017-05-31 00:30:46', 3.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1328, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92669, 9.01424, '2017-05-31 00:34:11', 9.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1329, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92781, 9.01563, '2017-05-31 00:35:11', 7.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1330, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92716, 9.01421, '2017-05-31 00:36:27', 14.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1331, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92797, 9.01563, '2017-05-31 00:37:29', 2.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1332, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92712, 9.01476, '2017-05-31 00:41:13', 13, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1333, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92807, 9.01583, '2017-05-31 00:42:13', 5.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1334, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92724, 9.01519, '2017-05-31 00:43:54', 1.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1335, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92812, 9.0158, '2017-05-31 00:47:19', 0.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1336, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92723, 9.01547, '2017-05-31 00:51:15', 4.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1337, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92813, 9.01557, '2017-05-31 00:58:38', 17.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1338, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92702, 9.01569, '2017-05-31 01:00:07', 16.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1339, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92791, 9.01544, '2017-05-31 01:02:27', 6.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1340, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01676, '2017-05-31 01:30:18', 5.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1341, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01536, '2017-05-31 01:36:25', 3.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1342, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92748, 9.01676, '2017-05-31 01:46:21', 5.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1343, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92771, 9.0154, '2017-05-31 01:49:19', 2.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1344, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92757, 9.01401, '2017-05-31 01:52:27', 18.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1345, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01554, '2017-05-31 01:53:27', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1346, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92757, 9.01759, '2017-05-31 02:08:50', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1347, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92752, 9.01557, '2017-05-31 02:09:50', 3.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1348, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92708, 9.01859, '2017-05-31 02:15:26', 25.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1349, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92776, 9.0137, '2017-05-31 02:17:52', 10.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1350, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9274, 9.0162, '2017-05-31 02:18:52', 6.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1351, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92768, 9.01758, '2017-05-31 02:19:55', 16, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1352, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92762, 9.01599, '2017-05-31 02:20:56', 16.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1353, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92785, 9.01409, '2017-05-31 02:21:56', 34.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1354, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92781, 9.01564, '2017-05-31 02:22:56', 7.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1355, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92788, 9.01398, '2017-05-31 02:23:56', 4.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1356, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92742, 9.0155, '2017-05-31 02:24:56', 2.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1357, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92774, 9.01418, '2017-05-31 02:29:34', 8.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1358, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01546, '2017-05-31 02:32:34', 18.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1359, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9274, 9.01705, '2017-05-31 02:33:38', 12.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1360, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01538, '2017-05-31 02:34:38', 3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1361, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92761, 9.01713, '2017-05-31 02:36:50', 15.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1362, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92778, 9.01483, '2017-05-31 02:37:50', 11.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1363, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01624, '2017-05-31 02:39:31', 3.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1364, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92793, 9.01421, '2017-05-31 02:40:59', 19.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1365, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92747, 9.01556, '2017-05-31 02:42:01', 52.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1366, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01701, '2017-05-31 02:43:01', 15.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1367, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92766, 9.01565, '2017-05-31 02:44:01', 4.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1368, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92784, 9.01707, '2017-05-31 02:46:04', 13.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1369, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.0154, '2017-05-31 02:47:04', 0.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1370, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92802, 9.01296, '2017-05-31 02:50:59', 10.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1371, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01462, '2017-05-31 02:51:59', 1.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1372, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92765, 9.01603, '2017-05-31 02:56:01', 6.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1373, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01451, '2017-05-31 02:59:03', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1374, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92745, 9.01626, '2017-05-31 03:00:43', 14.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1375, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9277, 9.01461, '2017-05-31 03:01:44', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1376, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92748, 9.01602, '2017-05-31 03:03:44', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1377, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01443, '2017-05-31 04:57:02', 6.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1378, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.016, '2017-05-31 04:58:46', 0.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1379, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92799, 9.01809, '2017-05-31 04:59:51', 12.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1380, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92763, 9.01677, '2017-05-31 05:00:51', 1.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1381, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92711, 9.01394, '2017-05-31 05:02:17', 17.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1382, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92771, 9.01609, '2017-05-31 05:04:25', 5.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1383, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92745, 9.01445, '2017-05-31 05:08:36', 5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1384, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01602, '2017-05-31 05:09:36', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1385, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9275, 9.01456, '2017-05-31 05:11:23', 2.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1386, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92761, 9.01604, '2017-05-31 05:13:15', 3.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1387, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92732, 9.01461, '2017-05-31 05:19:41', 2.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1388, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92826, 9.01737, '2017-05-31 05:21:24', 3.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1389, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01484, '2017-05-31 05:28:12', 2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1390, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.01613, '2017-05-31 05:44:31', 7.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1391, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92683, 9.01691, '2017-05-31 07:06:49', 37.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1392, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01592, '2017-05-31 07:08:05', 2.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1393, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92729, 9.0132, '2017-05-31 07:19:09', 10.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1394, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92754, 9.01529, '2017-05-31 07:20:09', 4.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1395, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92795, 9.0168, '2017-05-31 07:33:26', 4.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1396, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92756, 9.01551, '2017-05-31 07:36:06', 6.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1397, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92848, 9.01583, '2017-05-31 07:37:19', 21.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1398, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9276, 9.01552, '2017-05-31 07:39:59', 1.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1399, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92732, 9.01415, '2017-05-31 07:54:57', 12.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1400, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92762, 9.01553, '2017-05-31 07:55:57', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1401, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92722, 9.01425, '2017-05-31 08:03:45', 10, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1402, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92775, 9.01694, '2017-05-31 08:06:36', 35.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1403, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01538, '2017-05-31 08:07:36', 3.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1404, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92797, 9.01765, '2017-05-31 08:08:36', 19.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1405, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92751, 9.01565, '2017-05-31 08:09:36', 8.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1406, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9269, 9.01461, '2017-05-31 08:10:41', 3.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1407, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92772, 9.01597, '2017-05-31 08:11:41', 6.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1408, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.927, 9.01506, '2017-05-31 09:01:24', 21.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1409, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92299, 9.01427, '2017-05-31 09:02:24', 13.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1410, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92422, 9.02768, '2017-05-31 09:03:24', 68.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1411, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92412, 9.04225, '2017-05-31 09:04:24', 36.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1412, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92972, 9.04084, '2017-05-31 09:05:24', 42.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1413, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93391, 9.04161, '2017-05-31 09:06:24', 39.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1414, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93811, 9.03714, '2017-05-31 09:07:24', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1415, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93737, 9.03043, '2017-05-31 09:08:24', 18, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1416, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93806, 9.02793, '2017-05-31 09:09:24', 19, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1417, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93774, 9.0265, '2017-05-31 09:10:24', 0.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1418, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93817, 9.02776, '2017-05-31 09:14:08', 27.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1419, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93698, 9.03359, '2017-05-31 09:15:08', 23.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1420, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93543, 9.03519, '2017-05-31 09:16:08', 27.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1421, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93428, 9.0356, '2017-05-31 09:17:08', 5.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1422, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93526, 9.03541, '2017-05-31 09:18:22', 40.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1423, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93787, 9.03633, '2017-05-31 09:19:22', 41.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1424, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93481, 9.0413, '2017-05-31 09:20:22', 28.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1425, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93897, 9.0545, '2017-05-31 09:21:22', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1426, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93122, 9.06634, '2017-05-31 09:22:22', 38.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1427, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93342, 9.0705, '2017-05-31 09:23:22', 57.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1428, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93375, 9.0737, '2017-05-31 09:24:22', 8.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1429, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93408, 9.07237, '2017-05-31 09:27:26', 27.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1430, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93082, 9.07215, '2017-05-31 09:28:26', 40, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1431, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92921, 9.07322, '2017-05-31 09:29:26', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1432, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92916, 9.0834, '2017-05-31 09:30:26', 54.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1433, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93634, 9.09066, '2017-05-31 09:31:26', 31.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1434, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.93768, 9.09225, '2017-05-31 09:32:26', 76.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1435, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92474, 9.10717, '2017-05-31 09:33:26', 117.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1436, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.914, 9.11395, '2017-05-31 09:34:26', 47.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1437, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91347, 9.12497, '2017-05-31 09:35:26', 27, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1438, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91399, 9.13501, '2017-05-31 09:36:26', 31.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1439, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91387, 9.13654, '2017-05-31 09:37:26', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1440, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91404, 9.13799, '2017-05-31 09:39:16', 32.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1441, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91444, 9.13229, '2017-05-31 09:40:16', 47.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1442, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91364, 9.12084, '2017-05-31 09:41:16', 74.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1443, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91163, 9.11758, '2017-05-31 09:42:16', 73.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1444, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.89952, 9.12713, '2017-05-31 09:43:16', 95.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1445, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88729, 9.13678, '2017-05-31 09:44:16', 88.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1446, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.87423, 9.14709, '2017-05-31 09:45:16', 99.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1447, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86083, 9.15589, '2017-05-31 09:46:16', 101.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1448, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.84618, 9.15191, '2017-05-31 09:47:16', 91.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1449, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8374, 9.1452, '2017-05-31 09:48:16', 28.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1450, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83722, 9.15165, '2017-05-31 09:49:16', 55.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1451, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83649, 9.15751, '2017-05-31 09:50:16', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1452, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83426, 9.1574, '2017-05-31 09:51:16', 59.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1453, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82373, 9.15481, '2017-05-31 09:52:16', 58.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1454, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81961, 9.15445, '2017-05-31 09:53:16', 8.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1455, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81959, 9.15587, '2017-05-31 09:56:17', 10.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1456, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82109, 9.15316, '2017-05-31 09:57:17', 26.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1457, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82294, 9.15518, '2017-05-31 09:58:17', 44.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1458, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83246, 9.15698, '2017-05-31 09:59:17', 65.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1459, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83639, 9.158, '2017-05-31 10:00:17', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1460, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83719, 9.15251, '2017-05-31 10:01:17', 60, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1461, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83632, 9.14577, '2017-05-31 10:02:17', 49.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1462, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82367, 9.13691, '2017-05-31 10:03:17', 111, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1463, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.80592, 9.13658, '2017-05-31 10:04:17', 117.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1464, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7933, 9.1437, '2017-05-31 10:05:17', 48.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1465, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79629, 9.14807, '2017-05-31 10:06:17', 48.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1466, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79309, 9.15088, '2017-05-31 10:07:17', 21.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1467, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79256, 9.1497, '2017-05-31 10:08:17', 0.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1468, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7924, 9.15118, '2017-05-31 10:14:27', 38.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1469, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79064, 9.15735, '2017-05-31 10:15:27', 37.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1470, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79173, 9.16409, '2017-05-31 10:16:27', 47.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1471, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78766, 9.17034, '2017-05-31 10:17:27', 29.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1472, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7844, 9.17332, '2017-05-31 10:18:27', 29, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1473, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78295, 9.17098, '2017-05-31 10:19:27', 1.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1474, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78238, 9.16984, '2017-05-31 10:25:02', 13.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1475, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77893, 9.16407, '2017-05-31 10:26:02', 79.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1476, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77099, 9.17439, '2017-05-31 10:27:02', 77.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1477, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76058, 9.17616, '2017-05-31 10:28:02', 68.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1478, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75006, 9.17512, '2017-05-31 10:29:02', 64.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1479, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74072, 9.17983, '2017-05-31 10:30:02', 69.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1480, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73197, 9.19016, '2017-05-31 10:31:02', 75.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1481, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72368, 9.20161, '2017-05-31 10:32:02', 78.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1482, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71465, 9.21076, '2017-05-31 10:33:02', 71.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1483, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71038, 9.21811, '2017-05-31 10:34:02', 45.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1484, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70079, 9.22065, '2017-05-31 10:35:02', 76.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1485, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.6897, 9.22292, '2017-05-31 10:36:02', 78.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1486, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.67924, 9.21819, '2017-05-31 10:37:02', 67.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1487, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.67122, 9.21362, '2017-05-31 10:38:02', 50.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1488, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.66785, 9.2149, '2017-05-31 10:39:02', 31.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1489, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.66847, 9.2177, '2017-05-31 10:40:02', 29.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1490, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.66758, 9.21856, '2017-05-31 10:41:02', 10.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1491, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.66832, 9.21942, '2017-05-31 10:43:50', 26.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1492, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.66854, 9.21543, '2017-05-31 10:44:50', 22.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1493, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.67035, 9.21238, '2017-05-31 10:45:50', 44, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1494, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.67799, 9.21856, '2017-05-31 10:46:50', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1495, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.6896, 9.22284, '2017-05-31 10:47:50', 83.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1496, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70014, 9.22072, '2017-05-31 10:48:50', 70.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1497, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71037, 9.2183, '2017-05-31 10:49:50', 49.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1498, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.7064, 9.23069, '2017-05-31 10:50:50', 74.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1499, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70154, 9.24197, '2017-05-31 10:51:50', 46.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1500, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69988, 9.25168, '2017-05-31 10:52:50', 48.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1501, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70103, 9.25756, '2017-05-31 10:53:50', 10.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1502, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69991, 9.26183, '2017-05-31 10:54:50', 40.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1503, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70135, 9.26394, '2017-05-31 10:55:50', 14.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1504, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.6991, 9.26133, '2017-05-31 10:56:50', 22.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1505, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70001, 9.26273, '2017-05-31 10:57:50', 12.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1506, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.6996, 9.26712, '2017-05-31 10:58:50', 14.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1507, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69999, 9.26269, '2017-05-31 10:59:50', 11.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1508, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70291, 9.26159, '2017-05-31 11:00:50', 26.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1509, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69994, 9.26139, '2017-05-31 11:01:50', 36.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1510, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70269, 9.26302, '2017-05-31 11:02:50', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1511, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70417, 9.26477, '2017-05-31 11:03:50', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1512, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70176, 9.27146, '2017-05-31 11:04:50', 33.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1513, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.6989, 9.26817, '2017-05-31 11:05:50', 11.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1514, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69878, 9.26571, '2017-05-31 11:06:50', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1515, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.69915, 9.26699, '2017-05-31 11:12:27', 24.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1516, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70132, 9.26813, '2017-05-31 11:13:27', 27.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1517, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70213, 9.26725, '2017-05-31 11:14:33', 40.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1518, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70369, 9.26113, '2017-05-31 11:15:33', 32.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1519, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70248, 9.25643, '2017-05-31 11:16:33', 46.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1520, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70474, 9.25068, '2017-05-31 11:17:33', 4.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1521, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70537, 9.24966, '2017-05-31 12:12:03', 39.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1522, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.70884, 9.2412, '2017-05-31 12:13:03', 55.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1523, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71702, 9.23564, '2017-05-31 12:14:03', 50.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1524, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72118, 9.22917, '2017-05-31 12:15:03', 44, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1525, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71512, 9.2202, '2017-05-31 12:16:03', 59.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1526, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.71314, 9.21462, '2017-05-31 12:17:03', 60.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1527, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72022, 9.20377, '2017-05-31 12:18:03', 78.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1528, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.72961, 9.19334, '2017-05-31 12:19:03', 82.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1529, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.73785, 9.18292, '2017-05-31 12:20:03', 60.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1530, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.74697, 9.17651, '2017-05-31 12:21:03', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1531, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.75716, 9.17546, '2017-05-31 12:22:03', 65.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1532, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.76738, 9.17703, '2017-05-31 12:23:03', 77.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1533, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.77734, 9.16665, '2017-05-31 12:24:03', 89.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1534, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.78585, 9.15003, '2017-05-31 12:25:03', 93.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1535, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.79834, 9.14296, '2017-05-31 12:26:03', 85.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1536, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.81079, 9.13452, '2017-05-31 12:27:03', 104.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1537, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.82664, 9.13828, '2017-05-31 12:28:03', 113.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1538, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83196, 9.13662, '2017-05-31 12:29:03', 60.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1539, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83407, 9.12574, '2017-05-31 12:30:03', 49.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1540, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.83573, 9.11608, '2017-05-31 12:31:03', 48.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1541, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8383, 9.10273, '2017-05-31 12:32:03', 68.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1542, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8466, 9.09133, '2017-05-31 12:33:03', 65.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1543, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85361, 9.08795, '2017-05-31 12:34:03', 39.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1544, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85181, 9.08205, '2017-05-31 12:35:03', 54.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1545, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85048, 9.07794, '2017-05-31 12:36:03', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1546, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85076, 9.07939, '2017-05-31 12:39:52', 41.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1547, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85457, 9.08643, '2017-05-31 12:40:52', 16, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1548, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.85837, 9.0826, '2017-05-31 12:41:52', 33.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1549, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.86282, 9.07899, '2017-05-31 12:42:52', 56.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1550, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8725, 9.07838, '2017-05-31 12:43:52', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1551, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.88136, 9.0768, '2017-05-31 12:44:52', 48.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1552, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.8914, 9.07638, '2017-05-31 12:45:52', 63.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1553, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.901, 9.07007, '2017-05-31 12:46:52', 74.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1554, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.90836, 9.06002, '2017-05-31 12:47:52', 37.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1555, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.91386, 9.06232, '2017-05-31 12:48:52', 75.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1556, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92142, 9.05733, '2017-05-31 12:49:52', 57.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1557, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92421, 9.04208, '2017-05-31 12:50:52', 34, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1558, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.9244, 9.02803, '2017-05-31 12:51:52', 63.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (1559, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92333, 9.01433, '2017-05-31 12:52:52', 34, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1560, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92773, 9.01564, '2017-05-31 12:53:52', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1561, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92698, 9.01722, '2017-05-31 14:01:49', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (1562, 1, NULL, '2017-06-01 10:47:11', '0000-00-00 00:00:00', 49.92758, 9.01569, '2017-05-31 14:02:49', 4.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2055, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92798, 9.01676, '2017-05-31 00:04:41', 13.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2056, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92766, 9.01538, '2017-05-31 00:05:41', 3.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2057, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92811, 9.01667, '2017-05-31 00:14:51', 19.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2058, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92752, 9.0156, '2017-05-31 00:16:06', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2059, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92737, 9.01398, '2017-05-31 00:22:06', 5.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2060, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92752, 9.01577, '2017-05-31 00:23:06', 4.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2061, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92844, 9.01672, '2017-05-31 00:27:13', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2062, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92777, 9.01579, '2017-05-31 00:28:13', 0.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2063, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9284, 9.01718, '2017-05-31 00:29:46', 4.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2064, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92786, 9.01574, '2017-05-31 00:30:46', 3.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2065, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92669, 9.01424, '2017-05-31 00:34:11', 9.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2066, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92781, 9.01563, '2017-05-31 00:35:11', 7.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2067, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92716, 9.01421, '2017-05-31 00:36:27', 14.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2068, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92797, 9.01563, '2017-05-31 00:37:29', 2.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2069, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92712, 9.01476, '2017-05-31 00:41:13', 13, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2070, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92807, 9.01583, '2017-05-31 00:42:13', 5.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2071, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92724, 9.01519, '2017-05-31 00:43:54', 1.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2072, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92812, 9.0158, '2017-05-31 00:47:19', 0.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2073, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92723, 9.01547, '2017-05-31 00:51:15', 4.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2074, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92813, 9.01557, '2017-05-31 00:58:38', 17.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2075, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92702, 9.01569, '2017-05-31 01:00:07', 16.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2076, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92791, 9.01544, '2017-05-31 01:02:27', 6.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2077, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01676, '2017-05-31 01:30:18', 5.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2078, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92763, 9.01536, '2017-05-31 01:36:25', 3.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2079, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92748, 9.01676, '2017-05-31 01:46:21', 5.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2080, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92771, 9.0154, '2017-05-31 01:49:19', 2.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2081, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92757, 9.01401, '2017-05-31 01:52:27', 18.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2082, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92765, 9.01554, '2017-05-31 01:53:27', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2083, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92757, 9.01759, '2017-05-31 02:08:50', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2084, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92752, 9.01557, '2017-05-31 02:09:50', 3.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2085, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92708, 9.01859, '2017-05-31 02:15:26', 25.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2086, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92776, 9.0137, '2017-05-31 02:17:52', 10.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2087, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9274, 9.0162, '2017-05-31 02:18:52', 6.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2088, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92768, 9.01758, '2017-05-31 02:19:55', 16, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2089, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92762, 9.01599, '2017-05-31 02:20:56', 16.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2090, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92785, 9.01409, '2017-05-31 02:21:56', 34.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2091, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92781, 9.01564, '2017-05-31 02:22:56', 7.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2092, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92788, 9.01398, '2017-05-31 02:23:56', 4.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2093, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92742, 9.0155, '2017-05-31 02:24:56', 2.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2094, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92774, 9.01418, '2017-05-31 02:29:34', 8.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2095, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92722, 9.01546, '2017-05-31 02:32:34', 18.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2096, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9274, 9.01705, '2017-05-31 02:33:38', 12.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2097, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92747, 9.01538, '2017-05-31 02:34:38', 3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2098, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92761, 9.01713, '2017-05-31 02:36:50', 15.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2099, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92778, 9.01483, '2017-05-31 02:37:50', 11.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2100, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01624, '2017-05-31 02:39:31', 3.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2101, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92793, 9.01421, '2017-05-31 02:40:59', 19.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2102, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92747, 9.01556, '2017-05-31 02:42:01', 52.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2103, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92722, 9.01701, '2017-05-31 02:43:01', 15.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2104, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92766, 9.01565, '2017-05-31 02:44:01', 4.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2105, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92784, 9.01707, '2017-05-31 02:46:04', 13.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2106, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.0154, '2017-05-31 02:47:04', 0.7, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2107, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92802, 9.01296, '2017-05-31 02:50:59', 10.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2108, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.01462, '2017-05-31 02:51:59', 1.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2109, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92765, 9.01603, '2017-05-31 02:56:01', 6.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2110, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92775, 9.01451, '2017-05-31 02:59:03', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2111, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92745, 9.01626, '2017-05-31 03:00:43', 14.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2112, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.01461, '2017-05-31 03:01:44', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2113, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92748, 9.01602, '2017-05-31 03:03:44', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2114, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92756, 9.01443, '2017-05-31 04:57:02', 6.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2115, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92772, 9.016, '2017-05-31 04:58:46', 0.9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2116, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92799, 9.01809, '2017-05-31 04:59:51', 12.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2117, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92763, 9.01677, '2017-05-31 05:00:51', 1.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2118, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92711, 9.01394, '2017-05-31 05:02:17', 17.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2119, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92771, 9.01609, '2017-05-31 05:04:25', 5.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2120, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92745, 9.01445, '2017-05-31 05:08:36', 5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2121, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01602, '2017-05-31 05:09:36', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2122, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9275, 9.01456, '2017-05-31 05:11:23', 2.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2123, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92761, 9.01604, '2017-05-31 05:13:15', 3.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2124, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92732, 9.01461, '2017-05-31 05:19:41', 2.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2125, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92826, 9.01737, '2017-05-31 05:21:24', 3.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2126, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92756, 9.01484, '2017-05-31 05:28:12', 2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2127, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92795, 9.01613, '2017-05-31 05:44:31', 7.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2128, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92683, 9.01691, '2017-05-31 07:06:49', 37.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2129, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92751, 9.01592, '2017-05-31 07:08:05', 2.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2130, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92729, 9.0132, '2017-05-31 07:19:09', 10.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2131, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92754, 9.01529, '2017-05-31 07:20:09', 4.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2132, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92795, 9.0168, '2017-05-31 07:33:26', 4.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2133, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92756, 9.01551, '2017-05-31 07:36:06', 6.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2134, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92848, 9.01583, '2017-05-31 07:37:19', 21.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2135, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01552, '2017-05-31 07:39:59', 1.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2136, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92732, 9.01415, '2017-05-31 07:54:57', 12.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2137, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92762, 9.01553, '2017-05-31 07:55:57', 1.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2138, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92722, 9.01425, '2017-05-31 08:03:45', 10, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2139, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92775, 9.01694, '2017-05-31 08:06:36', 35.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2140, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92758, 9.01538, '2017-05-31 08:07:36', 3.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2141, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92797, 9.01765, '2017-05-31 08:08:36', 19.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2142, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92751, 9.01565, '2017-05-31 08:09:36', 8.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2143, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9269, 9.01461, '2017-05-31 08:10:41', 3.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2144, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92772, 9.01597, '2017-05-31 08:11:41', 6.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2145, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.927, 9.01506, '2017-05-31 09:01:24', 21.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2146, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92299, 9.01427, '2017-05-31 09:02:24', 13.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2147, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92422, 9.02768, '2017-05-31 09:03:24', 68.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2148, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92412, 9.04225, '2017-05-31 09:04:24', 36.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2149, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92972, 9.04084, '2017-05-31 09:05:24', 42.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2150, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93391, 9.04161, '2017-05-31 09:06:24', 39.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2151, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93811, 9.03714, '2017-05-31 09:07:24', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2152, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93737, 9.03043, '2017-05-31 09:08:24', 18, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2153, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93806, 9.02793, '2017-05-31 09:09:24', 19, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2154, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93774, 9.0265, '2017-05-31 09:10:24', 0.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2155, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93817, 9.02776, '2017-05-31 09:14:08', 27.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2156, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93698, 9.03359, '2017-05-31 09:15:08', 23.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2157, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93543, 9.03519, '2017-05-31 09:16:08', 27.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2158, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93428, 9.0356, '2017-05-31 09:17:08', 5.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2159, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93526, 9.03541, '2017-05-31 09:18:22', 40.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2160, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93787, 9.03633, '2017-05-31 09:19:22', 41.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2161, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93481, 9.0413, '2017-05-31 09:20:22', 28.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2162, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93897, 9.0545, '2017-05-31 09:21:22', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2163, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93122, 9.06634, '2017-05-31 09:22:22', 38.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2164, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93342, 9.0705, '2017-05-31 09:23:22', 57.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2165, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93375, 9.0737, '2017-05-31 09:24:22', 8.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2166, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93408, 9.07237, '2017-05-31 09:27:26', 27.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2167, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93082, 9.07215, '2017-05-31 09:28:26', 40, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2168, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92921, 9.07322, '2017-05-31 09:29:26', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2169, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92916, 9.0834, '2017-05-31 09:30:26', 54.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2170, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93634, 9.09066, '2017-05-31 09:31:26', 31.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2171, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93768, 9.09225, '2017-05-31 09:32:26', 76.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2172, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92474, 9.10717, '2017-05-31 09:33:26', 117.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2173, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.914, 9.11395, '2017-05-31 09:34:26', 47.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2174, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91347, 9.12497, '2017-05-31 09:35:26', 27, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2175, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91399, 9.13501, '2017-05-31 09:36:26', 31.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2176, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91387, 9.13654, '2017-05-31 09:37:26', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2177, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91404, 9.13799, '2017-05-31 09:39:16', 32.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2178, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91444, 9.13229, '2017-05-31 09:40:16', 47.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2179, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91364, 9.12084, '2017-05-31 09:41:16', 74.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2180, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91163, 9.11758, '2017-05-31 09:42:16', 73.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2181, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.89952, 9.12713, '2017-05-31 09:43:16', 95.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2182, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.88729, 9.13678, '2017-05-31 09:44:16', 88.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2183, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87423, 9.14709, '2017-05-31 09:45:16', 99.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2184, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.86083, 9.15589, '2017-05-31 09:46:16', 101.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2185, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.84618, 9.15191, '2017-05-31 09:47:16', 91.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2186, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8374, 9.1452, '2017-05-31 09:48:16', 28.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2187, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83722, 9.15165, '2017-05-31 09:49:16', 55.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2188, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83649, 9.15751, '2017-05-31 09:50:16', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2189, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83426, 9.1574, '2017-05-31 09:51:16', 59.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2190, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82373, 9.15481, '2017-05-31 09:52:16', 58.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2191, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.81961, 9.15445, '2017-05-31 09:53:16', 8.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2192, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.81959, 9.15587, '2017-05-31 09:56:17', 10.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2193, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82109, 9.15316, '2017-05-31 09:57:17', 26.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2194, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82294, 9.15518, '2017-05-31 09:58:17', 44.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2195, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83246, 9.15698, '2017-05-31 09:59:17', 65.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2196, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83639, 9.158, '2017-05-31 10:00:17', 0.1, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2197, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83719, 9.15251, '2017-05-31 10:01:17', 60, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2198, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83632, 9.14577, '2017-05-31 10:02:17', 49.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2199, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82367, 9.13691, '2017-05-31 10:03:17', 111, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2200, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.80592, 9.13658, '2017-05-31 10:04:17', 117.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2201, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7933, 9.1437, '2017-05-31 10:05:17', 48.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2202, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79629, 9.14807, '2017-05-31 10:06:17', 48.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2203, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79309, 9.15088, '2017-05-31 10:07:17', 21.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2204, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79256, 9.1497, '2017-05-31 10:08:17', 0.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2205, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7924, 9.15118, '2017-05-31 10:14:27', 38.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2206, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79064, 9.15735, '2017-05-31 10:15:27', 37.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2207, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79173, 9.16409, '2017-05-31 10:16:27', 47.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2208, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.78766, 9.17034, '2017-05-31 10:17:27', 29.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2209, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7844, 9.17332, '2017-05-31 10:18:27', 29, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2210, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.78295, 9.17098, '2017-05-31 10:19:27', 1.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2211, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.78238, 9.16984, '2017-05-31 10:25:02', 13.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2212, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77893, 9.16407, '2017-05-31 10:26:02', 79.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2213, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77099, 9.17439, '2017-05-31 10:27:02', 77.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2214, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76058, 9.17616, '2017-05-31 10:28:02', 68.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2215, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.75006, 9.17512, '2017-05-31 10:29:02', 64.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2216, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.74072, 9.17983, '2017-05-31 10:30:02', 69.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2217, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.73197, 9.19016, '2017-05-31 10:31:02', 75.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2218, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72368, 9.20161, '2017-05-31 10:32:02', 78.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2219, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71465, 9.21076, '2017-05-31 10:33:02', 71.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2220, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71038, 9.21811, '2017-05-31 10:34:02', 45.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2221, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70079, 9.22065, '2017-05-31 10:35:02', 76.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2222, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.6897, 9.22292, '2017-05-31 10:36:02', 78.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2223, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67924, 9.21819, '2017-05-31 10:37:02', 67.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2224, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67122, 9.21362, '2017-05-31 10:38:02', 50.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2225, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.66785, 9.2149, '2017-05-31 10:39:02', 31.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2226, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.66847, 9.2177, '2017-05-31 10:40:02', 29.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2227, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.66758, 9.21856, '2017-05-31 10:41:02', 10.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2228, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.66832, 9.21942, '2017-05-31 10:43:50', 26.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2229, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.66854, 9.21543, '2017-05-31 10:44:50', 22.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2230, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67035, 9.21238, '2017-05-31 10:45:50', 44, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2231, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67799, 9.21856, '2017-05-31 10:46:50', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2232, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.6896, 9.22284, '2017-05-31 10:47:50', 83.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2233, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70014, 9.22072, '2017-05-31 10:48:50', 70.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2234, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71037, 9.2183, '2017-05-31 10:49:50', 49.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2235, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7064, 9.23069, '2017-05-31 10:50:50', 74.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2236, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70154, 9.24197, '2017-05-31 10:51:50', 46.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2237, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69988, 9.25168, '2017-05-31 10:52:50', 48.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2238, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70103, 9.25756, '2017-05-31 10:53:50', 10.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2239, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69991, 9.26183, '2017-05-31 10:54:50', 40.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2240, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70135, 9.26394, '2017-05-31 10:55:50', 14.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2241, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.6991, 9.26133, '2017-05-31 10:56:50', 22.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2242, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70001, 9.26273, '2017-05-31 10:57:50', 12.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2243, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.6996, 9.26712, '2017-05-31 10:58:50', 14.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2244, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69999, 9.26269, '2017-05-31 10:59:50', 11.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2245, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70291, 9.26159, '2017-05-31 11:00:50', 26.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2246, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69994, 9.26139, '2017-05-31 11:01:50', 36.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2247, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70269, 9.26302, '2017-05-31 11:02:50', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2248, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70417, 9.26477, '2017-05-31 11:03:50', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2249, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70176, 9.27146, '2017-05-31 11:04:50', 33.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2250, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.6989, 9.26817, '2017-05-31 11:05:50', 11.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2251, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69878, 9.26571, '2017-05-31 11:06:50', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2252, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69915, 9.26699, '2017-05-31 11:12:27', 24.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2253, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70132, 9.26813, '2017-05-31 11:13:27', 27.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2254, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70213, 9.26725, '2017-05-31 11:14:33', 40.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2255, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70369, 9.26113, '2017-05-31 11:15:33', 32.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2256, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70248, 9.25643, '2017-05-31 11:16:33', 46.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2257, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70474, 9.25068, '2017-05-31 11:17:33', 4.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2258, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70537, 9.24966, '2017-05-31 12:12:03', 39.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2259, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70884, 9.2412, '2017-05-31 12:13:03', 55.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2260, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71702, 9.23564, '2017-05-31 12:14:03', 50.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2261, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72118, 9.22917, '2017-05-31 12:15:03', 44, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2262, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71512, 9.2202, '2017-05-31 12:16:03', 59.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2263, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71314, 9.21462, '2017-05-31 12:17:03', 60.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2264, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72022, 9.20377, '2017-05-31 12:18:03', 78.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2265, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72961, 9.19334, '2017-05-31 12:19:03', 82.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2266, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.73785, 9.18292, '2017-05-31 12:20:03', 60.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2267, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.74697, 9.17651, '2017-05-31 12:21:03', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2268, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.75716, 9.17546, '2017-05-31 12:22:03', 65.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2269, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76738, 9.17703, '2017-05-31 12:23:03', 77.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2270, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77734, 9.16665, '2017-05-31 12:24:03', 89.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2271, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.78585, 9.15003, '2017-05-31 12:25:03', 93.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2272, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79834, 9.14296, '2017-05-31 12:26:03', 85.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2273, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.81079, 9.13452, '2017-05-31 12:27:03', 104.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2274, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82664, 9.13828, '2017-05-31 12:28:03', 113.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2275, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83196, 9.13662, '2017-05-31 12:29:03', 60.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2276, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83407, 9.12574, '2017-05-31 12:30:03', 49.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2277, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83573, 9.11608, '2017-05-31 12:31:03', 48.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2278, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8383, 9.10273, '2017-05-31 12:32:03', 68.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2279, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8466, 9.09133, '2017-05-31 12:33:03', 65.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2280, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85361, 9.08795, '2017-05-31 12:34:03', 39.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2281, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85181, 9.08205, '2017-05-31 12:35:03', 54.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2282, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85048, 9.07794, '2017-05-31 12:36:03', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2283, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85076, 9.07939, '2017-05-31 12:39:52', 41.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2284, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85457, 9.08643, '2017-05-31 12:40:52', 16, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2285, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85837, 9.0826, '2017-05-31 12:41:52', 33.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2286, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.86282, 9.07899, '2017-05-31 12:42:52', 56.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2287, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8725, 9.07838, '2017-05-31 12:43:52', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2288, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.88136, 9.0768, '2017-05-31 12:44:52', 48.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2289, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8914, 9.07638, '2017-05-31 12:45:52', 63.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2290, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.901, 9.07007, '2017-05-31 12:46:52', 74.8, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2291, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.90836, 9.06002, '2017-05-31 12:47:52', 37.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2292, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91386, 9.06232, '2017-05-31 12:48:52', 75.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2293, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92142, 9.05733, '2017-05-31 12:49:52', 57.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2294, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92421, 9.04208, '2017-05-31 12:50:52', 34, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2295, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9244, 9.02803, '2017-05-31 12:51:52', 63.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2296, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92333, 9.01433, '2017-05-31 12:52:52', 34, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2297, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92773, 9.01564, '2017-05-31 12:53:52', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2298, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92698, 9.01722, '2017-05-31 14:01:49', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2299, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92758, 9.01569, '2017-05-31 14:02:49', 4.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2300, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92666, 9.01562, '2017-05-31 14:33:22', 12.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2301, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92756, 9.01553, '2017-05-31 14:36:36', 4.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2302, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92669, 9.01519, '2017-05-31 15:01:54', 11.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2303, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92745, 9.01595, '2017-05-31 15:03:49', 9.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2304, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92824, 9.01485, '2017-05-31 15:08:02', 8.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2305, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92758, 9.01586, '2017-05-31 15:09:25', 17.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2306, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92786, 9.01745, '2017-05-31 15:12:12', 9.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2307, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92754, 9.01578, '2017-05-31 15:13:12', 5.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2308, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92831, 9.01502, '2017-05-31 15:49:49', 12, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2309, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92751, 9.01564, '2017-05-31 15:51:59', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2310, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92827, 9.01487, '2017-05-31 17:11:49', 7.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2311, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92761, 9.01582, '2017-05-31 17:13:10', 2.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2312, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9272, 9.01438, '2017-05-31 18:46:16', 3.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2313, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92777, 9.01552, '2017-05-31 18:49:10', 5.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2314, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92639, 9.01608, '2017-05-31 21:13:28', 4.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2315, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92791, 9.01566, '2017-05-31 21:14:28', 5.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2316, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92702, 9.01534, '2017-05-31 21:32:05', 14.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2317, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92782, 9.01603, '2017-05-31 21:47:29', 8.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2318, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92729, 9.01474, '2017-05-31 22:14:10', 13.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2319, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92766, 9.01604, '2017-05-31 23:52:36', 8.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2320, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92692, 9.01484, '2017-06-01 00:13:36', 12, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2321, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.01566, '2017-06-01 00:17:05', 18, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2322, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.927, 9.01478, '2017-06-01 00:19:20', 3.2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2323, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92749, 9.01595, '2017-06-01 00:20:34', 8.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2324, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9269, 9.01465, '2017-06-01 00:25:18', 2.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2325, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92768, 9.01611, '2017-06-01 00:26:18', 3.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2326, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92817, 9.01729, '2017-06-01 00:38:58', 10.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2327, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92758, 9.01584, '2017-06-01 00:39:59', 5.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2328, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92651, 9.01488, '2017-06-01 00:54:32', 7.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2329, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92763, 9.01571, '2017-06-01 00:56:07', 3.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2330, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92678, 9.0152, '2017-06-01 01:03:13', 1.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2331, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92769, 9.01551, '2017-06-01 01:05:02', 5.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2332, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92664, 9.02153, '2017-06-01 01:39:08', 5.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2333, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01679, '2017-06-01 01:40:08', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2334, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92771, 9.0154, '2017-06-01 01:43:04', 3.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2335, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92766, 9.01397, '2017-06-01 01:49:51', 9, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2336, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92764, 9.01537, '2017-06-01 01:51:11', 1.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2337, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92728, 9.0167, '2017-06-01 02:05:29', 6.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2338, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92757, 9.01534, '2017-06-01 02:14:52', 6.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2339, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01693, '2017-06-01 02:19:36', 8.6, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2340, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92761, 9.01548, '2017-06-01 02:20:39', 1.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2341, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92784, 9.01363, '2017-06-01 02:28:31', 8.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2342, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92773, 9.01603, '2017-06-01 02:30:13', 3, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2343, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92789, 9.01456, '2017-06-01 02:31:13', 12.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2344, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92848, 9.01322, '2017-06-01 02:32:24', 17.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2345, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92762, 9.01588, '2017-06-01 02:33:24', 3.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2346, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92799, 9.01454, '2017-06-01 02:36:12', 7.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2347, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92761, 9.01582, '2017-06-01 02:38:24', 2.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2348, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92716, 9.01725, '2017-06-01 02:45:46', 6.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2349, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92747, 9.01555, '2017-06-01 02:46:46', 2.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2350, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92803, 9.01417, '2017-06-01 02:55:22', 12, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2351, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92745, 9.01586, '2017-06-01 02:56:23', 3.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2352, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92695, 9.01468, '2017-06-01 03:50:59', 3.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2353, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92775, 9.01545, '2017-06-01 03:53:20', 5.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2354, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9279, 9.01686, '2017-06-01 04:06:45', 13.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2355, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92762, 9.01482, '2017-06-01 04:07:55', 6.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2356, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92771, 9.01621, '2017-06-01 04:16:17', 1.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2357, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92751, 9.01484, '2017-06-01 04:30:18', 3.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2358, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9279, 9.0166, '2017-06-01 04:31:18', 16.9, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2359, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92759, 9.01529, '2017-06-01 04:32:53', 5.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2360, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92814, 9.01384, '2017-06-01 04:38:47', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2361, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92763, 9.015, '2017-06-01 04:39:59', 1.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2362, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92772, 9.01639, '2017-06-01 04:51:57', 2.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2363, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92761, 9.01499, '2017-06-01 04:53:38', 10.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2364, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92728, 9.01369, '2017-06-01 04:55:40', 2.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2365, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92757, 9.01533, '2017-06-01 04:56:40', 5, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2366, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92803, 9.01661, '2017-06-01 04:58:15', 6.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2367, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.01405, '2017-06-01 04:59:15', 12.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2368, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92771, 9.01617, '2017-06-01 05:00:15', 3.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2369, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.928, 9.01754, '2017-06-01 05:01:38', 12.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2370, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92718, 9.01306, '2017-06-01 05:03:40', 25.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2371, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.01623, '2017-06-01 05:05:28', 1.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2372, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92767, 9.0148, '2017-06-01 05:09:47', 17.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2373, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92741, 9.01644, '2017-06-01 05:12:09', 13.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2374, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9276, 9.01501, '2017-06-01 05:14:13', 14.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2375, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92776, 9.01656, '2017-06-01 05:21:38', 12.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2376, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92767, 9.01483, '2017-06-01 05:24:50', 6.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2377, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9278, 9.01625, '2017-06-01 05:33:40', 4.1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2378, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92747, 9.01476, '2017-06-01 05:35:38', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2379, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92758, 9.01628, '2017-06-01 05:36:51', 2.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2380, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92751, 9.01483, '2017-06-01 05:40:18', 1.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2381, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92792, 9.01611, '2017-06-01 06:33:11', 11.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2382, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92722, 9.01703, '2017-06-01 06:45:28', 9.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2383, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9281, 9.01673, '2017-06-01 06:46:42', 3.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2384, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92719, 9.01608, '2017-06-01 06:48:41', 31.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2385, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92861, 9.01876, '2017-06-01 06:49:51', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2386, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92774, 9.01627, '2017-06-01 06:50:51', 2.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2387, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92819, 9.01751, '2017-06-01 06:53:29', 5.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2388, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92835, 9.01908, '2017-06-01 06:55:28', 12.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2389, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92803, 9.01743, '2017-06-01 06:56:28', 3.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2390, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92752, 9.01627, '2017-06-01 06:58:30', 4.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2391, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92717, 9.01395, '2017-06-01 07:06:42', 4.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2392, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92758, 9.01566, '2017-06-01 07:07:42', 2.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2393, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92767, 9.01717, '2017-06-01 07:13:33', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2394, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92726, 9.01462, '2017-06-01 07:14:40', 13.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2395, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92771, 9.01616, '2017-06-01 07:15:40', 5.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2396, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92688, 9.01554, '2017-06-01 07:17:43', 18.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2397, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92557, 9.01439, '2017-06-01 07:18:43', 1.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2398, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92744, 9.01544, '2017-06-01 07:19:43', 1.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2399, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92662, 9.01488, '2017-06-01 07:22:35', 11.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2400, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92753, 9.01507, '2017-06-01 07:23:55', 12.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2401, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9277, 9.01644, '2017-06-01 07:28:29', 3.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2402, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92742, 9.01504, '2017-06-01 07:31:46', 8.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2403, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92794, 9.0162, '2017-06-01 07:35:33', 15.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2404, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92744, 9.0135, '2017-06-01 07:36:38', 0.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2405, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92805, 9.01612, '2017-06-01 07:37:38', 2.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2406, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92734, 9.01522, '2017-06-01 07:39:30', 17, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2407, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92779, 9.01644, '2017-06-01 07:41:11', 5.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2408, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9274, 9.01509, '2017-06-01 07:50:55', 11.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2409, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.928, 9.017, '2017-06-01 07:52:44', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2410, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92785, 9.0154, '2017-06-01 07:53:56', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2411, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92713, 9.01392, '2017-06-01 07:55:05', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2412, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92766, 9.01535, '2017-06-01 07:56:05', 1.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2413, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92731, 9.014, '2017-06-01 07:59:29', 17.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2414, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92776, 9.01559, '2017-06-01 08:01:03', 10, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2415, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92699, 9.01305, '2017-06-01 08:02:17', 2.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2416, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92778, 9.01584, '2017-06-01 08:03:17', 9.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2417, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92633, 9.01448, '2017-06-01 08:10:54', 2.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2418, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92767, 9.01561, '2017-06-01 08:11:54', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2419, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92679, 9.01504, '2017-06-01 08:20:17', 30.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2420, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92297, 9.01497, '2017-06-01 08:21:17', 37.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2421, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92527, 9.03008, '2017-06-01 08:22:17', 68.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2422, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92386, 9.04344, '2017-06-01 08:23:17', 44.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2423, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92132, 9.05829, '2017-06-01 08:24:17', 30.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2424, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9281, 9.06443, '2017-06-01 08:25:17', 73.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2425, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92923, 9.07226, '2017-06-01 08:26:17', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2426, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93276, 9.07072, '2017-06-01 08:27:17', 44.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2427, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93391, 9.07314, '2017-06-01 08:28:17', 29.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2428, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93382, 9.07168, '2017-06-01 08:34:38', 31.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2429, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93336, 9.07037, '2017-06-01 08:35:38', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2430, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93244, 9.0708, '2017-06-01 08:38:13', 31.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2431, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92921, 9.07318, '2017-06-01 08:39:13', 38.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2432, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.929, 9.08246, '2017-06-01 08:40:13', 25.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2433, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93441, 9.08942, '2017-06-01 08:41:13', 72.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2434, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.93782, 9.09186, '2017-06-01 08:42:13', 60.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2435, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.92685, 9.1055, '2017-06-01 08:43:13', 100.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2436, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.91416, 9.1155, '2017-06-01 08:44:13', 59.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2437, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.9136, 9.12069, '2017-06-01 08:45:13', 61.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2438, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.90806, 9.12907, '2017-06-01 08:46:13', 70.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2439, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.90032, 9.13512, '2017-06-01 08:47:13', 72.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2440, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8922, 9.14182, '2017-06-01 08:48:13', 25.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2441, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.897, 9.15341, '2017-06-01 08:49:13', 31.4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2442, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.88854, 9.16499, '2017-06-01 08:50:13', 84.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2443, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87991, 9.16748, '2017-06-01 08:51:13', 47.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2444, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87494, 9.1666, '2017-06-01 08:52:13', 26.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2445, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87673, 9.1686, '2017-06-01 08:53:13', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2446, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87577, 9.16901, '2017-06-01 08:54:13', 1.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2447, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87481, 9.1687, '2017-06-01 08:55:48', 41.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2448, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87179, 9.16809, '2017-06-01 08:56:48', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2449, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.87227, 9.16687, '2017-06-01 08:58:11', 17.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2450, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.86792, 9.16718, '2017-06-01 08:59:11', 43.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2451, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85957, 9.16462, '2017-06-01 09:00:11', 53.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2452, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.85343, 9.16339, '2017-06-01 09:01:11', 44.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2453, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.84806, 9.16172, '2017-06-01 09:02:11', 6.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2454, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.84583, 9.1657, '2017-06-01 09:03:11', 36.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2455, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.84655, 9.17571, '2017-06-01 09:04:11', 28.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2456, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83876, 9.17121, '2017-06-01 09:05:11', 78.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2457, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83653, 9.15825, '2017-06-01 09:06:11', 8.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2458, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8357, 9.15769, '2017-06-01 09:07:39', 30.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2459, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82597, 9.15518, '2017-06-01 09:08:39', 74.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2460, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82642, 9.15217, '2017-06-01 09:09:39', 35.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2461, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82604, 9.14803, '2017-06-01 09:10:39', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2462, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.8273, 9.14788, '2017-06-01 09:11:39', 31.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2463, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83221, 9.1479, '2017-06-01 09:12:39', 39, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2464, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83432, 9.15123, '2017-06-01 09:13:39', 0.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2465, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83356, 9.15033, '2017-06-01 09:16:42', 44.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2466, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.83048, 9.14778, '2017-06-01 09:17:42', 27.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2467, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82706, 9.14792, '2017-06-01 09:18:42', 25.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2468, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82614, 9.15255, '2017-06-01 09:19:42', 37.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2469, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.82063, 9.15598, '2017-06-01 09:20:42', 60, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2470, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.81145, 9.15723, '2017-06-01 09:21:42', 27.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2471, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.80686, 9.1602, '2017-06-01 09:22:42', 43, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2472, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.80196, 9.16476, '2017-06-01 09:23:42', 55.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2473, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.79386, 9.16903, '2017-06-01 09:24:42', 80.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2474, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.78696, 9.17969, '2017-06-01 09:25:42', 45.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2475, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.78295, 9.18139, '2017-06-01 09:26:42', 29.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2476, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77986, 9.18331, '2017-06-01 09:27:42', 5.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2477, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77388, 9.18418, '2017-06-01 09:28:42', 43.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2478, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76905, 9.18411, '2017-06-01 09:29:42', 0.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2479, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7681, 9.18399, '2017-06-01 09:31:26', 47.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2480, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76188, 9.1834, '2017-06-01 09:32:26', 24, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2481, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76071, 9.18521, '2017-06-01 09:33:26', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2482, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76093, 9.18383, '2017-06-01 09:37:31', 22.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2483, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7647, 9.18548, '2017-06-01 09:38:31', 30.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2484, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76364, 9.19376, '2017-06-01 09:39:31', 62.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2485, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76454, 9.21105, '2017-06-01 09:40:31', 81.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2486, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77029, 9.22806, '2017-06-01 09:41:31', 81.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2487, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77368, 9.2415, '2017-06-01 09:42:31', 55.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2488, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.77224, 9.24571, '2017-06-01 09:43:31', 33, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2489, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76812, 9.24833, '2017-06-01 09:44:31', 47.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2490, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.76754, 9.24725, '2017-06-01 09:46:26', 9.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2491, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.75889, 9.24383, '2017-06-01 09:47:26', 102.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2492, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.74538, 9.2356, '2017-06-01 09:48:26', 93.2, NULL,
         NULL, NULL);
/*
-- Query: select * from tad_node_geoposition where position_id>2492
LIMIT 0, 5000

-- Date: 2017-06-01 11:45
*/
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2493, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.73555, 9.23305, '2017-06-01 09:49:26', 70.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2494, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.73079, 9.23065, '2017-06-01 09:50:26', 38.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2495, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72687, 9.2263, '2017-06-01 09:51:26', 26.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2496, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72431, 9.22609, '2017-06-01 09:52:26', 31.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2497, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71915, 9.22706, '2017-06-01 09:53:26', 31.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2498, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71568, 9.21899, '2017-06-01 09:54:26', 34.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2499, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7204, 9.21572, '2017-06-01 09:55:26', 21.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2500, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72164, 9.21364, '2017-06-01 09:56:26', 27.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2501, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72291, 9.21101, '2017-06-01 09:57:26', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2502, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72229, 9.21217, '2017-06-01 09:59:16', 45.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2503, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7172, 9.21794, '2017-06-01 10:00:16', 33.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2504, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71656, 9.22323, '2017-06-01 10:01:16', 58.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2505, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.72083, 9.22878, '2017-06-01 10:02:16', 62.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2506, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.71517, 9.2373, '2017-06-01 10:03:16', 62.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2507, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70659, 9.24282, '2017-06-01 10:04:16', 47.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2508, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70787, 9.24804, '2017-06-01 10:05:16', 42, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2509, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70892, 9.25088, '2017-06-01 10:06:16', 5.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2510, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70836, 9.2498, '2017-06-01 10:08:23', 21.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2511, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70597, 9.24402, '2017-06-01 10:09:23', 3.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2512, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7046, 9.25078, '2017-06-01 10:10:23', 0.2, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2513, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70396, 9.25184, '2017-06-01 10:11:59', 49, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2514, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70127, 9.25533, '2017-06-01 10:12:59', 0, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2515, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69944, 9.25167, '2017-06-01 10:13:59', 2.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2516, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69994, 9.2529, '2017-06-01 10:16:18', 34.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2517, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70292, 9.2572, '2017-06-01 10:17:18', 4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2518, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70455, 9.25866, '2017-06-01 10:18:18', 22.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2519, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70441, 9.26623, '2017-06-01 10:19:18', 45.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2520, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70526, 9.27363, '2017-06-01 10:20:18', 43.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2521, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70414, 9.28207, '2017-06-01 10:21:18', 51.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2522, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70655, 9.29961, '2017-06-01 10:22:18', 85.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2523, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7069, 9.31866, '2017-06-01 10:23:18', 73.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2524, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70486, 9.3285, '2017-06-01 10:24:18', 11.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2525, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70176, 9.33083, '2017-06-01 10:25:18', 29.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2526, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7001, 9.3359, '2017-06-01 10:26:18', 34.7, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2527, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69525, 9.3448, '2017-06-01 10:27:18', 65, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2528, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.68796, 9.35353, '2017-06-01 10:28:18', 70.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2529, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67873, 9.36049, '2017-06-01 10:29:18', 64.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2530, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67464, 9.37392, '2017-06-01 10:30:18', 75.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2531, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67088, 9.37555, '2017-06-01 10:31:18', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2532, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67178, 9.37579, '2017-06-01 10:36:51', 28.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2533, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.67637, 9.36907, '2017-06-01 10:37:51', 68.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2534, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.6843, 9.35697, '2017-06-01 10:38:51', 84, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2535, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69286, 9.34666, '2017-06-01 10:39:51', 67.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2536, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.69982, 9.33642, '2017-06-01 10:40:51', 45.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2537, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70306, 9.32962, '2017-06-01 10:41:51', 39, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2538, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70567, 9.32466, '2017-06-01 10:42:51', 57.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2539, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70678, 9.30775, '2017-06-01 10:43:51', 88.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2540, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70418, 9.28663, '2017-06-01 10:44:51', 91, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2541, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70715, 9.27142, '2017-06-01 10:45:51', 67.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2542, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70585, 9.26371, '2017-06-01 10:46:51', 42.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2543, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70412, 9.2596, '2017-06-01 10:47:51', 0.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2544, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70388, 9.25772, '2017-06-01 10:48:51', 32.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2545, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70041, 9.25451, '2017-06-01 10:49:51', 29.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2546, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.7, 9.24674, '2017-06-01 10:50:51', 37.4, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2547, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70303, 9.23877, '2017-06-01 10:51:51', 39.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2548, 2, NULL, '2017-06-01 11:04:40', '0000-00-00 00:00:00', 49.70725, 9.22949, '2017-06-01 10:52:51', 61.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2756, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.73808, 12.74448, '2017-05-31 13:19:57', 0.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2757, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.73798, 12.74456, '2017-05-31 13:52:58', 4.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2758, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.73654, 12.74873, '2017-05-31 13:57:38', 33.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2759, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75605, 12.78798, '2017-05-31 14:01:35', 75.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2760, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.76085, 12.80074, '2017-05-31 14:02:35', 53.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2761, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.76325, 12.81296, '2017-05-31 14:03:35', 63, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2762, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.76481, 12.83122, '2017-05-31 14:04:35', 96.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2763, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.76982, 12.84826, '2017-05-31 14:05:35', 79.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2764, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.77346, 12.86012, '2017-05-31 14:06:35', 53.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2765, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.77479, 12.867, '2017-05-31 14:07:35', 0.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2766, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.77509, 12.86844, '2017-05-31 14:21:44', 46.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2767, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.77627, 12.8728, '2017-05-31 14:22:44', 6.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2768, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.78038, 12.87879, '2017-05-31 14:23:44', 51.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2769, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.78282, 12.88054, '2017-05-31 14:24:44', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2770, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.78313, 12.88193, '2017-05-31 14:29:58', 44.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2771, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.78699, 12.89388, '2017-05-31 14:30:58', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2772, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.79057, 12.90245, '2017-05-31 14:31:58', 85.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2773, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.79528, 12.90511, '2017-05-31 14:32:58', 41.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2774, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8015, 12.91801, '2017-05-31 14:33:58', 107.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2775, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.81401, 12.93437, '2017-05-31 14:34:58', 108.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2776, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.81844, 12.94067, '2017-05-31 14:35:58', 55.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2777, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.82477, 12.92327, '2017-05-31 14:36:58', 86.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2778, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.83268, 12.90877, '2017-05-31 14:37:58', 82.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2779, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.84185, 12.89641, '2017-05-31 14:38:58', 82.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2780, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.84856, 12.89143, '2017-05-31 14:39:58', 26.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2781, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.85501, 12.8909, '2017-05-31 14:40:58', 80.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2782, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.86114, 12.87719, '2017-05-31 14:41:58', 84.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2783, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.86629, 12.86116, '2017-05-31 14:42:58', 79.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2784, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87257, 12.84899, '2017-05-31 14:43:58', 67.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2785, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87534, 12.83356, '2017-05-31 14:44:58', 63.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2786, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87639, 12.81785, '2017-05-31 14:45:58', 71.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2787, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88017, 12.80424, '2017-05-31 14:46:58', 70.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2788, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88627, 12.79831, '2017-05-31 14:47:58', 44, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2789, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88836, 12.79887, '2017-05-31 14:48:58', 20, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2790, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89172, 12.80315, '2017-05-31 14:49:58', 22, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2791, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89082, 12.80371, '2017-05-31 14:50:58', 0, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2792, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89039, 12.80515, '2017-05-31 14:51:58', 0.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2793, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89076, 12.80388, '2017-05-31 14:55:10', 13.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2794, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88988, 12.80071, '2017-05-31 14:56:10', 54.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2795, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88822, 12.7973, '2017-05-31 14:57:10', 9.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2796, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88732, 12.79722, '2017-05-31 15:00:56', 31.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2797, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88328, 12.79112, '2017-05-31 15:01:56', 46.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2798, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87654, 12.77968, '2017-05-31 15:02:56', 76.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2799, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8777, 12.76318, '2017-05-31 15:03:56', 65.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2800, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88315, 12.74924, '2017-05-31 15:04:56', 75.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2801, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88331, 12.74295, '2017-05-31 15:05:56', 21.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2802, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89067, 12.7327, '2017-05-31 15:06:56', 95.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2803, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89735, 12.71691, '2017-05-31 15:07:56', 74.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2804, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89404, 12.71547, '2017-05-31 15:08:56', 85.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2805, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88455, 12.70766, '2017-05-31 15:09:56', 54.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2806, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89007, 12.69311, '2017-05-31 15:10:56', 79.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2807, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89478, 12.67849, '2017-05-31 15:11:56', 72.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2808, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89525, 12.66433, '2017-05-31 15:12:56', 54.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2809, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89984, 12.66283, '2017-05-31 15:13:56', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2810, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.90073, 12.66248, '2017-05-31 15:19:52', 20, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2811, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8996, 12.66273, '2017-05-31 15:20:52', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2812, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.90005, 12.66394, '2017-05-31 15:30:19', 26.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2813, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89421, 12.66437, '2017-05-31 15:31:19', 24.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2814, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88647, 12.65476, '2017-05-31 15:32:19', 82.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2815, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87545, 12.64815, '2017-05-31 15:33:19', 76.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2816, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87397, 12.64165, '2017-05-31 15:34:19', 41.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2817, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88124, 12.63665, '2017-05-31 15:35:19', 43.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2818, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88364, 12.62992, '2017-05-31 15:36:19', 47.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2819, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88684, 12.62413, '2017-05-31 15:37:19', 41.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2820, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88738, 12.62692, '2017-05-31 15:38:19', 0.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2821, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88824, 12.62747, '2017-05-31 15:43:41', 31.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2822, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89237, 12.62695, '2017-05-31 15:44:41', 32.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2823, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89245, 12.62328, '2017-05-31 15:45:41', 3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2824, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89154, 12.62319, '2017-05-31 15:48:44', 15, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2825, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.89143, 12.61133, '2017-05-31 15:49:44', 49.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2826, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88894, 12.60238, '2017-05-31 15:50:44', 49.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2827, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88471, 12.59475, '2017-05-31 15:51:44', 5.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2828, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88643, 12.59219, '2017-05-31 15:52:44', 40, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2829, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88703, 12.58795, '2017-05-31 15:53:44', 51, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2830, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88567, 12.57582, '2017-05-31 15:54:44', 32.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2831, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88533, 12.57442, '2017-05-31 15:55:44', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2832, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88459, 12.57282, '2017-05-31 15:56:44', 21.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2833, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88343, 12.56882, '2017-05-31 15:57:44', 27.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2834, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88242, 12.56753, '2017-05-31 15:58:44', 9.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2835, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88194, 12.56873, '2017-05-31 15:59:44', 0.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2836, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88285, 12.56855, '2017-05-31 16:02:54', 12.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2837, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88245, 12.56496, '2017-05-31 16:03:54', 33.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2838, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.88161, 12.56442, '2017-05-31 16:05:15', 25, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2839, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87975, 12.56433, '2017-05-31 16:06:15', 46, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2840, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.87659, 12.55472, '2017-05-31 16:07:15', 7.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2841, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8749, 12.54949, '2017-05-31 16:08:15', 60.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2842, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8739, 12.54978, '2017-05-31 16:09:15', 15.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2843, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.86391, 12.55824, '2017-05-31 16:10:15', 79.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2844, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.85743, 12.57029, '2017-05-31 16:11:15', 60.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2845, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.85474, 12.57598, '2017-05-31 16:12:15', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2846, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8542, 12.57709, '2017-05-31 16:13:32', 17.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2847, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.85026, 12.58913, '2017-05-31 16:14:32', 104.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2848, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.84083, 12.6093, '2017-05-31 16:15:32', 107, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2849, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.84078, 12.6304, '2017-05-31 16:16:32', 93.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2850, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.84076, 12.65107, '2017-05-31 16:17:32', 86.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2851, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.84208, 12.67051, '2017-05-31 16:18:32', 85.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2852, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.83913, 12.68928, '2017-05-31 16:19:32', 79.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2853, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.83594, 12.70739, '2017-05-31 16:20:32', 79, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2854, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.83313, 12.71658, '2017-05-31 16:21:32', 46.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2855, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.83025, 12.72368, '2017-05-31 16:22:32', 45.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2856, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.8228, 12.72965, '2017-05-31 16:23:32', 80.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2857, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.81163, 12.73877, '2017-05-31 16:24:32', 92, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2858, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.79878, 12.74222, '2017-05-31 16:25:32', 91, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2859, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.78794, 12.75181, '2017-05-31 16:26:32', 49.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2860, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.78054, 12.75077, '2017-05-31 16:27:32', 47, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2861, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.7726, 12.74859, '2017-05-31 16:28:32', 86.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2862, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.76193, 12.74794, '2017-05-31 16:29:32', 79.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2863, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75106, 12.74752, '2017-05-31 16:30:32', 39, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2864, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.74887, 12.75771, '2017-05-31 16:31:32', 52.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2865, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75297, 12.76264, '2017-05-31 16:32:32', 58.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2866, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75631, 12.7677, '2017-05-31 16:33:32', 27.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2867, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75539, 12.76765, '2017-05-31 16:40:52', 27.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2868, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75407, 12.76477, '2017-05-31 16:41:52', 3.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2869, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.7544, 12.76607, '2017-05-31 16:42:52', 1.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2870, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75511, 12.76705, '2017-05-31 16:45:52', 31.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2871, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75589, 12.76839, '2017-05-31 16:46:52', 0.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2872, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75678, 12.76797, '2017-05-31 16:48:31', 28.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2873, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.75365, 12.76332, '2017-05-31 16:49:31', 65.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2874, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.74733, 12.75655, '2017-05-31 16:50:31', 53.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2875, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.74249, 12.74622, '2017-05-31 16:51:31', 34.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2876, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.74352, 12.74448, '2017-05-31 16:52:31', 5.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2877, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.74284, 12.74551, '2017-05-31 17:30:00', 38.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2878, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.73723, 12.74706, '2017-05-31 17:31:00', 29.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2879, 4, NULL, '2017-06-01 11:09:35', '0000-00-00 00:00:00', 48.73807, 12.74428, '2017-05-31 17:32:00', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2883, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12014, 11.61335, '2017-05-31 10:03:17', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2884, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11991, 11.61308, '2017-05-31 10:03:54', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2885, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12004, 11.61323, '2017-05-31 10:05:01', 1.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2886, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12023, 11.61341, '2017-05-31 10:06:12', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2887, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12019, 11.61335, '2017-05-31 11:19:28', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2888, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12024, 11.6134, '2017-05-31 11:20:35', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2889, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12016, 11.61341, '2017-05-31 11:21:41', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2890, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12015, 11.61351, '2017-05-31 11:22:34', 0.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2891, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12012, 11.61346, '2017-05-31 11:23:43', 0.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2892, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12017, 11.6134, '2017-05-31 11:24:52', 1.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2893, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12014, 11.61349, '2017-05-31 11:26:02', 0.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2894, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12017, 11.61351, '2017-05-31 11:27:11', 0.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2895, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12041, 11.61204, '2017-05-31 12:12:49', 17.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2896, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12012, 11.61345, '2017-05-31 12:14:11', 3.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2897, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12014, 11.61336, '2017-05-31 13:41:27', 1.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2898, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.1202, 11.6134, '2017-05-31 13:42:45', 0.5, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2899, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12019, 11.61334, '2017-05-31 13:43:03', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2900, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12006, 11.61334, '2017-05-31 13:44:13', 1, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2901, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11983, 11.61338, '2017-05-31 13:45:38', 7.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2902, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12, 11.61327, '2017-05-31 13:46:32', 3.6, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2903, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12004, 11.61354, '2017-05-31 13:47:41', 3.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2904, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12009, 11.61337, '2017-05-31 13:48:49', 1.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2905, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.1201, 11.61328, '2017-05-31 13:50:44', 0.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2906, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.1201, 11.61344, '2017-05-31 13:51:08', 0.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2907, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12014, 11.61331, '2017-05-31 13:53:01', 2.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2908, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.1202, 11.6134, '2017-05-31 13:54:09', 2, NULL, NULL,
   NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2909, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12004, 11.61339, '2017-05-31 13:54:36', 1.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2910, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11919, 11.61393, '2017-05-31 14:37:05', 13.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2911, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12005, 11.61348, '2017-05-31 14:38:32', 2.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2912, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12096, 11.61366, '2017-05-31 14:48:31', 15.4, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2913, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12009, 11.61327, '2017-05-31 14:50:55', 6.9, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2914, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11918, 11.61352, '2017-05-31 15:01:55', 27.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2915, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12192, 11.61406, '2017-05-31 15:02:58', 0, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2916, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11856, 11.61303, '2017-05-31 15:03:58', 22.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2917, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.1203, 11.61349, '2017-05-31 15:04:58', 10.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2918, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12126, 11.61381, '2017-05-31 15:08:30', 17.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2919, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12018, 11.61327, '2017-05-31 15:09:30', 0.6, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2920, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12206, 11.614, '2017-05-31 15:20:22', 8.3, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2921, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12111, 11.61388, '2017-05-31 15:21:35', 3.7, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2922, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.123, 11.61554, '2017-05-31 15:22:35', 38.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2923, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12054, 11.61356, '2017-05-31 15:23:35', 8.8, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2924, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11982, 11.61269, '2017-05-31 15:30:57', 10.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2925, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11934, 11.61398, '2017-05-31 15:46:57', 16.2, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2926, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12031, 11.61326, '2017-05-31 15:47:57', 2.3, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2927, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.11991, 11.6114, '2017-05-31 16:06:47', 35.5, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES (2928, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12029, 11.61334, '2017-05-31 16:07:47', 4, NULL,
              NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2929, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12101, 11.6124, '2017-05-31 16:58:49', 15.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2930, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12027, 11.61324, '2017-05-31 16:59:57', 5.1, NULL,
         NULL, NULL);
INSERT INTO `tad_node_geoposition` (`position_id`, `user_id`, `node_id`, `ts_created`, `ts_updated`, `latitude`, `longitude`, `position_datetime`, `speed`, `bearing`, `altitude`, `accuracy`)
VALUES
  (2931, 3, NULL, '2017-06-01 11:10:07', '0000-00-00 00:00:00', 52.12015, 11.61341, '2017-05-31 17:04:23', 1.2, NULL,
         NULL, NULL);
