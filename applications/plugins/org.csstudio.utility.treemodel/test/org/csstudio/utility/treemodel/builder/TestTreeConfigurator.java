/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.utility.treemodel.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.utility.treemodel.ITreeNodeConfiguration;

/**
 * Test enum configuring the test xml files.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 01.06.2010
 */
public enum TestTreeConfigurator implements ITreeNodeConfiguration<TestTreeConfigurator> {

    ROOT("ou", "root"),

    /**
     * The facility object class (efan).
     */
    FACILITY("efan", "facility"),

    /**
     * The component object class (ecom).
     */
    COMPONENT("ecom", "component"),

    /**
     * The IOC object class (econ).
     */
    IOC("econ", "ioc"),

    /**
     * The record object class (eren).
     */
    RECORD("eren", "record");


    private static final Map<String, TestTreeConfigurator> CACHE_BY_NAME =
        new HashMap<String, TestTreeConfigurator>();


    static {
        // Initialize the _nestedClass attribute
        RECORD._nestedClasses = Collections.emptySet();

        IOC._nestedClasses.add(RECORD);

        COMPONENT._nestedClasses.add(IOC);

        FACILITY._nestedClasses.add(COMPONENT);

        ROOT._nestedClasses.add(FACILITY);

        for (final TestTreeConfigurator oc : TestTreeConfigurator.values()) {
            CACHE_BY_NAME.put(oc.getNodeTypeName(), oc);
        }
    }


    /**
     * The name of this object class in the directory.
     */
    private final String _description;

    /**
     * The name of the attribute to use for the RDN of entries of this class in
     * the directory.
     */
    private final String _nodeTypeName;


    /**
     * The object class of a container nested within a container of this object
     * class. <code>null</code> if this object class is not a container or if
     * there is no standard nested class for this class.
     */
    private Set<TestTreeConfigurator> _nestedClasses = new HashSet<TestTreeConfigurator>();

    /**
     * Creates a new object class.
     *
     * @param nodeTypeName
     *            the name of the attribute to use for the RDN.
     * @param description
     *            the description of this tree component.
     */
    //CHECKSTYLE:OFF
    private TestTreeConfigurator(final String nodeTypeName,
                                         final String description) {
    //CHECKSTYLE:ON
        _nodeTypeName = nodeTypeName;
        _description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDescription() {
        return _description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getNodeTypeName() {
        return _nodeTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Set<TestTreeConfigurator> getNestedContainerClasses() {
        return _nestedClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public TestTreeConfigurator getNodeTypeByNodeTypeName(@Nonnull final String name) {
        return getNodeTypeByNodeNameStatic(name);
    }

    @CheckForNull
    private static TestTreeConfigurator getNodeTypeByNodeNameStatic(@Nonnull final String name) {
        return CACHE_BY_NAME.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getRootTypeName() {
        return ROOT.getNodeTypeName();
    }

}

