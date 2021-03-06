/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.table.ARobotSectionTable;
import org.rf.ide.core.testdata.model.table.TableHeader;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.robotframework.ide.eclipse.main.plugin.RedImages;

import com.google.common.base.Objects;
import com.google.common.collect.Range;

public abstract class RobotSuiteFileSection implements RobotFileInternalElement {

    private final String name;

    private final RobotElement parent;

    protected final List<RobotFileInternalElement> elements = Collections
            .synchronizedList(new ArrayList<RobotFileInternalElement>());

    private final ARobotSectionTable sectionTable;

    RobotSuiteFileSection(final RobotSuiteFile parent, final String name, final ARobotSectionTable sectionTable) {
        this.parent = parent;
        this.name = name;
        this.sectionTable = sectionTable;
    }

    public abstract void link();

    @Override
    public ARobotSectionTable getLinkedElement() {
        return sectionTable;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        } else if (obj.getClass() == getClass()) {
            final RobotSuiteFileSection other = (RobotSuiteFileSection) obj;
            return name.equals(other.name) && this.parent == other.parent;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, parent);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public ImageDescriptor getImage() {
        return RedImages.getRobotCasesFileSectionImage();
    }

    public List<Position> getPositions() {
        final List<Position> positions = newArrayList();
        if (sectionTable != null) {
            for (final TableHeader<? extends ARobotSectionTable> header : sectionTable.getHeaders()) {
                final FilePosition begin = header.getBeginPosition();
                final FilePosition end = header.getEndPosition();

                if (!begin.isNotSet() && !end.isNotSet()) {
                    positions.add(new Position(begin.getOffset(), end.getOffset() - begin.getOffset()));
                }
            }
        }
        return positions;
    }

    @Override
    public Position getPosition() {
        return new Position(0);
    }

    @Override
    public DefinitionPosition getDefinitionPosition() {
        final RobotToken tableHeader = sectionTable.getHeaders().get(0).getTableHeader();
        return new DefinitionPosition(tableHeader.getFilePosition(), tableHeader.getText().length());
    }

    @Override
    public Optional<? extends RobotElement> findElement(final int offset) {
        for (final RobotFileInternalElement element : getChildren()) {
            final Optional<? extends RobotElement> candidate = element.findElement(offset);
            if (candidate.isPresent()) {
                return candidate;
            }
        }
        for (final TableHeader<? extends ARobotSectionTable> header : sectionTable.getHeaders()) {
            if (Range.closed(header.getBeginPosition().getOffset(), header.getEndPosition().getOffset())
                    .contains(offset)) {
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    @Override
    public OpenStrategy getOpenRobotEditorStrategy() {
        return new PageActivatingOpeningStrategy(RobotSuiteFileSection.this);
    }

    @Override
    public RobotElement getParent() {
        return parent;
    }

    @Override
    public List<? extends RobotFileInternalElement> getChildren() {
        return elements;
    }

    @Override
    public int getIndex() {
        return parent == null ? -1 : parent.getChildren().indexOf(this);
    }

    public RobotElement findChild(final String name) {
        for (final RobotElement element : elements) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public RobotSuiteFile getSuiteFile() {
        return (RobotSuiteFile) this.getParent();
    }

    public int getHeaderLine() {
        return sectionTable.getHeaders().get(0).getTableHeader().getLineNumber();
    }
}
