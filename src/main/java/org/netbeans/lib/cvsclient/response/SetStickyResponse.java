/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.lib.cvsclient.response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Handles the Set-sticky response.
 * 
 * @author Milos Kleint
 */

class SetStickyResponse implements Response {

    /**
     * 
     */
    private static final long serialVersionUID = -801003095576041714L;

    /**
     * Process the data for the response.
     * 
     * @param dis
     *            the data inputstream allowing the client to read the server's
     *            response. Note that the actual response name has already been
     *            read and the input stream is positioned just before the first
     *            argument, if any.
     */
    public void process(final LoggedDataInputStream dis, final ResponseServices services) throws ResponseException {
        PrintWriter w = null;
        try {
            final String localPath = dis.readLine();

            final String repositoryPath = dis.readLine();
            final String tag = dis.readLine();
            // System.err.println("Repository path is: " + repositoryPath);
            // services.updateAdminData(localPath, repositoryPath, null);
            String absPath = services.convertPathname(localPath, repositoryPath);

            if (services.getGlobalOptions().isExcluded(new File(absPath))) {
                return;
            }

            absPath = absPath + "/CVS"; // NOI18N
            final File absFile = new File(absPath);
            if (absFile.exists()) {
                final File tagFile = new File(absPath, "Tag"); // NOI18N
                if ("THEAD".equals(tag) | "NHEAD".equals(tag)) { // NOI18N
                    tagFile.delete();
                } else {
                    w = new PrintWriter(new BufferedWriter(new FileWriter(tagFile)));
                    // we write out the sticky tag/date
                    w.println(tag);
                }
            }
        } catch (final IOException e) {
            throw new ResponseException(e);
        } finally {
            if (w != null) {
                w.close();
            }
        }
    }

    /**
     * Is this a terminal response, i.e. should reading of responses stop after
     * this response. This is true for responses such as OK or an error response
     */
    public boolean isTerminalResponse() {
        return false;
    }
}
