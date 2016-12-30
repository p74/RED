/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project.build.causes;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMarkerResolution;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSettingsSection;
import org.robotframework.ide.eclipse.main.plugin.project.build.AdditionalMarkerAttributes;
import org.robotframework.ide.eclipse.main.plugin.project.build.RobotProblem;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.AddLibraryToRedXmlFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.ChangeImportedPathFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.ChangeToFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.CreateResourceFileFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.DefineGlobalVariableInConfigFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.DocumentToDocumentationWordFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.MetadataKeyInSameColumnFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.SettingMetaSynonimFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.SettingSimpleWordReplacer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.TableHeaderDepracatedAliasesReplacer;

public enum GeneralSettingsProblem implements IProblemCause {
    UNKNOWN_SETTING {

        @Override
        public String getProblemDescription() {
            return "Unknown '%s' setting";
        }
    },
    EMPTY_SETTING {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.EMPTY_SETTINGS;
        }

        @Override
        public String getProblemDescription() {
            return "Empty setting '%s'";
        }
    },
    UNSUPPORTED_SETTING {

        @Override
        public String getProblemDescription() {
            return "The setting '%s' is not supported inside %s file";
        }
    },
    DUPLICATED_SETTING {

        @Override
        public String getProblemDescription() {
            return "The setting '%s' is duplicated";
        }
    },
    MISSING_LIBRARY_NAME {

        @Override
        public String getProblemDescription() {
            return "Empty setting '%s'. Specify name or path of library to import";
        }
    },
    MISSING_RESOURCE_NAME {

        @Override
        public String getProblemDescription() {
            return "Empty setting '%s'. Specify path of resource file to import";
        }
    },
    MISSING_VARIABLES_NAME {

        @Override
        public String getProblemDescription() {
            return "Empty setting '%s'. Specify path of variable file to import";
        }
    },
    IMPORT_PATH_PARAMETERIZED {

        @Override
        public String getProblemDescription() {
            return "The import name/path '%s' is parameterized. Some of used parameters cannot be resolved."
                    + " Use Variable mappings in red.xml for parameter resolution";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String nameOrPath = marker.getAttribute(AdditionalMarkerAttributes.NAME, null);
            return DefineGlobalVariableInConfigFixer.createFixers(nameOrPath);
        }
    },
    IMPORT_PATH_USES_SINGLE_WINDOWS_SEPARATORS {

        @Override
        public String getProblemDescription() {
            return "Windows paths are not supported. Use global variable '${/}' or Linux-like '/' path separators.";
        }
    },
    IMPORT_PATH_ABSOLUTE {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.ABSOLUTE_PATH;
        }

        @Override
        public String getProblemDescription() {
            return "Path '%s' is absolute. It is preferred to use relative paths";
        }
    },
    IMPORT_PATH_RELATIVE_VIA_MODULES_PATH {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.IMPORT_PATH_RELATIVE_VIA_MODULES_PATH;
        }

        @Override
        public String getProblemDescription() {
            return "Path '%s' is relative to location from python Modules Search Path (sys.path). Points to '%s'";
        }
    },
    IMPORT_PATH_OUTSIDE_WORKSPACE {

        @Override
        public String getProblemDescription() {
            return "Path '%s' points to location outside your workspace";
        }
    },
    NON_EXISTING_LIBRARY_IMPORT {

        @Override
        public String getProblemDescription() {
            return "Unknown '%s' library. Try to use Quick Fix (Ctrl+1) or add library to red.xml for proper validation";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final IFile suiteFile = (IFile) marker.getResource();
            final String name = marker.getAttribute(AdditionalMarkerAttributes.NAME, null);
            final String path = marker.getAttribute(AdditionalMarkerAttributes.PATH, null);
            final boolean isPath = path != null;
            final boolean isName = name != null;

            final List<IMarkerResolution> fixers = new ArrayList<>();
            fixers.add(new AddLibraryToRedXmlFixer(isPath ? path : name, isPath));
            if (isName) {
                fixers.addAll(ChangeToFixer.createFixers(RobotProblem.getRegionOf(marker),
                        new SimilaritiesAnalyst().provideSimilarLibraries(suiteFile, name)));
            }
            return fixers;
        }
    },
    NON_EXISTING_RESOURCE_IMPORT {

        @Override
        public String getProblemDescription() {
            return "Resource import '%s' is invalid: file does not exist. Try to use Quick Fix (Ctrl+1)";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final IPath path = Path.fromPortableString(marker.getAttribute(AdditionalMarkerAttributes.PATH, null));
            final List<IMarkerResolution> fixers = new ArrayList<>();
            fixers.add(CreateResourceFileFixer.createFixer(path.toPortableString(), marker));
            fixers.addAll(ChangeImportedPathFixer.createFixersForSameFile((IFile) marker.getResource(), path));
            return fixers;
        }
    },
    INVALID_RESOURCE_IMPORT {

        @Override
        public String getProblemDescription() {
            return "Resource import '%s' is invalid%s";
        }
    },
    HTML_RESOURCE_IMPORT {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.HTML_FORMAT;
        }

        @Override
        public String getProblemDescription() {
            return "HTML is valid resource type for Robot although RED does not support html files.";
        }
    },
    NON_EXISTING_VARIABLES_IMPORT {

        @Override
        public String getProblemDescription() {
            return "Resource import '%s' is invalid: file does not exist. Check file name and path.";
        }
    },
    SETTING_ARGUMENTS_NOT_APPLICABLE {

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is not applicable for arguments: %s. %s";
        }
    },
    DUPLICATED_TEMPLATE_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' keyword as template";
        }
    },
    DUPLICATED_SUITE_SETUP_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' keyword as suite setup";
        }
    },
    DUPLICATED_SUITE_TEARDOWN_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' keyword as suite teardown";
        }
    },
    DUPLICATED_TEST_SETUP_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' keyword as test setup";
        }
    },
    DUPLICATED_TEST_TEARDOWN_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' keyword as test teardown";
        }
    },
    DUPLICATED_TEST_TIMEOUT_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' as test timeout";
        }
    },
    DUPLICATED_FORCE_TAGS_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' merged elements as force tags";
        }
    },
    DUPLICATED_DEFAULT_TAGS_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use '%s' merged elements as default tags";
        }
    },
    DUPLICATED_DOCUMENTATION_28 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DUPLICATED_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is duplicated. Robot will try to use merged elements as documentation";
        }
    },
    METADATA_SETTING_JOINED_WITH_KEY_IN_COLUMN_29 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DEPRECATED_API;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' in the same column with Metadata Key is removed in Robot Framework 3.0";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new MetadataKeyInSameColumnFixer());
        }
    },
    METADATA_SETTING_JOINED_WITH_KEY_IN_COLUMN_30 {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' in the same column with Metadata Key is removed in Robot Framework 3.0";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new MetadataKeyInSameColumnFixer());
        }
    },
    META_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is deprecated from Robot Framework 3.0. Use Metadata syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new SettingMetaSynonimFixer());
        }
    },
    DOCUMENT_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is deprecated from Robot Framework 3.0. Use Documentation syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new DocumentToDocumentationWordFixer(RobotSettingsSection.class));
        }
    },
    SUITE_PRECONDITION_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is deprecated from Robot Framework 3.0. Use Suite Setup syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(
                    new SettingSimpleWordReplacer(RobotSettingsSection.class, "Suite Precondition", "Suite Setup"));
        }
    },
    SUITE_POSTCONDITION_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is deprecated from Robot Framework 3.0. Use Suite Teardown syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(
                    new SettingSimpleWordReplacer(RobotSettingsSection.class, "Suite Postcondition", "Suite Teardown"));
        }
    },
    TEST_PRECONDITION_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is deprecated from Robot Framework 3.0. Use Test Setup syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(
                    new SettingSimpleWordReplacer(RobotSettingsSection.class, "Test Precondition", "Test Setup"));
        }
    },
    TEST_POSTCONDITION_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' is deprecated from Robot Framework 3.0. Use Test Teardown syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(
                    new SettingSimpleWordReplacer(RobotSettingsSection.class, "Test Postcondition", "Test Teardown"));
        }
    },
    METADATA_TABLE_HEADER_SYNONIM {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Table header '%s' is deprecated from Robot Framework 3.0. Use *** Settings *** syntax instead of current.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(
                    new TableHeaderDepracatedAliasesReplacer(RobotSettingsSection.class, "Metadata", "Settings"));
        }
    },
    LIBRARY_WITH_NAME_NOT_UPPER_CASE_COMBINATION {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.REMOVED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Setting '%s' not in upper case is deprecated from Robot Framework 3.0.\nUse WITH NAME instead.";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(
                    ChangeToFixer.createFixers(RobotProblem.getRegionOf(marker), Arrays.asList("WITH NAME")));
        }
    },
    VARIABLE_AS_KEYWORD_USAGE_IN_SETTING {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.VARIABLE_AS_KEYWORD_USAGE;
        }

        @Override
        public String getProblemDescription() {
            return "Variable '%s' is given as keyword name.";
        }
    };

    @Override
    public boolean hasResolution() {
        return false;
    }

    @Override
    public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
        return newArrayList();
    }

    @Override
    public ProblemCategory getProblemCategory() {
        return ProblemCategory.RUNTIME_ERROR;
    }

    @Override
    public String getEnumClassName() {
        return GeneralSettingsProblem.class.getName();
    }
}
