// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package groovy.org.apache.cloudstack.ldap

import org.apache.cloudstack.api.ServerApiException
import org.apache.cloudstack.api.command.LdapUserSearchCmd
import org.apache.cloudstack.api.response.LdapUserResponse
import org.apache.cloudstack.ldap.LdapManager
import org.apache.cloudstack.ldap.LdapUser
import org.apache.cloudstack.ldap.NoLdapUserMatchingQueryException

class LdapSearchUserCmdSpec extends spock.lang.Specification {
    def "Test successful response from execute"() {
        given:
        def ldapManager = Mock(LdapManager)
        List<LdapUser> users = new ArrayList()
        users.add(new LdapUser("rmurphy", "rmurphy@test.com", "Ryan", "Murphy", "cn=rmurphy,dc=cloudstack,dc=org"))
        ldapManager.searchUsers(_) >> users
        LdapUserResponse response = new LdapUserResponse("rmurphy", "rmurphy@test.com", "Ryan", "Murphy", "cn=rmurphy,dc=cloudstack,dc=org")
        ldapManager.createLdapUserResponse(_) >> response
        def ldapUserSearchCmd = new LdapUserSearchCmd(ldapManager)
        when:
        ldapUserSearchCmd.execute()
        then:
        ldapUserSearchCmd.responseObject.getResponses().size() != 0
    }

    def "Test successful empty response from execute"() {
        given:
        def ldapManager = Mock(LdapManager)
        ldapManager.searchUsers(_) >> {throw new NoLdapUserMatchingQueryException()}
        def ldapUserSearchCmd = new LdapUserSearchCmd(ldapManager)
        when:
        ldapUserSearchCmd.execute()
        then:
        ldapUserSearchCmd.responseObject.getResponses().size() == 0
    }

    def "Test getEntityOwnerId is 0"() {
        given:
        def ldapManager = Mock(LdapManager)
        def ldapUserSearchCmd = new LdapUserSearchCmd(ldapManager)
        when:
        long ownerId = ldapUserSearchCmd.getEntityOwnerId()
        then:
        ownerId == 1
    }

    def "Test successful return of getCommandName"() {
        given:
        def ldapManager = Mock(LdapManager)
        def ldapUserSearchCmd = new LdapUserSearchCmd(ldapManager)
        when:
        String commandName = ldapUserSearchCmd.getCommandName()
        then:
        commandName == "ldapuserresponse"
    }
}
