/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.restapi.server.security;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.restapi.server.HasRestApiService;
import org.openksavi.sponge.restapi.server.RestApiInvalidUsernamePasswordServerException;
import org.openksavi.sponge.util.Initializable;

public interface RestApiSecurityService extends HasRestApiService, Initializable {

    UserAuthentication authenticateUser(String username, String password) throws RestApiInvalidUsernamePasswordServerException;

    UserAuthentication authenticateAnonymous(User anonymous);

    void openSecurityContext(UserAuthentication userAuthentication);

    void closeSecurityContext();

    boolean canCallAction(UserContext userContext, ActionAdapter actionAdapter);

    boolean canSendEvent(UserContext userContext, String eventName);

    boolean canSubscribeEvent(UserContext userContext, String eventName);

    boolean canUseKnowledgeBase(UserContext userContext, KnowledgeBase knowledgeBase);
}
