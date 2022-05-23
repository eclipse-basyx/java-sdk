/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.authorization;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.RoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Simple attribute based implementation for {@link IAASRegistryAuthorizer}.
 *
 * @author wege
 */
public class SimpleAbacAASRegistryAuthorizer implements IAASRegistryAuthorizer {
  protected AbacRuleChecker abacRuleChecker;
  protected RoleAuthenticator roleAuthenticator;

  public SimpleAbacAASRegistryAuthorizer(final AbacRuleChecker abacRuleChecker, final RoleAuthenticator roleAuthenticator) {
    this.abacRuleChecker = abacRuleChecker;
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public void enforceRegisterAas(IIdentifier aasId) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceRegisterSubmodel(IIdentifier aasId, IIdentifier smId) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUnregisterAas(IIdentifier aasId) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUnregisterSubmodel(IIdentifier aasId, IIdentifier smId) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public AASDescriptor enforceLookupAas(IIdentifier aasId, AASDescriptor aas) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
    return aas;
  }

  @Override
  public SubmodelDescriptor enforceLookupSubmodel(IIdentifier aasId, IIdentifier smId, SubmodelDescriptor sm) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
    return sm;
  }
}
