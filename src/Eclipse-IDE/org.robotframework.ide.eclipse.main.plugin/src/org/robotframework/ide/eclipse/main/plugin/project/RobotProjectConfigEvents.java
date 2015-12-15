/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project;


public class RobotProjectConfigEvents {

    public static final String ROBOT_CONFIG_ENV_LOADING_STARTED = "robot/redxml/env/loading_started";

    public static final String ROBOT_CONFIG_ENV_LOADED = "robot/redxml/env/loaded";


    public static final String ROBOT_CONFIG_VAR_MAP_DETAIL_CHANGED = "robot/redxml/detail/varmap/changed/*";
    public static final String ROBOT_CONFIG_VAR_MAP_NAME_CHANGED = "robot/redxml/detail/varmap/changed/name";
    public static final String ROBOT_CONFIG_VAR_MAP_VALUE_CHANGED = "robot/redxml/detail/varmap/changed/name";
    public static final String ROBOT_CONFIG_VAR_MAP_STRUCTURE_CHANGED = "robot/redxml/structural/varmap/changed";

    public static final String ROBOT_CONFIG_VAR_FILE_DETAIL_CHANGED = "robot/redxml/detail/varfile/changed/*";
    public static final String ROBOT_CONFIG_VAR_FILE_PATH_CHANGED = "robot/redxml/detail/varfile/changed/path";
    public static final String ROBOT_CONFIG_VAR_FILE_STRUCTURE_CHANGED = "robot/redxml/structural/varfile/changed";


    public static final String ROBOT_CONFIG_LIBRARIES_STRUCTURE_CHANGED = "robot/redxml/structural/libs/changed";

}
