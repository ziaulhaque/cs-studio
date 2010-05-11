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
package org.csstudio.alarm.service.internal;

import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;

import java.util.List;

import javax.annotation.Nonnull;
import javax.naming.directory.SearchControls;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.ContentModel;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;

/**
 * Alarm configuration service implementation
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 11.05.2010
 */
public class AlarmConfigurationServiceImpl implements IAlarmConfigurationService {

    private final ILdapService _ldapService;

    /**
     * Constructor.
     * @param ldapService access to LDAP
     */
    public AlarmConfigurationServiceImpl(@Nonnull final ILdapService ldapService) {
        _ldapService = ldapService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ContentModel<LdapEpicsAlarmCfgObjectClass> retrieveInitialContentModel(@Nonnull final List<String> facilityNames) {

        final ContentModel<LdapEpicsAlarmCfgObjectClass> model =
            new ContentModel<LdapEpicsAlarmCfgObjectClass>(LdapEpicsAlarmCfgObjectClass.ROOT);

        for (final String facility : facilityNames) {
            final LdapSearchResult result =
                _ldapService.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(EFAN_FIELD_NAME, facility,
                                                                                         OU_FIELD_NAME, EPICS_ALARM_CFG_FIELD_VALUE),
                                                               "(objectClass=*)",
                                                               SearchControls.SUBTREE_SCOPE);

            model.addSearchResult(result);
        }
        return model;
    }

}