/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

/*
 * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.engys.util.filechooser.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryAuthStore implements AuthStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryAuthStore.class);

    private Map<UserAuthenticationInfo, UserAuthenticationDataWrapper> map;

    public MemoryAuthStore() {
        map = new HashMap<UserAuthenticationInfo, UserAuthenticationDataWrapper>();
    }

    @Override
    public UserAuthenticationDataWrapper getUserAuthenticationData(UserAuthenticationInfo info) {
        return map.get(info);
    }

    @Override
    public Collection<UserAuthenticationDataWrapper> getUserAuthenticationDatas(String protocol, String host) {
        List<UserAuthenticationDataWrapper> list = new ArrayList<UserAuthenticationDataWrapper>();
        for (UserAuthenticationInfo key : map.keySet()) {
            if (StringUtils.equalsIgnoreCase(key.getProtocol(), protocol) && StringUtils.equalsIgnoreCase(key.getHost(), host)) {
                list.add(map.get(key));
            }
        }
        return list;
    }

    @Override
    public void add(UserAuthenticationInfo aInfo, UserAuthenticationDataWrapper authenticationData) {
        LOGGER.debug("Adding auth info {}://{}@{}", new Object[] { aInfo.getProtocol(), aInfo.getUser(), aInfo.getHost() });
        map.put(aInfo, authenticationData);
    }

    @Override
    public void remove(UserAuthenticationInfo authenticationInfo) {
        map.remove(authenticationInfo);
    }

    @Override
    public Collection<UserAuthenticationInfo> getAll() {
        return new ArrayList<UserAuthenticationInfo>(map.keySet());
    }
    
    @Override
    public void clear() {
    	map.clear();
	}

}
