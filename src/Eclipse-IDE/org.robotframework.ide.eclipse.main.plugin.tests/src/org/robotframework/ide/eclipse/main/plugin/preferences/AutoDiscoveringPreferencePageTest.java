/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.ui.IWorkbench;
import org.junit.Rule;
import org.junit.Test;
import org.robotframework.ide.eclipse.main.plugin.RedPreferences;
import org.robotframework.red.junit.ShellProvider;

public class AutoDiscoveringPreferencePageTest {

    @Rule
    public ShellProvider shellProvider = new ShellProvider();

    @Test
    public void initDoesNothing() {
        final IWorkbench workbench = mock(IWorkbench.class);

        final AutoDiscoveringPreferencePage page = new AutoDiscoveringPreferencePage();
        page.init(workbench);

        verifyZeroInteractions(workbench);
    }

    @Test
    public void checkIfEditorForRecursiveModulesAdditionIsDefined() throws Exception {
        final AutoDiscoveringPreferencePage page = new AutoDiscoveringPreferencePage();
        page.createControl(shellProvider.getShell());

        final List<FieldEditor> editors = FieldEditorPreferencePageHelper.getEditors(page);
        assertThat(editors).hasSize(1);

        final FieldEditor editor = editors.get(0);
        assertThat(editor).isInstanceOf(BooleanFieldEditor.class);
        assertThat(editor.getPreferenceName())
                .isEqualTo(RedPreferences.PROJECT_MODULES_RECURSIVE_ADDITION_ON_VIRTUALENV_ENABLED);
    }
}
